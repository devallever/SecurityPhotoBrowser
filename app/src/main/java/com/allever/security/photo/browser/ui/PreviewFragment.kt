package com.allever.security.photo.browser.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.ImageView
import android.widget.VideoView
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.util.ImageUtil
import com.allever.security.photo.browser.util.MediaTypeUtil
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.chrisbanes.photoview.PhotoView

class PreviewFragment : androidx.fragment.app.Fragment() {

    companion object {
        private val TAG = PreviewFragment::class.java.simpleName
    }

    private var mThumbnailBean: ThumbnailBean? = null
    private var mVideoViewHolder: VideoViewHolder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_preview, container, false)
        val imageView = rootView?.findViewById<SubsamplingScaleImageView>(R.id.id_iv_image)
        imageView?.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
        val gifView = rootView.findViewById<PhotoView>(R.id.id_iv_image_gif)
        val videoView = rootView?.findViewById<VideoView>(R.id.id_video_view)
        val ivPlayAndPause = rootView?.findViewById<ImageView>(R.id.id_iv_video_controller)

        if (MediaTypeUtil.isImage(mThumbnailBean?.type ?: -1)) {
            //图片类型
            if (MediaTypeUtil.isGif(mThumbnailBean?.type!!)) {
                ImageUtil.loadEncodeImage(context!!, mThumbnailBean, gifView)
                imageView?.visibility = View.GONE
            } else {
                ImageUtil.loadBigImage(context!!, mThumbnailBean, imageView!!)
                gifView.visibility = View.GONE
            }
        } else if (MediaTypeUtil.isVideo(mThumbnailBean?.type ?: -1)) {
            //视频类型
            imageView?.visibility = View.GONE
            gifView.visibility = View.GONE

            mVideoViewHolder = VideoViewHolder()
            //判断资源类型，系统资源还是加密资源
            if (mThumbnailBean?.sourceType == ThumbnailBean.DECODE) {
                mVideoViewHolder?.initVideo(videoView, mThumbnailBean?.tempPath, ivPlayAndPause)
            } else {
                mVideoViewHolder?.initVideo(videoView, mThumbnailBean?.path, ivPlayAndPause)
            }
        }

        if (mThumbnailBean?.isAutoPlay == true){
            mVideoViewHolder?.play()
        }

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