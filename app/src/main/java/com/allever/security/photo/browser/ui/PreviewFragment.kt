package com.allever.security.photo.browser.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageView
import android.widget.VideoView
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.util.ImageUtil
import com.allever.security.photo.browser.util.MediaTypeUtil

class PreviewFragment : Fragment() {

    companion object {
        private val TAG = PreviewFragment::class.java.simpleName
    }

    //    private var mVideoMark: View? = null

    private var mThumbnailBean: ThumbnailBean? = null
    private var mVideoViewHolder: VideoViewHolder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_preview, container, false)
        val imageView = rootView?.findViewById<ImageView>(R.id.id_iv_image)
        val videoView = rootView?.findViewById<VideoView>(R.id.id_video_view)
        if (MediaTypeUtil.isImage(mThumbnailBean?.type!!)) {
            ImageUtil.loadEncodeImage(context!!, mThumbnailBean, imageView!!)
        }
//

//        val ivPlayAndPause = rootView?.findViewById<ImageView>(R.id.id_iv_video_controller)
//
//        if (MediaTypeUtil.isImage(mThumbnailBean?.type ?: -1)) {
//            //图片类型
//            imageView?.visibility = View.VISIBLE
//            ImageLoader.loadImage(mThumbnailBean?.path, imageView)
//        } else if (MediaTypeUtil.isVideo(mThumbnailBean?.type ?: -1)) {
//            //视频类型
//            imageView?.visibility = View.GONE
//
//            mVideoViewHolder = VideoViewHolder()
//            mVideoViewHolder?.initVideo(videoView, mThumbnailBean?.path, ivPlayAndPause)
//        }
//
//        if (mThumbnailBean?.isAutoPlay == true){
//            mVideoViewHolder?.play()
//        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mVideoViewHolder?.stop()
        mVideoViewHolder?.destroy()
        mVideoViewHolder = null
    }

    fun pause() {
        mVideoViewHolder?.pause()
    }

    fun setData(thumbnailBean: ThumbnailBean?) {
        mThumbnailBean = thumbnailBean
    }

}