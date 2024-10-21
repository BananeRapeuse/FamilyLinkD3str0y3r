package com.example.familylinkdestroyer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.DataOutputStream
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Demander l'accès aux fichiers et au root
        requestNecessaryPermissions()

        // Bouton pour désinstaller Family Link
        val uninstallFLButton: Button = findViewById(R.id.uninstall_fl_button)
        
        // Action lors du clic sur le bouton
        uninstallFLButton.setOnClickListener {
            if (isFamilyLinkInstalled()) {
                if (isDeviceRooted()) {
                    uninstallFLRoot()
                } else {
                    uninstallFLWithoutRoot()
                }
            } else {
                Toast.makeText(this, "Family Link is not installed", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Fonction pour demander les permissions nécessaires
    private fun requestNecessaryPermissions() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }
    }

    // Fonction pour vérifier si Family Link est installé
    private fun isFamilyLinkInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo("com.google.android.apps.kids.familylink", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // Fonction pour vérifier si l'appareil est rooté
    private fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/su",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su-backup",
            "/system/xbin/mu"
        )
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }
        return false
    }

    // Désinstaller Family Link avec accès root
    private fun uninstallFLRoot() {
        try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            outputStream.writeBytes("pm uninstall --user 0 com.google.android.apps.kids.familylink
")
            outputStream.flush()
            outputStream.writeBytes("exit
")
            outputStream.flush()
            outputStream.close()
            process.waitFor()

            Toast.makeText(this, "Family Link uninstalled successfully with root", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to uninstall Family Link with root", Toast.LENGTH_LONG).show()
        }
    }

    // Désinstaller Family Link sans accès root
    private fun uninstallFLWithoutRoot() {
        try {
            val process = Runtime.getRuntime().exec("adb shell pm uninstall --user 0 com.google.android.apps.kids.familylink")
            process.waitFor()

            if (process.exitValue() == 0) {
                Toast.makeText(this, "Family Link uninstalled successfully without root", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to uninstall Family Link without root", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error executing commands", Toast.LENGTH_LONG).show()
        }
    }

    // Gestion des résultats de la demande de permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions required", Toast.LENGTH_LONG).show()
                finish() // Fermer l'application si les permissions ne sont pas accordées
            }
        }
    }
}