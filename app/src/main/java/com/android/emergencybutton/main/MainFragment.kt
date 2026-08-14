/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.emergencybutton.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
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
import info.guardianproject.panic.Panic
import kotlinx.coroutines.launch
import com.android.emergencybutton.R
import com.android.emergencybutton.applist.AppListRVAdapter
import com.android.emergencybutton.utils.CommonUtils.panicAppListKey
import javax.inject.Inject

@AndroidEntryPoint(Fragment::class)
class MainFragment :
    Hilt_MainFragment(R.layout.fragment_main),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var appListAdapterFactory: AppListRVAdapter.AppListAdapterFactory

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var appListRVAdapter: AppListRVAdapter
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent?.action != Panic.ACTION_TRIGGER) {
            // Toolbar
            val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
            if (activity?.intent?.action == Intent.ACTION_MAIN) {
                toolbar.navigationIcon = null
            } else {
                toolbar.setNavigationOnClickListener { activity?.finish() }
            }

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.settings -> findNavController().navigate(R.id.settingsFragment)
                }
                true
            }

            // Floating Action Button
            view.requireViewById<FloatingActionButton>(R.id.floatingActionButton).apply {
                setOnClickListener { findNavController().navigate(R.id.appListFragment) }

                // Adjust layout margins for edgeToEdge display
                ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
                    updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        bottomMargin = insets.bottom
                    }
                    WindowInsetsCompat.CONSUMED
                }
            }

            // Recycler View
            appListRVAdapter = appListAdapterFactory.getAdapter()
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.appList.collect { list ->
                        appListRVAdapter.submitList(list.filter { it.panicApp })
                    }
                }
            }
            view.requireViewById<RecyclerView>(R.id.recyclerView).adapter = appListRVAdapter
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        } else {
            view.requireViewById<ConstraintLayout>(R.id.mainFragmentLayout).visibility = View.GONE
            view.requireViewById<ConstraintLayout>(R.id.panicActionLayout).visibility = View.VISIBLE
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == panicAppListKey) {
            appListRVAdapter.submitList(viewModel.getAppList().filter { it.panicApp })
        }
    }
}
