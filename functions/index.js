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
        // Update owned book owner usernames
        const newUsername = change.after.data().username;
        const oldUsername = change.before.data().username;
        if (newUsername !== oldUsername) {
            const batch = db.batch();
            for (const bookRef of change.after.data().ownedBooks) {
                batch.update(bookRef, {ownerUsername: newUsername});
            }
            await batch.commit();
        }
    });
