package com.allever.security.photo.browser.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.allever.security.photo.browser.bean.ThumbnailBean

class PreviewFragmentPagerAdapter(fragmentManager: FragmentManager, data: MutableList<ThumbnailBean>) : FragmentStatePagerAdapter(fragmentManager) {

    var data: MutableList<ThumbnailBean>? = data
    var currentFragment: Fragment? = null

    override fun getItem(position: Int): Fragment? {
        return PreviewFragment()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        if (fragment is PreviewFragment) {
            val fragmentData = data
            if (fragmentData != null && position in 0 until fragmentData.size) {
                fragment.setData(fragmentData[position])
            }
        }
        currentFragment = fragment as Fragment
        return fragment
    }

    override fun getCount(): Int = data?.size ?: 0

}