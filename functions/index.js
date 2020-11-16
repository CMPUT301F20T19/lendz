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

        const ownedBooksBatch = db.batch();
        const requestsBatch = db.batch();

        if (newUsername !== oldUsername) {
            // Update owned book owner usernames
            for (const bookRef of change.after.data().ownedBooks) {
                ownedBooksBatch.update(bookRef, {ownerUsername: newUsername});
            }

            // Update requester username in requested books
            for (const requestRef of change.after.data().requests) {
                requestsBatch.update(requestRef, {requesterUsername: newUsername});
            }
        }

        if (newFullName !== oldFullName) {
            // Update requester full name in requested books
            for (const requestRef of change.after.data().requests) {
                requestsBatch.update(requestRef, {requesterFullName: newFullName});
            }
        }

        await ownedBooksBatch.commit();
        await requestsBatch.commit();
    });

exports.onBookCreate = functions.firestore
    .document('books/{bookId}')
    .onCreate(async (change, context) => {
        const ownerRef = change.after.data().owner;
        const ownerData = await ownerRef.get();

        // Add ownerUsername to book
        bookRef.set({
            ownerUsername: ownerData.username
        }, {merge: true});

        // Load ownedBooks of owner
        const newOwnedBooks = ownerData.ownedBooks ? ownerData.ownedBooks : [];

        // Add new book to ownedBooks
        newOwnedBooks.push(change.after.ref);

        // Update ownedBooks
        ownerRef.set({
            ownedBooks: newOwnedBooks
        }, {merge: true});
    });

exports.onBookDelete = functions.firestore
    .document('books/{bookId}')
    .onDelete(async (change, context) => {
        const ownerRef = change.after.data().owner;

        // Load ownedBooks of owner
        const ownerData = await ownerRef.get();
        const newOwnedBooks = ownerData.ownedBooks ? ownerData.ownedBooks : [];

        // Remove deleted book from ownedBooks
        const bookIndex = newOwnedBooks.indexOf(change.before.ref);
        if (bookIndex === -1) {
            return;
        }
        newOwnedBooks.splice(bookIndex, 1);

        // Update ownedBooks
        ownerRef.set({
            ownedBooks: newOwnedBooks
        }, {merge: true});
    });

async function deletePendingRequest(bookRef, requestRef) {
    // Load pendingRequests of book
    const bookData = await bookRef.get();
    const newPendingRequests = bookData.pendingRequests ? bookData.pendingRequests : [];

    // Remove declined request from pendingRequests
    const requestIndex = newPendingRequests.indexOf(change.after.ref);
    if (requestIndex !== -1) {
        newPendingRequests.splice(requestIndex, 1);
    }

    bookRef.set({
        pendingRequests: newPendingRequests,
    }, {merge: true});
}

exports.onRequestCreate = functions.firestore
    .document('requests/{requestId}')
    .onCreate(async (change, context) => {
        const bookRef = change.after.data().book;

        // Load pendingRequests of book
        const bookData = await bookRef.get();
        const newPendingRequests = bookData.pendingRequests ? bookData.pendingRequests : [];

        // Add new request to pendingRequests
        newPendingRequests.push(change.after.ref);

        bookRef.set({
            pendingRequests: newPendingRequests
        }, {merge: true});
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

                // Set acceptedRequest to accepted request
                bookRef.set({
                    acceptedRequest: change.after.ref
                }, {merge: true});
            }
        }
    });