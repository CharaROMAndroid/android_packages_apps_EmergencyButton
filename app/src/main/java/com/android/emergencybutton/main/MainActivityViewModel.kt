/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.emergencybutton.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.android.emergencybutton.models.App
import com.android.emergencybutton.utils.CommonUtils
import com.android.emergencybutton.utils.CommonUtils.panicAppListKey
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak") // false positive, see https://github.com/google/dagger/issues/3253
class MainActivityViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _appList = MutableStateFlow(emptyList<App>())
    val appList = _appList.asStateFlow()

    init {
        _appList.value = getAppList()
    }

    fun getAppList(): List<App> {
        val pkgList = sharedPreferences.getStringSet(panicAppListKey, emptySet())?.toSet()
        if (pkgList.isNullOrEmpty()) return CommonUtils.getAllPackages(context)

        return CommonUtils.getAllPackages(context).toMutableList().map { app ->
            app.panicApp = pkgList.any { it == app.packageName }
            app
        }
    }

    fun savePanicAppList(appList: List<App>) {
        _appList.value = appList
        sharedPreferences.edit {
            putStringSet(
                panicAppListKey,
                appList.filter { it.panicApp }.map { it.packageName }.toSet()
            )
        }
    }

    fun uninstallPanicApps() {
        appList.value
            .filter { it.panicApp }
            .forEach {
                CommonUtils.uninstallPackage(context.packageManager, it.packageName)
            }
    }
}
