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
                ownedBooksBatch.update(bookRef, { ownerUsername: newUsername });
            }
            await ownedBooksBatch.commit();
        }

        if (newUsername !== oldUsername || newFullName !== oldFullName) {
            // Update requester username and full name in requested books

            const requestsBatch = db.batch();
            // Get requests made by this user
            const requestsSnapshot = await db.collection('requests').where('requester', '==', change.after.ref).get();

            requestsSnapshot.forEach((doc) => {
                requestsBatch.update(doc.ref, { requesterUsername: newUsername, requesterFullName: newFullName });
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
        bookRef.set({
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
        const requests = data.pendingRequests ? data.pendingRequests : [];
        if (data.acceptedRequest) {
            requests.push(data.acceptedRequest);
        }

        const requestsBatch = db.batch();
        for (const request of requests) {
            requestsBatch.update(request, { bookTitle: data.description.title, bookPhotoUrl: data.photo });
        }
        await requestsBatch.commit();
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
        const bookIndex = newOwnedBooks.indexOf(snapshot.ref);
        if (bookIndex === -1) {
            functions.logger.error('book not found in ownedBooks');
            return;
        }
        newOwnedBooks.splice(bookIndex, 1);

        // Update ownedBooks
        ownerRef.set({
            ownedBooks: newOwnedBooks
        }, { merge: true });
    });

async function deletePendingRequest(bookRef, requestRef) {
    // Load pendingRequests of book
    const bookData = (await bookRef.get()).data();
    const newPendingRequests = bookData.pendingRequests ? bookData.pendingRequests : [];
    const newPendingRequesters = bookData.pendingRequesters ? bookData.pendingRequesters : [];

    // Remove declined request from pendingRequests
    const requestIndex = newPendingRequests.indexOf(change.after.ref);
    if (requestIndex === -1) {
        functions.logger.error('request not found in pendingRequests');
        return;
    }
    newPendingRequests.splice(requestIndex, 1);
    newPendingRequesters.splice(requestIndex, 1);

    bookRef.set({
        pendingRequests: newPendingRequests,
        pendingRequesters: newPendingRequesters
    }, { merge: true });
}

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
            if (newStatus === 1) {
                // If new status is DECLINED, then remove request from pendingRequests on the book
                const bookRef = change.after.data().book;
                deletePendingRequest(bookRef, change.after.ref);
            } else if (newStatus === 2) {
                // If new status is ACCEPTED, then move request to acceptedRequest on the book
                const bookRef = change.after.data().book;
                deletePendingRequest(bookRef, change.after.ref);

                // Set acceptedRequest and acceptedRequester
                bookRef.set({
                    acceptedRequest: change.after.ref,
                    acceptedRequester: change.after.data().requester
                }, { merge: true });
            }
        }
    });