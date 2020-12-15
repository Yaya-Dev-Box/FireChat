package com.fruitPunchSamurai.firechat.repos

import android.net.Uri
import com.fruitPunchSamurai.firechat.models.LastMessage
import com.fruitPunchSamurai.firechat.models.Message
import com.fruitPunchSamurai.firechat.models.User
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase

class FireRepo {

    private var usersRepo = FireUsersRepo()
    private var rdb = Firebase.database
    private var messagesRepo = FireMessagesRepo()
    private var storageRepo = StorageRepo()

    suspend fun getAllUsers() = usersRepo.getAllUsers()

    suspend fun addUser(user: User) {
        usersRepo.addUser(user)
    }

    suspend fun getUser(id: String?): DocumentSnapshot = usersRepo.getUser(id)

    suspend fun getImageURI(mediaID: String, currentUserID: String, receiverID: String) =
        storageRepo.getImageURI(mediaID, currentUserID, receiverID)


    fun setLastMessageAsRead(currentUserID: String, contactID: String) {
        messagesRepo.setLastMessageAsRead(currentUserID, contactID)
    }

    suspend fun addTextMessageAndLastMessage(
        message: Message,
        lastMessage: LastMessage,
        currentUsername: String,
    ) {
        val pushValue = rdb.getReference("Messages").push().key ?: return

        messagesRepo.addMessageAndLastMessage(
            message,
            lastMessage,
            currentUsername,
            pushValue
        )
    }

    /**Leave uri null if there is no media to be added*/
    suspend fun addImageMessageAndLastMessage(
        message: Message,
        lastMessage: LastMessage,
        currentUsername: String,
        uri: Uri? = null
    ) {
        val pushValue = rdb.getReference("Messages").push().key ?: return
        if (uri == null) return

        message.apply { mediaID = "$pushValue.jpg" }

        storageRepo.addChatImageToStorage(
            uri,
            pushValue,
            message.ownerID,
            lastMessage.contactID
        )

        messagesRepo.addMessageAndLastMessage(
            message,
            lastMessage,
            currentUsername,
            pushValue
        )
    }


}