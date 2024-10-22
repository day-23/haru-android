package com.example.haru.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SharedPrefsManager {
    private var sharedPreferences: SharedPreferences? = null

    fun getSharedPrefs(context: Context): SharedPreferences {
        if (sharedPreferences == null) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val i = 0
            while (true) {
                try {
                    sharedPreferences = EncryptedSharedPreferences.create(
                        context,
                        "HaruSharedPrefs"+i.toString(),
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )

                    break
                } catch (ex : Exception){

                }
            }
        }
        return sharedPreferences!!
    }

    fun clear(context: Context) {
        val editor = getSharedPrefs(context).edit()
        editor.clear()
        editor.apply()
    }
}