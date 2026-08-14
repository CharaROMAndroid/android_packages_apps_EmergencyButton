/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.emergencybutton.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint
import com.android.emergencybutton.utils.CommonUtils.panicAppListKey
import javax.inject.Inject

@AndroidEntryPoint(BroadcastReceiver::class)
open class PackageManagerReceiver : Hilt_PackageManagerReceiver() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null && intent?.action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart
            val panicApps =
                sharedPreferences.getStringSet(panicAppListKey, emptySet())?.toMutableSet()

            if (!panicApps.isNullOrEmpty() && panicApps.contains(packageName)) {
                panicApps.remove(packageName)
                sharedPreferences.edit(true) { putStringSet(panicAppListKey, panicApps) }
            }
        }
    }
}
