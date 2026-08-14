/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.emergencybutton.main

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import info.guardianproject.panic.PanicResponder
import com.android.emergencybutton.R

@AndroidEntryPoint(DialogFragment::class)
class ConfirmationDialogFragment : Hilt_ConfirmationDialogFragment() {

    companion object {
        const val TAG = "ConfirmationDialogFragment"
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val packageManager = requireContext().packageManager
        val callingPkgInfo = packageManager.getApplicationInfo(
            requireActivity().callingPackage!!,
            PackageManager.ApplicationInfoFlags.of(0)
        )
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirmation_title)
            .setMessage(
                HtmlCompat.fromHtml(
                    getString(
                        R.string.confirmation_desc,
                        packageManager.getApplicationLabel(callingPkgInfo)
                    ),
                    FROM_HTML_MODE_COMPACT
                )
            )
            .setPositiveButton(R.string.accept) { _, _ ->
                activity?.apply {
                    setResult(Activity.RESULT_OK)
                    PanicResponder.setTriggerPackageName(this)
                }
            }
            .setNegativeButton(R.string.reject) { _, _ ->
                activity?.apply {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            .create()
    }
}
