/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allever.security.photo.browser.util


import android.content.Context
import android.view.View
import android.widget.ImageView
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.function.endecode.DecodeListener
import com.allever.security.photo.browser.function.endecode.PrivateBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.android.absbase.utils.DeviceUtils
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File
import com.davemorrissey.labs.subscaleview.ImageSource

object ImageUtil {
    val EXIF_TAG_MAKE = "PhotoEditor"

    val IMAGE_WIDTH = 612
    val MIDDLE_IMAGE_WIDTH = 305
    val SMALL_IMAGE_WIDTH = 160
    val MICRO_IMAGE_WIDTH = 80
    val IMAGE_SAVE_WIDTH = IMAGE_WIDTH * 2
    val IMAGE_ME_SAVE_QUALITY = 80
    val IMAGE_PICTURES_SAVE_QUALITY = 95


    //12 * 2 - 8 * 2
    val smallImageCacheWidth: Int
        get() = Math.min(
            (DeviceUtils.getScreenWidthPx() - DeviceUtils.dip2px(10f) * 4) / 3,
            SMALL_IMAGE_WIDTH
        )


    fun loadBigImage(context: Context, thumbnailBean: ThumbnailBean?, imageView: SubsamplingScaleImageView) {
        thumbnailBean ?: return
        if (thumbnailBean.sourceType == ThumbnailBean.SYSTEM) {
            if (MediaTypeUtil.isGif(thumbnailBean.type)) {

            } else {
                imageView.setImage(ImageSource.uri(thumbnailBean.path))
            }
            return
        }
        val tempFile = File(thumbnailBean.tempPath)
        if (tempFile.exists()) {
            imageView.setImage(ImageSource.uri(thumbnailBean.tempPath))
//            Glide.with(context).load(thumbnailBean.tempPath).into(imageView)
        } else {
            //解码
            val md5Path = MD5.getMD5Str(thumbnailBean.path) ?: return
            PrivateHelper.decode(
                File(
                    PrivateHelper.PATH_ENCODE_ORIGINAL,
                    md5Path
                ).absolutePath, object : DecodeListener {
                    override fun onDecodeStart() {}
                    override fun onDecodeSuccess(privateBean: PrivateBean) {
                        imageView.setImage(ImageSource.uri(thumbnailBean.tempPath))
//                        Glide.with(context).load(thumbnailBean.tempPath).into(imageView)
                    }

                    override fun onDecodeFailed(msg: String) {}
                })
        }
    }

    fun loadEncodeImage(context: Context, thumbnailBean: ThumbnailBean?, imageView: ImageView) {
        thumbnailBean ?: return
        imageView.visibility = View.VISIBLE
        if (thumbnailBean.sourceType == ThumbnailBean.SYSTEM) {
            Glide.with(context).load(thumbnailBean.path).into(imageView)
            return
        }
        val tempFile = File(thumbnailBean.tempPath)
        if (tempFile.exists()) {
            Glide.with(context).load(thumbnailBean.tempPath).into(imageView)
        } else {
            //解码
            val md5Path = MD5.getMD5Str(thumbnailBean.path) ?: return
            PrivateHelper.decode(
                File(
                    PrivateHelper.PATH_ENCODE_ORIGINAL,
                    md5Path
                ).absolutePath, object : DecodeListener {
                    override fun onDecodeStart() {}
                    override fun onDecodeSuccess(privateBean: PrivateBean) {
                        Glide.with(context).load(thumbnailBean.tempPath).into(imageView)
                    }

                    override fun onDecodeFailed(msg: String) {}
                })
        }
    }
}
