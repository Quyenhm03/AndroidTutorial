package com.eco.musicplayer.audioplayer.music.roomdb.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder.buildQueryString
import android.net.Uri
import com.eco.musicplayer.audioplayer.music.roomdb.AppDatabase

class UserPostProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.eco.musicplayer.audioplayer.music.roomdb.contentprovider"
        val BASE_URI: Uri = Uri.parse("content://$AUTHORITY")

        const val PATH_USERS = "users"
        const val PATH_POSTS = "posts"
        const val PATH_USER_WITH_POSTS = "user_with_posts"

        val USERS_URI: Uri = Uri.withAppendedPath(BASE_URI, PATH_USERS)
        val POSTS_URI: Uri = Uri.withAppendedPath(BASE_URI, PATH_POSTS)
        val USER_WITH_POSTS_URI: Uri = Uri.withAppendedPath(BASE_URI, PATH_USER_WITH_POSTS)

        private const val USERS = 100
        private const val USER_ID = 101
        private const val POSTS = 200
        private const val POST_ID = 201
        private const val USER_WITH_POSTS = 300

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH_USERS, USERS)
            addURI(AUTHORITY, "$PATH_USERS/#", USER_ID)
            addURI(AUTHORITY, PATH_POSTS, POSTS)
            addURI(AUTHORITY, "$PATH_POSTS/#", POST_ID)
            addURI(AUTHORITY, PATH_USER_WITH_POSTS, USER_WITH_POSTS)
        }

        //MIME Types
        const val MIME_TYPE_USER_DIR = "vnd.android.cursor.dir/vnd.$AUTHORITY.user"
        const val MIME_TYPE_USER_ITEM = "vnd.android.cursor.item/vnd.$AUTHORITY.user"
        const val MIME_TYPE_POST_DIR = "vnd.android.cursor.dir/vnd.$AUTHORITY.post"
        const val MIME_TYPE_POST_ITEM = "vnd.android.cursor.item/vnd.$AUTHORITY.post"
    }

    private lateinit var  database: AppDatabase

    override fun onCreate(): Boolean {
        database = AppDatabase.getInstance(context!!)
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = database.openHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            USERS -> {
                db.delete("users", selection, selectionArgs)
            }
            USER_ID -> {
                val id = uri.lastPathSegment
                db.delete("users", "id = ?", arrayOf(id))
            }
            POSTS -> {
                db.delete("posts", selection, selectionArgs)
            }
            POST_ID -> {
                val id = uri.lastPathSegment
                db.delete("posts", "id = ?", arrayOf(id))
            }
            else -> 0
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            USERS -> MIME_TYPE_USER_DIR
            USER_ID -> MIME_TYPE_USER_ITEM
            POSTS -> MIME_TYPE_POST_DIR
            POST_ID -> MIME_TYPE_POST_ITEM
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null) {
            return null
        }

        val db = database.openHelper.writableDatabase
        val id = when (uriMatcher.match(uri)) {
            USERS -> db.insert("users", SQLiteDatabase.CONFLICT_REPLACE, values)
            POSTS -> db.insert("posts", SQLiteDatabase.CONFLICT_REPLACE, values)
            else -> -1L
        }

        if (id > 0) {
            val resultUri = ContentUris.withAppendedId(uri, id)
            // auto update data when it change
            context?.contentResolver?.notifyChange(uri, null)
            return resultUri
        }
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = database.openHelper.readableDatabase

        val cursor = when (uriMatcher.match(uri)) {
            USERS -> {
                val query = buildQueryString(
                    table = "users",
                    projection = projection,
                    selection = selection,
                    sortOrder = sortOrder ?: "id DESC"
                )
                db.query(query, selectionArgs ?: emptyArray())
            }
            USER_ID -> {
                val id = uri.lastPathSegment
                val query = buildQueryString(
                    table = "users",
                    projection = projection,
                    selection = "id = ?",
                    sortOrder = null
                )
                db.query(query, arrayOf(id))
            }
            POSTS -> {
                val query = buildQueryString(
                    table = "posts",
                    projection = projection,
                    selection = selection,
                    sortOrder = sortOrder ?: "created_at DESC"
                )
                db.query(query, selectionArgs ?: emptyArray())
            }
            POST_ID -> {
                val id = uri.lastPathSegment
                val query = buildQueryString(
                    table = "posts",
                    projection = projection,
                    selection = "id = ?",
                    sortOrder = null
                )
                db.query(query, arrayOf(id))
            }
            else -> null
        }

        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    private fun buildQueryString(
        table: String,
        projection: Array<out String>?,
        selection: String?,
        sortOrder: String?
    ): String {
        val columns = projection?.joinToString(", ") ?: "*"
        val query = StringBuilder("SELECT $columns FROM $table")

        if (!selection.isNullOrEmpty()) {
            query.append(" WHERE $selection")
        }

        if (!sortOrder.isNullOrEmpty()) {
            query.append(" ORDER BY $sortOrder")
        }

        return query.toString()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        if (values == null) {
            return 0
        }

        val db = database.openHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            USERS -> {
                db.update("users", SQLiteDatabase.CONFLICT_REPLACE, values, selection, selectionArgs)
            }
            USER_ID -> {
                val id = uri.lastPathSegment
                db.update("users", SQLiteDatabase.CONFLICT_REPLACE, values, "id = ?", arrayOf(id))
            }
            POSTS -> {
                db.update("posts", SQLiteDatabase.CONFLICT_REPLACE, values, selection, selectionArgs)
            }
            POST_ID -> {
                val id = uri.lastPathSegment
                db.update("posts", SQLiteDatabase.CONFLICT_REPLACE, values, "id = ?", arrayOf(id))
            }
            else -> 0
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun bulkInsert(uri: Uri, values: Array<out ContentValues>): Int {
        val db = database.openHelper.writableDatabase
        var count = 0

        db.beginTransaction()
        try {
            when (uriMatcher.match(uri)) {
                USERS -> {
                    values.forEach { value ->
                        val id = db.insert("users", SQLiteDatabase.CONFLICT_REPLACE, value)
                        if (id > 0) {
                            count++
                        }
                    }
                }
                POSTS -> {
                    values.forEach { value ->
                        val id = db.insert("posts", SQLiteDatabase.CONFLICT_REPLACE, value)
                        if (id > 0) {
                            count++
                        }
                    }
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }
}