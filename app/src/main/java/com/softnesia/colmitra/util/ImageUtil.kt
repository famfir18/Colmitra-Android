package com.softnesia.colmitra.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64OutputStream
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.net.toFile
import com.softnesia.colmitra.px
import java.io.*
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow

/**
 * Created by Dark on 22/01/2018.
 */
object ImageUtil {
    /**
     * Compress an image by maxWidth as compression scale while still persisting the Exif data
     *
     * @param context
     * @param fileUri
     * @param maxWidth
     * @return temporary file
     */
    fun decodeAsFile(context: Context, fileUri: Uri, maxWidth: Int): File? {
        val filePath = FileUtil.getFilePath(context, fileUri)
        val fileName = FileUtil.getFileName(context, fileUri)

        if (filePath != null) {
            val file = File(filePath)
            val bitmap = decodeFile(file, maxWidth)
            val tempFile = bitmapToTemporaryFile(context, bitmap!!, fileName!!)
            copyExif(filePath, tempFile!!.path)

            return tempFile
        }

        return null
    }

    fun bitmapToTemporaryFile(context: Context, bitmap: Bitmap, fileName: String): File? {
        var file: File? = null

        try {
            file = File(context.cacheDir, "temp_$fileName")
            val outStream: OutputStream
            outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    fun mergeBitmaps(overlay: Bitmap, bitmap: Bitmap): Bitmap {
        val height = bitmap.height
        val width = bitmap.width
        val combined = Bitmap.createBitmap(width, height, bitmap.config)
        val canvas = Canvas(combined)
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height
        canvas.drawBitmap(bitmap, Matrix(), null)
        val centreX = (canvasWidth - overlay.width) / 2
        val centreY = (canvasHeight - overlay.height) / 2
        canvas.drawBitmap(overlay, centreX.toFloat(), centreY.toFloat(), null)
        return combined
    }

    fun bitmapToByteArray(bmp: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        bmp.recycle()
        return byteArray
    }

    fun decodeFile(context: Context, fileUri: Uri, maxSizeInBytes: Int): Bitmap? {
        val filePath = FileUtil.getFilePath(context, fileUri)

        if (filePath != null) {
            val file = File(filePath)
            return decodeFile(file, maxSizeInBytes)
        }

        return null
    }

    fun decodeFile(f: File, maxWidth: Int): Bitmap? {
        var b: Bitmap? = null
        try {
            //Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            var fis = FileInputStream(f)
            BitmapFactory.decodeStream(fis, null, o)
            fis.close()
            var scale = 1
            if (o.outWidth > maxWidth || o.outHeight > maxWidth) {
                scale = 2.0.pow(
                    ceil(
                        ln(maxWidth / max(o.outHeight, o.outWidth).toDouble()) / ln(0.5)
                    )
                ).toInt()
            }

            //Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            fis = FileInputStream(f)
            b = BitmapFactory.decodeStream(fis, null, o2)
            fis.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return b
    }

    fun encodeToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun decodeFromString(encodedBitmap: String): Bitmap {
        val imageAsBytes = Base64.decode(encodedBitmap.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        var drawable = AppCompatResources.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun displayCircularImageWithBorder(
        context: Context,
        bitmap: Bitmap,
        imageView: ImageView,
        borderWidth: Int
    ) {
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
        circularBitmapDrawable.isCircular = true
        imageView.setImageDrawable(circularBitmapDrawable)
        val borderDp: Int = borderWidth.px
        imageView.setPadding(borderDp, borderDp, borderDp, borderDp)
    }

    fun getByteArrayInputStream(photoPath: String, compressRate: Int): ByteArrayInputStream {
        val bmp = BitmapFactory.decodeFile(photoPath)
        val out = ByteArrayOutputStream()
        // Compress to bitmap by compressRate% quality
        bmp.compress(Bitmap.CompressFormat.JPEG, compressRate, out)
        bmp.recycle()
        val ba = out.toByteArray()
        try {
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ByteArrayInputStream(ba)
    }

    fun getByteArrayInputStream(bmp: Bitmap, compressRate: Int): ByteArrayInputStream {
        val out = ByteArrayOutputStream()
        // Compress to bitmap by compressRate% quality
        bmp.compress(Bitmap.CompressFormat.JPEG, compressRate, out)
        bmp.recycle()
        val ba = out.toByteArray()
        try {
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ByteArrayInputStream(ba)
    }

    fun animateImage(context: Context, imageView: ImageView, animationResource: Int) {
        val myFadeInAnimation = AnimationUtils.loadAnimation(context, animationResource)
        imageView.startAnimation(myFadeInAnimation)
    }

    fun getImageRotation(fileUri: Uri): Int {
        var exif: ExifInterface? = null
        var exifRotation = 0
        try {
            exif = ExifInterface(fileUri.path!!)
            exifRotation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return if (exif == null) 0 else exifToDegrees(
            exifRotation
        )
    }

    private fun exifToDegrees(rotation: Int): Int {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90) return 90
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) return 180
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) return 270
        return 0
    }

    fun getBitmapRotatedByDegree(bitmap: Bitmap, rotationDegree: Int): Bitmap {
        val matrix = Matrix()
        matrix.preRotate(rotationDegree.toFloat())
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun copyExif(oldPath: String?, newPath: String?) {
        val oldExif: ExifInterface
        try {
            oldExif = ExifInterface(oldPath)
            val attributes = arrayOf(
                ExifInterface.TAG_APERTURE,
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_DATETIME_DIGITIZED,
                ExifInterface.TAG_EXPOSURE_TIME,
                ExifInterface.TAG_FLASH,
                ExifInterface.TAG_FOCAL_LENGTH,
                ExifInterface.TAG_GPS_ALTITUDE,
                ExifInterface.TAG_GPS_ALTITUDE_REF,
                ExifInterface.TAG_GPS_DATESTAMP,
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LATITUDE_REF,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_GPS_LONGITUDE_REF,
                ExifInterface.TAG_GPS_PROCESSING_METHOD,
                ExifInterface.TAG_GPS_TIMESTAMP,
                ExifInterface.TAG_ISO,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.TAG_SUBSEC_TIME,
                ExifInterface.TAG_SUBSEC_TIME_DIG,
                ExifInterface.TAG_SUBSEC_TIME_ORIG,
                ExifInterface.TAG_WHITE_BALANCE
            )
            val newExif = ExifInterface(newPath)
            for (i in attributes.indices) {
                val value = oldExif.getAttribute(attributes[i])
                if (value != null) {
                    newExif.setAttribute(attributes[i], value)
                }
            }
            newExif.saveAttributes()
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String): File? {
        val filename = "Signature_${System.currentTimeMillis()}.png"
        val write: (OutputStream) -> Boolean = {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DCIM}/$albumName"
                )
            }

            val uri: Uri?
            context.contentResolver.let {
                uri = it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let { uri ->
                    it.openOutputStream(uri)?.let(write)
                }
            }

            return uri?.toFile()
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + File.separator + albumName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, filename)
            val succeed = write(FileOutputStream(image))

            return if (succeed) image else null
        }
    }

    // https://stackoverflow.com/a/54532684/2178568
    fun convertImageFileToBase64(imageFile: File): String {
        return FileInputStream(imageFile).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                    inputStream.copyTo(base64FilterStream)
                    base64FilterStream.close() // This line is required, see comments
                    outputStream.toString()
                }
            }
        }
    }
}