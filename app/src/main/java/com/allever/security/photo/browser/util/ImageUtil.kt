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


import com.android.absbase.utils.DeviceUtils


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


}
