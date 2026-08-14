/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.emergencybutton.applist

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.android.emergencybutton.R
import com.android.emergencybutton.main.MainActivityViewModel
import com.android.emergencybutton.utils.CommonUtils
import javax.inject.Inject

@AndroidEntryPoint(Fragment::class)
class AppListFragment :
    Hilt_AppListFragment(R.layout.fragment_app_list),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var appListAdapterFactory: AppListRVAdapter.AppListAdapterFactory

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var appListRVAdapter: AppListRVAdapter
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appListRVAdapter = appListAdapterFactory.getAdapter(R.id.appListFragment)

        // Recycler View
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appList.collect {
                    appListRVAdapter.submitList(it)
                }
            }
        }
        view.requireViewById<RecyclerView>(R.id.recyclerView).adapter = appListRVAdapter
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        // Floating Action Button
        view.requireViewById<FloatingActionButton>(R.id.floatingActionButton).apply {
            setOnClickListener {
                viewModel.savePanicAppList(appListRVAdapter.currentList)
                findNavController().navigateUp()
            }

            // Adjust layout margins for edgeToEdge display
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
                updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = insets.bottom }
                WindowInsetsCompat.CONSUMED
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == CommonUtils.panicAppListKey) {
            appListRVAdapter.submitList(viewModel.getAppList())
        }
    }
}
