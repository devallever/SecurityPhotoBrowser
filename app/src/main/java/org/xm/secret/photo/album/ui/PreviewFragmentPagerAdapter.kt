package org.xm.secret.photo.album.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.view.ViewGroup
import com.allever.lib.common.util.DLog
import org.xm.secret.photo.album.bean.ThumbnailBean

class PreviewFragmentPagerAdapter(fragmentManager: FragmentManager, data: MutableList<ThumbnailBean>) :
    androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    private var mData: MutableList<ThumbnailBean>? = data
    private var mContainer: ViewGroup? = null

    override fun getItem(position: Int): Fragment {
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