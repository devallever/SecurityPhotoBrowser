/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allever.security.photo.browser.ui

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.text.TextUtils


import java.util.ArrayList
import java.util.Arrays
import java.util.Locale

import android.view.View.LAYOUT_DIRECTION_RTL
import com.allever.security.photo.browser.R

/**
 * All tab data is accessed via this model.
 */
object TabModel {

    /**
     * Identifies each of the primary tabs within the application.
     */
    enum class Tab private constructor(
        fragmentClass: Class<*>,
        @param:DrawableRes @field:DrawableRes @get:DrawableRes val iconResId: Int,
        @param:StringRes @field:StringRes @get:StringRes
        val labelResId: Int,
        val emptyIconResId: Int,
        val bundle: Bundle? = null
    ) {
        /**
         *
         */
        ALL(
            PickFragment::class.java,
            R.drawable.ic_launcher,
            R.string.tab_all_album,
            emptyIconResId = R.drawable.icon_album_empty_all
        ),
        VIDEO(
            PickFragment::class.java,
            R.drawable.ic_launcher,
            R.string.tab_video,
            emptyIconResId = R.drawable.icon_album_empty_video
        ),
        PICTURE(
            PickFragment::class.java,
            R.drawable.ic_launcher,
            R.string.tab_photo,
            emptyIconResId = R.drawable.icon_album_empty_photo
        );

        val fragmentClassName: String
        var drawable: Drawable? = null

        init {
            fragmentClassName = fragmentClass.name
        }
    }

    /**
     * The listeners to notify when the selected tab is changed.
     */
    private val mTabListeners = ArrayList<TabListener>()

    /**
     * The listeners to notify when the vertical scroll state of the selected tab is changed.
     */
    private val mTabScrollListeners = ArrayList<TabScrollListener>()

    /**
     * The scrolled-to-top state of each tab.
     */
    private val mTabScrolledToTop = BooleanArray(Tab.values().size)

    /**
     * An enumerated value indicating the currently selected tab.
     */
    private var mSelectedTab: Tab = Tab.ALL

    /**
     * @return the number of tabs
     */
    val tabCount: Int
        get() = Tab.values().size

    /**
     * @return an enumerated value indicating the currently selected primary tab
     */
    /**
     * @param tab an enumerated value indicating the newly selected primary tab
     */
    // Notify of the tab change.
    // Notify of the vertical scroll position change if there is one.
    var selectedTab: Tab
        get() {
            return mSelectedTab
        }
        set(tab) {
            val oldSelectedTab = selectedTab
            if (oldSelectedTab != tab) {
                mSelectedTab = tab
                for (tl in mTabListeners) {
                    tl.selectedTabChanged(oldSelectedTab, tab)
                }
                val tabScrolledToTop = isTabScrolledToTop(tab)
                if (isTabScrolledToTop(oldSelectedTab!!) != tabScrolledToTop) {
                    for (tsl in mTabScrollListeners) {
                        tsl.selectedTabScrollToTopChanged(tab, tabScrolledToTop)
                    }
                }
            }
        }


    init {
        Arrays.fill(mTabScrolledToTop, true)
    }

    //
    // Selected tab
    //

    /**
     * @param tabListener to be notified when the selected tab changes
     */
    fun addTabListener(tabListener: TabListener) {
        mTabListeners.add(tabListener)
    }

    /**
     * @param tabListener to no longer be notified when the selected tab changes
     */
    fun removeTabListener(tabListener: TabListener) {
        mTabListeners.remove(tabListener)
    }

    /**
     * @param ordinal the ordinal (left-to-right index) of the tab
     * @return the tab at the given `ordinal`
     */
    fun getTab(ordinal: Int): Tab {
        return Tab.values()[ordinal]
    }

    /**
     * @param position the position of the tab in the user interface
     * @return the tab at the given `ordinal`
     */
    fun getTabAt(position: Int): Tab {
        val ordinal: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == LAYOUT_DIRECTION_RTL) {
            ordinal = tabCount - position - 1
        } else {
            ordinal = position
        }
        return getTab(ordinal)
    }

    //
    // Tab scrolling
    //

    /**
     * @param tabScrollListener to be notified when the scroll position of the selected tab changes
     */
    fun addTabScrollListener(tabScrollListener: TabScrollListener) {
        mTabScrollListeners.add(tabScrollListener)
    }

    /**
     * @param tabScrollListener to be notified when the scroll position of the selected tab changes
     */
    fun removeTabScrollListener(tabScrollListener: TabScrollListener) {
        mTabScrollListeners.remove(tabScrollListener)
    }

    /**
     * Updates the scrolling state in the for this tab.
     *
     * @param tab           an enumerated value indicating the tab reporting its vertical scroll position
     * @param scrolledToTop `true` iff the vertical scroll position of this tab is at the top
     */
    fun setTabScrolledToTop(tab: Tab, scrolledToTop: Boolean) {
        if (isTabScrolledToTop(tab) != scrolledToTop) {
            mTabScrolledToTop[tab.ordinal] = scrolledToTop
            if (tab == selectedTab) {
                for (tsl in mTabScrollListeners) {
                    tsl.selectedTabScrollToTopChanged(tab, scrolledToTop)
                }
            }
        }
    }

    /**
     * @param tab identifies the tab
     * @return `true` iff the content in the given `tab` is currently scrolled to top
     */
    fun isTabScrolledToTop(tab: Tab): Boolean {
        return mTabScrolledToTop[tab.ordinal]
    }


    interface TabScrollListener {

        /**
         * @param selectedTab   an enumerated value indicating the current selected tab
         * @param scrolledToTop indicates whether the current selected tab is scrolled to its top
         */
        fun selectedTabScrollToTopChanged(selectedTab: Tab, scrolledToTop: Boolean)
    }

    interface TabListener {

        /**
         * @param oldSelectedTab an enumerated value indicating the prior selected tab
         * @param newSelectedTab an enumerated value indicating the newly selected tab
         */
        fun selectedTabChanged(oldSelectedTab: Tab?, newSelectedTab: Tab)
    }
}