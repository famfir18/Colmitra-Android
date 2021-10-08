package com.softnesia.colmitra.util

import android.app.DownloadManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import com.softnesia.colmitra.BuildConfig
import java.io.*


object FileUtil {
    fun getNameFromUrl(url: String): String {
        return Uri.parse(url).lastPathSegment!!
    }

    const val DOCUMENTS_DIR = "documents"
    fun getFileName(context: Context, uri: Uri?): String? {
        if (uri == null) return null
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor =
                context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getFilePath(context: Context, fileUri: Uri): String? {
        var uri = fileUri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
                context.applicationContext,
                uri
            )
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                return getFilePathDownloadsDocument(context, uri)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                    split[1]
                )
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA
            )
            var cursor: Cursor?
            try {
                cursor = context.contentResolver
                    .query(uri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
                Log.e("File", "Error getting file path: " + e.message)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun getFilePathDownloadsDocument(context: Context, uri: Uri): String? {
        val id = DocumentsContract.getDocumentId(uri)
        if (id != null && id.startsWith("raw:")) {
            return id.substring(4)
        }
        val contentUriPrefixesToTry = arrayOf(
            "content://downloads/public_downloads",
            "content://downloads/my_downloads",
            "content://downloads/all_downloads"
        )
        for (contentUriPrefix in contentUriPrefixesToTry) {
            val contentUri = ContentUris.withAppendedId(
                Uri.parse(contentUriPrefix),
                java.lang.Long.valueOf(id)
            )
            try {
                val path = getDataColumn(context, contentUri, null, null)
                if (path != null) {
                    return path
                }
            } catch (e: Exception) {
            }
        }
        // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
        val fileName = getFileName(context, uri)
        val cacheDir = getDocumentCacheDir(context)
        val file = generateFileName(fileName, cacheDir)
        var destinationPath: String? = null
        if (file != null) {
            destinationPath = file.absolutePath
            saveFileFromUri(context, uri, destinationPath)
        }
        return destinationPath
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getMimeFromFileName(fileName: String): String {
        val i = fileName.lastIndexOf('.')
        return if (i > 0) {
            "application/" + fileName.substring(i + 1)
        } else "*/*"
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri, selection: String?, selectionArgs: Array<String?>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                if (BuildConfig.DEBUG) DatabaseUtils.dumpCursor(cursor)
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun generateFileName(name: String?, directory: File?): File? {
        if (name == null) return null

        var file = File(directory, name)
        if (file.exists()) {
            var fileName = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                val _name = "$fileName($index)$extension"
                file = File(directory, _name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            return null
        }
        return file
    }

    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`!!.read(buf)
            do {
                bos.write(buf)
            } while (`is`.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun addFileToDownload(context: Context, file: File) {
        val mimeType = getMimeFromFileName(file.name)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // You can add more columns.. Complete list of columns can be found at
            // https://developer.android.com/reference/android/provider/MediaStore.Downloads
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Downloads.TITLE, file.name);
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, file.name)
            contentValues.put(MediaStore.Downloads.MIME_TYPE, mimeType)
            contentValues.put(MediaStore.Downloads.SIZE, file.length())

            // If you downloaded to a specific folder inside "Downloads" folder
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)

            // Insert into the database
            val database = context.contentResolver
            database.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.addCompletedDownload(
                file.name, file.name, true,
                mimeType, file.absolutePath, file.length(), true
            )
        }
    }
}