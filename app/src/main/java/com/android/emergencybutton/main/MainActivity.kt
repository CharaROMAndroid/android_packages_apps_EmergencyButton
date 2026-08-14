/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.emergencybutton.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint
import info.guardianproject.panic.Panic
import info.guardianproject.panic.PanicResponder
import com.android.emergencybutton.R

@AndroidEntryPoint(AppCompatActivity::class)
class MainActivity : Hilt_MainActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        setContentView(R.layout.activity_main)

        // Adjust root view's paddings for edgeToEdge display
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { root, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            root.setPadding(0, insets.top, 0, 0)
            windowInsets
        }

        when (intent?.action) {
            Panic.ACTION_TRIGGER -> {
                val prefManager = PreferenceManager.getDefaultSharedPreferences(this)
                viewModel.uninstallPanicApps()
                if (prefManager.getBoolean("exit_app", true)) finish()
            }
            Panic.ACTION_CONNECT -> {
                // Show confirmation dialog if triggering package is a new one
                val intentReferer = PanicResponder.getConnectIntentSender(this)
                val triggerPkgName = PanicResponder.getTriggerPackageName(this)

                if (intentReferer.isNotBlank() && intentReferer != triggerPkgName) {
                    ConfirmationDialogFragment().show(
                        supportFragmentManager,
                        ConfirmationDialogFragment.TAG,
                    )
                }
            }
            Panic.ACTION_DISCONNECT -> {
                PanicResponder.checkForDisconnectIntent(this)
                finish()
            }
        }
    }
}
