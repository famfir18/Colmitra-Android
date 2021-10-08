package com.softnesia.colmitra.service.api

import android.content.Context
import android.net.Uri
import com.google.gson.JsonSyntaxException
import com.softnesia.colmitra.BuildConfig
import com.softnesia.colmitra.util.FileUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Response
import java.io.*

/**
 * Created by Dark on 13/03/2018.
 */
object RetrofitHelper {
    fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, descriptionString)
    }

    fun createFilePartFromUri(context: Context, fileUri: Uri): RequestBody {
        val filePath: String? = FileUtil.getFilePath(context, fileUri)
        val file = File(filePath!!)
        return RequestBody.create(MediaType.parse("image/*"), file)
    }

    fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part {
        val filePath: String? = FileUtil.getFilePath(context, fileUri)
        val file = File(filePath!!)
        return prepareFilePart(partName, file)
    }

    fun prepareFilePart(partName: String, file: File?): MultipartBody.Part {
        if (file == null) throw RuntimeException("File is null")
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            MediaType.parse("image/*"),
            file
        )
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun writeResponseBodyToDisk(body: ResponseBody, file: File): Boolean {
        return try {
            //            File file = new File(context.getExternalFilesDir(null) + File.separator + fileName);
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    @JvmStatic
    fun requestBodyToString(request: RequestBody?): String {
        return try {
            val buffer = Buffer()

            if (request == null) return ""

            request.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }

    @JvmStatic
    fun parseError(response: Response<*>): ApiResponse<*> {
        val converter =
            ApiService.generateRetrofit(BuildConfig.BASE_URL)
                .responseBodyConverter<ApiResponse<*>>(
                    ApiResponse::class.java,
                    arrayOfNulls(0)
                )

        return try {
            converter.convert(response.errorBody()!!)!!
        } catch (e: IOException) {
            return ApiResponse<Any?>()
        } catch (e: JsonSyntaxException) {
            return ApiResponse<Any?>()
        }
    }
}