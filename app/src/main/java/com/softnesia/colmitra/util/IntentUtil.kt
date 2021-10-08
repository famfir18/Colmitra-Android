package com.softnesia.colmitra.util

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import java.net.URISyntaxException


/**
 * Created by Dark on 25/07/2017.
 */
object IntentUtil {
    fun openAppRating(context: Context) {
        // you can also use BuildConfig.APPLICATION_ID
        val appId = context.packageName
        val rateIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$appId")
        )
        var marketFound = false

        // find all applications able to handle our rateIntent
        val otherApps = context.packageManager.queryIntentActivities(rateIntent, 0)
        for (otherApp in otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName == "com.android.vending") {
                val otherAppActivity = otherApp.activityInfo
                val componentName = ComponentName(
                    otherAppActivity.applicationInfo.packageName,
                    otherAppActivity.name
                )
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.component = componentName
                context.startActivity(rateIntent)
                marketFound = true
                break
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appId")
            )
            context.startActivity(webIntent)
        }
    }

    fun openAppSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    fun openDial(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    }

    fun openEmail(context: Context, addresses: Array<String?>?, subject: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "Choose client"))
        }
    }

    fun openFacebook(context: Context, username: String) {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/$username")
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebook.com/$username")
                )
            )
        }
    }

    fun openGoogleMap(context: Context, lat: Double, lng: Double) {
        val intentUri = Uri.parse("geo:$lat,$lng")
        val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        }
    }

    fun openInstagramByUrl(context: Context, url: String) {
        val userId = url.substring(url.lastIndexOf("/") + 1)
        openInstagramById(context, userId)
    }

    fun openInstagramById(context: Context, userId: String) {
        var uri = Uri.parse("http://instagram.com/_u/$userId")
        var intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.instagram.android")
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            uri = Uri.parse("http://instagram.com/$userId")
            intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    }

    fun openLineProfile(context: Context, userId: String) {
        val url = "http://line.me/ti/p/~$userId"
        var intent: Intent? = Intent()
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        context.startActivity(intent)
    }

    fun openLocationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    fun openMessage(context: Context, phoneNumber: String, message: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
        intent.putExtra("sms_body", message)
        context.startActivity(intent)
    }

    fun openTwitter(context: Context, screenName: String) {
        var intent: Intent?
        try {
            // get the Twitter app if possible
            context.packageManager.getPackageInfo("com.twitter.android", 0)
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("twitter://user?screen_name=$screenName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            // no Twitter app, revert to browser
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://twitter.com/$screenName")
            )
        }
        context.startActivity(intent)
    }

    fun openUrl(context: Context, url: String) {
        if (TextUtils.isEmpty(url)) return
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "It seems that you do not have an app to open the url.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun openWhatsapp(context: Context, phoneNumber: String, message: String?) {
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
        try {
            val pm = context.packageManager
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            openMessage(context, phoneNumber, message)
        }
    }

    /**
     * Courtesy: https://stackoverflow.com/a/12439378/2178568
     *
     * @param context Context
     * @param id      Youtube key id
     */
    fun openYoutubeVideo(context: Context, id: String) {
        try {
            val appIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
            context.startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=$id")
            )
            context.startActivity(webIntent)
        }
    }

    fun shareText(context: Context, text: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }
}