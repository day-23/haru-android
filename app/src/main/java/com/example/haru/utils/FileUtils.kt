package com.example.haru.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File


object FileUtils {
    val TAG = "FileUtils"
    private val DEBUG = false // Set to true to enable logging

    fun getFile(context: Context, uri: Uri?): File? {
        if (uri != null) {
            val path = getPath(context, uri)
            if (path != null && isLocal(path)) {
                return File(path)
            }
        }
        return null
    }

    fun isLocal(url: String?): Boolean {
        return if ((url != null) && !url.startsWith("http://") && !url.startsWith("https://")) {
            true
        } else false
    }

    fun getPath(context: Context, uri: Uri): String? {
        if (DEBUG) Log.d(
            "$TAG File -",
            (((((("Authority: " + uri.getAuthority()) +
                    ", Fragment: " + uri.getFragment()) +
                    ", Port: " + uri.getPort()) +
                    ", Query: " + uri.getQuery()) +
                    ", Scheme: " + uri.getScheme()) +
                    ", Host: " + uri.getHost()) +
                    ", Segments: " + uri.getPathSegments().toString()
        )
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return "${Environment.getExternalStorageDirectory()}/${split[1]}"
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if (("image" == type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if (("video" == type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if (("audio" == type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.getLastPathSegment() else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
            return uri.getPath()
        }
        return null
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return ("com.google.android.apps.photos.content" == uri.getAuthority())
    }


    fun isExternalStorageDocument(uri: Uri): Boolean {
        return ("com.android.externalstorage.documents" == uri.getAuthority())
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return ("com.android.providers.downloads.documents" == uri.getAuthority())
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            if(uri != null) {
                cursor = context.getContentResolver().query(
                    uri, projection, selection, selectionArgs,
                    null
                )

                if (cursor != null && cursor.moveToFirst()) {
                    if (DEBUG) DatabaseUtils.dumpCursor(cursor)
                    val column_index: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return ("com.android.providers.media.documents" == uri.getAuthority())
    }
}