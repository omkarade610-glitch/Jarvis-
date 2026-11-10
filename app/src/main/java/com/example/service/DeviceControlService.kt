package com.example.service

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log

data class AppItem(
    val name: String,
    val packageName: String
)

class DeviceControlService(private val context: Context) {
    private val TAG = "DeviceControlService"

    fun openChrome(searchQuery: String? = null): Boolean {
        return try {
            val url = if (!searchQuery.isNullOrBlank()) {
                "https://www.google.com/search?q=" + Uri.encode(searchQuery)
            } else {
                "https://www.google.com"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setPackage("com.android.chrome")
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                // Fallback to default browser
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening Chrome", e)
            false
        }
    }

    fun openYouTube(searchQuery: String? = null): Boolean {
        return try {
            val url = if (!searchQuery.isNullOrBlank()) {
                "https://www.youtube.com/results?search_query=" + Uri.encode(searchQuery)
            } else {
                "https://www.youtube.com"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening YouTube", e)
            false
        }
    }

    fun openSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening Settings", e)
            false
        }
    }

    fun openGallery(): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "image/*"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening Gallery", e)
            false
        }
    }

    fun openInstagram(): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening Instagram", e)
            false
        }
    }

    fun toggleFlashlight(enabled: Boolean): Result<Unit> {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
                ?: return Result.failure(IllegalStateException("CameraManager not available"))

            var cameraIdWithFlash: String? = null
            for (id in cameraManager.cameraIdList) {
                val chars = cameraManager.getCameraCharacteristics(id)
                val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                if (hasFlash) {
                    cameraIdWithFlash = id
                    break
                }
            }

            if (cameraIdWithFlash != null) {
                cameraManager.setTorchMode(cameraIdWithFlash, enabled)
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("No flashlight available on device"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle flashlight", e)
            Result.failure(e)
        }
    }

    fun adjustVolume(increase: Boolean): Result<Int> {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return Result.failure(IllegalStateException("AudioManager not available"))

            val direction = if (increase) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
            val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            Result.success(currentVol)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to adjust volume", e)
            Result.failure(e)
        }
    }

    fun setAlarm(hour: Int, minutes: Int, message: String): Result<Unit> {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set alarm", e)
            Result.failure(e)
        }
    }

    fun launchAppByPackage(packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app $packageName", e)
            false
        }
    }

    fun getInstalledLaunchableApps(): List<AppItem> {
        return try {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            resolveInfos.mapNotNull { ri ->
                val name = ri.loadLabel(pm).toString()
                val pkg = ri.activityInfo.packageName
                if (pkg != context.packageName) AppItem(name, pkg) else null
            }.distinctBy { it.packageName }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to query apps", e)
            emptyList()
        }
    }
}
