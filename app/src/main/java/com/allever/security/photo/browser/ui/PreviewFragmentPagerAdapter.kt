package com.allever.security.photo.browser.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.allever.lib.common.util.DLog
import com.allever.security.photo.browser.bean.ThumbnailBean

class PreviewFragmentPagerAdapter(fragmentManager: FragmentManager, data: MutableList<ThumbnailBean>) :
    FragmentStatePagerAdapter(fragmentManager) {

    private var mData: MutableList<ThumbnailBean>? = data
    private var mContainer: ViewGroup? = null

    override fun getItem(position: Int): Fragment? {
        return PreviewFragment()
    }

    fun instantiateItem(position: Int): Any? {
        if (mContainer == null) return null
        return instantiateItem(mContainer!!, position)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        mContainer = container
        val fragment = super.instantiateItem(container, position)
        if (fragment is PreviewFragment) {
            val fragmentData = mData
            if (fragmentData != null && position in 0 until fragmentData.size) {
                fragment.setData(fragmentData[position])
            }
        }
        DLog.d("currentFragment position = $position")
        return fragment
    }

    override fun getCount(): Int = mData?.size ?: 0

}