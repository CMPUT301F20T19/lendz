// Functions
const functions = require('firebase-functions');

// Required to access Cloud Firestore and Cloud Messaging
const admin = require('firebase-admin');

// Cloud Firestore
const db = admin.firestore();

exports.viewRequestTestBefore = functions.https.onCall((data, context) => {
    // Create test book
    db.doc('books/viewRequestTestBook').set({
        description: {
            author: "boyonda",
            description: "the witcher",
            isbn: "2536732323232",
            title: "Chelsea Book"
        },
        keywords: ["chelsea", "book", "boyonda", "the", "witcher", "2536732323232"],
        owner: db.doc('users/dwpqY6Wnr4MTavg9pJkvfjFadJ73'),
        ownerUsername: "WoodieFrank101",
        photo: null,
        status: 1,
        pendingRequests: [db.doc('requests/viewRequestTestRequest')],
        pendingRequesters: [db.doc('users/dqdzdaUMthZxuyo43LLpeCfkvjb2')]
    });

    // Create pending request for book
    db.doc('requests/viewRequestTestRequest').set({
        book: db.doc('books/viewRequestTestBook'),
        bookPhotoUrl: null,
        bookTitle: "Chelsea book",
        location: null,
        ownerUsername: "WoodieFrank101",
        requester: db.doc('users/dqdzdaUMthZxuyo43LLpeCfkvjb2'),
        requesterFullName: "James Harden",
        requesterUsername: "jamesHarden",
        status: 0,
        timestamp: 1606715798873
    });

    return true;
});

exports.viewRequestTestAfter = functions.https.onCall((data, context) => {
    // Delete test book
    db.doc('books/viewRequestTestBook').delete();
});