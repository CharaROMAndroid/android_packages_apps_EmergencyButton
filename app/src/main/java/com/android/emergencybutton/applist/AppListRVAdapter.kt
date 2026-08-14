/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.emergencybutton.applist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import com.android.emergencybutton.R
import com.android.emergencybutton.models.App
import javax.inject.Inject
import javax.inject.Singleton

class AppListRVAdapter @AssistedInject constructor(
    appListDiffUtil: AppListDiffUtil,
    @Assisted private val currentDestId: Int
) : ListAdapter<App, AppListRVAdapter.ViewHolder>(appListDiffUtil) {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    // Use interface as using NavController in adapter throws weird error
    @AssistedFactory
    interface AppListAdapterFactory {
        fun getAdapter(currentDestId: Int = -1): AppListRVAdapter
    }

    @Singleton
    class AppListDiffUtil @Inject constructor() : DiffUtil.ItemCallback<App>() {

        override fun areItemsTheSame(oldItem: App, newItem: App): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: App, newItem: App): Boolean {
            return when {
                oldItem.icon != newItem.icon -> false
                oldItem.name != newItem.name -> false
                oldItem.packageName != newItem.packageName -> false
                oldItem.panicApp != newItem.panicApp -> false
                else -> true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_app_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = getItem(position)

        holder.view.apply {
            val checkBox = requireViewById<CheckBox>(R.id.checkBox)
            requireViewById<ImageView>(R.id.appIcon).background = app.icon.toDrawable(resources)
            requireViewById<TextView>(R.id.appName).text = app.name
            requireViewById<TextView>(R.id.appPkgName).text = app.packageName
            checkBox.apply {
                isChecked = app.panicApp
                isVisible = currentDestId == R.id.appListFragment

                setOnCheckedChangeListener { view, isChecked ->
                    if (view.isShown) {
                        currentList.find { it.packageName == app.packageName }?.panicApp = isChecked
                    }
                }
            }

            // Set checked if root view was clicked
            if (currentDestId != R.id.appListFragment) {
                isClickable = false
                isFocusable = false
            } else {
                setOnClickListener { checkBox.isChecked = !checkBox.isChecked }
            }
        }
    }
}
