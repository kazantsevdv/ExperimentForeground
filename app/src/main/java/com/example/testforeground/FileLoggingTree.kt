package com.example.testforeground

import android.content.Context
import android.os.Environment
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class FileLoggingTree(val context: Context) : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            Log.e(tag, "$message")
            val path = "Log"
            val fileNameTimeStamp = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).format(Date())
            val logTimeStamp = SimpleDateFormat(
                "E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                Locale.getDefault()
            ).format(Date())
            val fileName = "$fileNameTimeStamp.html"

            // Create file
            val file = generateFile(path, fileName, context)

            // If file created or exists save logs
            if (file != null) {
                val writer = FileWriter(file, true)
                writer.append(
                    "<p style=\"background:lightgray;\"><strong "
                            + "style=\"background:lightblue;\">&nbsp&nbsp"
                )
                    .append(logTimeStamp)
                    .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                    .append(tag)
                    .append("</strong> - ")
                    .append(message)
                    .append("</p>")
                writer.flush()
                writer.close()
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error while logging into file : $e")
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }

    companion object {
        private val LOG_TAG = FileLoggingTree::class.java.simpleName

        /*  Helper method to create file*/
        private fun generateFile(path: String, fileName: String, context: Context): File? {
            var file: File? = null
            if (isExternalStorageAvailable) {
                val root = File(
                    Environment.getExternalStorageDirectory(),
                    "Android/data/" + BuildConfig.APPLICATION_ID + File.separator + path
                )
                var dirExists = true
                if (!root.exists()) {
                    dirExists = root.mkdirs()
                }
                if (dirExists) {
                    file = File(root, fileName)
                }
            }
            return file
        }

        /* Helper method to determine if external storage is available*/
        private val isExternalStorageAvailable: Boolean
            private get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}