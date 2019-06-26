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

package com.allever.security.photo.browser.util;


import com.android.absbase.utils.DeviceUtils;


public class ImageUtil {
    public static final String EXIF_TAG_MAKE = "PhotoEditor";

    public static final int IMAGE_WIDTH = 612;
    public static final int MIDDLE_IMAGE_WIDTH = 305;
    public static final int SMALL_IMAGE_WIDTH = 160;
    public static final int MICRO_IMAGE_WIDTH = 80;
    public static final int IMAGE_SAVE_WIDTH = IMAGE_WIDTH * 2;
    public static final int IMAGE_ME_SAVE_QUALITY = 80;
    public static final int IMAGE_PICTURES_SAVE_QUALITY = 95;


    public static final int getSmallImageCacheWidth() {
        //12 * 2 - 8 * 2
        return Math.min((DeviceUtils.getScreenWidthPx() - DeviceUtils.dip2px(10) * 4) / 3,
                SMALL_IMAGE_WIDTH);
    }


}
