// Functions
const functions = require('firebase-functions');

// Required to access Cloud Firestore
const admin = require('firebase-admin');
admin.initializeApp();

// Cloud Firestore
const db = admin.firestore();

exports.onUserUpdate = functions.firestore
    .document('users/{userId}')
    .onUpdate(async (change, context) => {
        const newUsername = change.after.data().username;
        const oldUsername = change.before.data().username;
        const newFullName = change.after.data().fullName;
        const oldFullName = change.before.data().fullName;

        if (newUsername === oldUsername && newFullName === oldFullName) {
            return;
        }

        if (newUsername !== oldUsername) {
            // Update owned book owner usernames

            const ownedBooksBatch = db.batch();
            const ownedBooks = change.after.data().ownedBooks ? change.after.data().ownedBooks : [];
            for (const bookRef of ownedBooks) {
                ownedBooksBatch.set(bookRef, { ownerUsername: newUsername }, { merge: true });
            }
            await ownedBooksBatch.commit();
        }

        if (newUsername !== oldUsername || newFullName !== oldFullName) {
            // Update requester username and full name in requested books

            const requestsBatch = db.batch();
            // Get requests made by this user
            const requestsSnapshot = await db.collection('requests').where('requester', '==', change.after.ref).get();

            requestsSnapshot.forEach((doc) => {
                requestsBatch.set(doc.ref, { requesterUsername: newUsername, requesterFullName: newFullName }, { merge: true });
            });
            await requestsBatch.commit();
        }
    });

exports.onBookCreate = functions.firestore
    .document('books/{bookId}')
    .onCreate(async (snapshot, context) => {
        const ownerRef = snapshot.data().owner;
        const ownerData = (await ownerRef.get()).data();

        // Add ownerUsername to book
        snapshot.ref.set({
            ownerUsername: ownerData.username
        }, { merge: true });

        // Load ownedBooks of owner
        const newOwnedBooks = ownerData.ownedBooks ? ownerData.ownedBooks : [];

        // Add new book to ownedBooks
        newOwnedBooks.push(snapshot.ref);

        // Update ownedBooks
        ownerRef.set({
            ownedBooks: newOwnedBooks
        }, { merge: true });
    });

exports.onBookUpdate = functions.firestore
    .document('books/{bookId}')
    .onUpdate(async (change, context) => {
        const data = change.after.data();

        // Update cached bookTitle and bookPhotoUrl of requests for this book
        const pendingRequests = data.pendingRequests ? data.pendingRequests : [];
        if (data.acceptedRequest) {
            pendingRequests.push(data.acceptedRequest);
        }

        const requestsBatch = db.batch();
        for (const request of pendingRequests) {
            requestsBatch.set(request, { bookTitle: data.description.title, bookPhotoUrl: data.photo }, { merge: true });
        }
        await requestsBatch.commit();

        // Handle scans
        if (data.ownerScanned === true && data.borrowerScanned === true) {
            const borrowerData = (await data.acceptedRequester.get()).data();
            const borrowedBooks = borrowerData.borrowedBooks ? borrowerData.borrowedBooks : [];
            let newStatus;
            if (data.status == 3) { // ACCEPTED
                // Initiate borrow
                borrowedBooks.push(change.after.ref);
                change.after.ref.set({
                    status: 2, // AVAILABLE
                    ownerScanned: false,
                    borrowerScanned: false
                }, { merge: true });
            } else if (data.status == 2) { // BORROWED
                // Initiate return
                newStatus = 0; // AVAILABLE
                let foundBook = false;
                for (let i = 0; i < borrowedBooks.length && !foundBook; i++) {
                    if (change.after.ref.id === borrowedBooks[i].id) {
                        borrowedBooks.splice(i, 1);
                        foundBook = true;
                    }
                }
                if (!foundBook) {
                    functions.logger.error('returning book not found in borrowedBooks');
                }
                change.after.ref.set({
                    status: 0, // AVAILABLE
                    ownerScanned: false,
                    borrowerScanned: false,
                    acceptedRequest: null,
                    acceptedRequester: null,
                    acceptedRequesterUsername: null
                }, { merge: true });
                data.acceptedRequest.delete();
            } else {
                functions.logger.error('unknown status after scan ' + data.status);
            }
            data.acceptedRequester.set({
                borrowedBooks: borrowedBooks
            }, { merge: true });
        } else {
            // Determine the status and set it
            let status = 0; // AVAILABLE
            if (data.acceptedRequester) {
                const requesterData = (await data.acceptedRequester.get()).data();
                for (const bookRef of requesterData.borrowedBooks) {
                    if (bookRef.id === change.after.ref.id) {
                        status = 2; // BORROWED
                        break;
                    }
                }
                if (status !== 2) {
                    status = 3; // ACCEPTED
                }
            } else if (data.pendingRequests && data.pendingRequests.length > 0) {
                status = 1; // REQUESTED
            }
            change.after.ref.set({
                status: status
            }, { merge: true });
        }
    });

exports.onBookDelete = functions.firestore
    .document('books/{bookId}')
    .onDelete(async (snapshot, context) => {
        // TODO: Decline pending requests for this book
        // TODO: Deal with accepted request for this book if it exists

        const ownerRef = snapshot.data().owner;

        // Load ownedBooks of owner
        const ownerData = (await ownerRef.get()).data();
        const newOwnedBooks = ownerData.ownedBooks ? ownerData.ownedBooks : [];

        // Remove deleted book from ownedBooks
        for (let i = 0; i < newOwnedBooks.length; i++) {
            if (snapshot.ref.id === newOwnedBooks[i].id) {
                newOwnedBooks.splice(i, 1);
                break;
            }
        }

        // Update ownedBooks
        ownerRef.set({
            ownedBooks: newOwnedBooks
        }, { merge: true });
    });

exports.onRequestCreate = functions.firestore
    .document('requests/{requestId}')
    .onCreate(async (snapshot, context) => {
        // Load book data
        const bookRef = snapshot.data().book;
        const bookSnapshot = await bookRef.get();
        const bookData = bookSnapshot.data();

        // Get pendingRequests and pendingRequesters of the book
        const newPendingRequests = bookData.pendingRequests ? bookData.pendingRequests : [];
        const newPendingRequesters = bookData.pendingRequesters ? bookData.pendingRequesters : [];

        // Add new request to pendingRequests and pendingRequesters
        newPendingRequests.push(snapshot.ref);
        newPendingRequesters.push(snapshot.data().requester);

        bookRef.set({
            pendingRequests: newPendingRequests,
            pendingRequesters: newPendingRequesters
        }, { merge: true });

        // Get requester data
        const requesterRef = snapshot.data().requester;
        const requesterSnapshot = await requesterRef.get();
        const requesterData = requesterSnapshot.data();

        // Get owner data
        const ownerRef = bookData.owner;
        const ownerSnapshot = await ownerRef.get();
        const ownerData = ownerSnapshot.data();

        // Set requesterUsername, requesterFullName, ownerUsername, bookTitle, and bookPhotoUrl
        snapshot.ref.set({
            requesterUsername: requesterData.username,
            requesterFullName: requesterData.fullName,
            ownerUsername: ownerData.username,
            bookTitle: bookData.description.title,
            bookPhotoUrl: bookData.photo
        }, { merge: true });

        // Create notification for owner
        const notificationData = {
            type: 0, // BookRequested
            notifiedUser: ownerRef,
            timestamp: snapshot.data().timestamp,
            request: snapshot.ref
        };
        db.collection('notifications').add(notificationData);
    });

exports.onRequestUpdate = functions.firestore
    .document('requests/{requestId}')
    .onUpdate(async (change, context) => {
        const oldStatus = change.before.data().status;
        const newStatus = change.after.data().status;
        if (oldStatus !== newStatus) {
            // Load pendingRequests of book
            const bookRef = change.after.data().book;
            const bookSnapshot = await bookRef.get();
            const bookData = bookSnapshot.data();

            const newPendingRequests = bookData.pendingRequests ? bookData.pendingRequests : [];
            const newPendingRequesters = bookData.pendingRequesters ? bookData.pendingRequesters : [];

            if (newStatus === 1) {
                // If new status is DECLINED, then remove the request from pendingRequests on the book
                for (let i = 0; i < newPendingRequests.length; i++) {
                    if (change.after.ref.id === newPendingRequests[i].id) {
                        newPendingRequests.splice(i, 1);
                        newPendingRequesters.splice(i, 1);
                        break;
                    }
                }

                bookRef.set({
                    pendingRequests: newPendingRequests,
                    pendingRequesters: newPendingRequesters
                }, { merge: true });

                // Create notification for requester
                const notificationData = {
                    type: 1, // RequestAcknowledged
                    notifiedUser: change.after.data().requester,
                    timestamp: change.after.data().timestamp,
                    request: change.after.ref
                };
                db.collection('notifications').add(notificationData);
            } else if (newStatus === 2) {
                // If new status is ACCEPTED, then set the acceptedRequest on the book and decline and clear pendingRequests

                // Decline all other pending requests
                const pendingRequestsBatch = db.batch();
                for (const requestRef of newPendingRequests) {
                    if (requestRef.id === change.after.ref.id) {
                        // Skip the accepted request
                        continue;
                    }
                    pendingRequestsBatch.set(requestRef, {
                        status: 1 // DECLINED
                    }, { merge: true });
                }
                pendingRequestsBatch.commit();

                // Get username of accepted requester
                const requesterData = (await change.after.data().requester.get()).data();

                bookRef.set({
                    acceptedRequest: change.after.ref,
                    acceptedRequester: change.after.data().requester,
                    acceptedRequesterUsername: requesterData.username,
                    pendingRequests: [],
                    pendingRequesters: []
                }, { merge: true });

                // Create notification for requester
                const notificationData = {
                    type: 1, // RequestAcknowledged
                    notifiedUser: change.after.data().requester,
                    timestamp: change.after.data().timestamp,
                    request: change.after.ref
                };
                db.collection('notifications').add(notificationData);
            }
        }
    });

exports.onRequestDelete = functions.firestore
    .document('requests/{requestId}')
    .onDelete(async (snapshot, context) => {
        // Remove all associated notifications
        const batch = db.batch();
        const notifications = await db.collection('notifications').where('request', '==', snapshot.ref).get();
        for (const notification of notifications.data()) {
            batch.delete(notification.ref);
        }
        batch.commit();
    });