package com.allever.security.photo.browser.ad

import com.allever.lib.ad.ADType
import com.allever.lib.ad.AdBusiness

object AdConstant {

    //    //adeverdeng
    private const val ADMOB_APPID = "ca-app-pub-9972782174497381~2056863410"
    private const val ALBUM_BANNER = "ca-app-pub-9972782174497381/8239128384"
    private const val GUIDE_BANNER = ALBUM_BANNER
    private const val SETTING_BANNER = ALBUM_BANNER
    private const val GALLERY_BANNER = ALBUM_BANNER
    private const val PICK_BANNER = ALBUM_BANNER
    private const val EXIT_INSERT = "ca-app-pub-9972782174497381/5421393358"
    private const val IMPORT_INSERT = EXIT_INSERT
    private const val EXPORT_INSERT = EXIT_INSERT
    private const val SETTING_INSERT = EXIT_INSERT
    private const val SETTING_VIDEO = "ca-app-pub-9972782174497381/7147933210"


    val AD_NAME_EXIT_INSERT = "AD_NAME_EXIT_INSERT"

    val AD_NAME_IMPORT_INSERT = "AD_NAME_IMPORT_INSERT"

    val AD_NAME_EXPORT_INSERT = "AD_NAME_EXPORT_INSERT"

    val AD_NAME_SETTING_INSERT = "AD_NAME_SETTING_INSERT"

    val AD_NAME_SETTING_BANNER = "AD_NAME_SETTING_BANNER"

    val AD_NAME_ALBUM_BANNER = "AD_NAME_ALBUM_BANNER"

    val AD_NAME_GUIDE_BANNER = "AD_NAME_GUIDE_BANNER"

    val AD_NAME_GALLERY_BANNER = "AD_NAME_GALLERY_BANNER"

    val AD_NAME_PICK_BANNER = "AD_NAME_PICK_BANNER"

    val AD_NAME_SETTING_VIDEO = "AD_NAME_SETTING_VIDEO"

    val adData = "{\n" +
            "  \"business\": [\n" +
            "    {\n" +
            "      \"name\": \"${AdBusiness.A}\",\n" +
            "      \"appId\": \"\",\n" +
            "      \"appKey\": \"\",\n" +
            "      \"token\": \"\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"adConfig\": [\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_EXIT_INSERT\",\n" +
            "      \"type\": \"${ADType.INSERT}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$EXIT_INSERT\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"eb075d1ea0806afa7bdbf1179315fb37\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_EXPORT_INSERT\",\n" +
            "      \"type\": \"${ADType.INSERT}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$EXPORT_INSERT\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"eb075d1ea0806afa7bdbf1179315fb37\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_IMPORT_INSERT\",\n" +
            "      \"type\": \"${ADType.INSERT}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$IMPORT_INSERT\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"eb075d1ea0806afa7bdbf1179315fb37\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_SETTING_INSERT\",\n" +
            "      \"type\": \"${ADType.INSERT}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$SETTING_INSERT\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"eb075d1ea0806afa7bdbf1179315fb37\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_SETTING_BANNER\",\n" +
            "      \"type\": \"${ADType.BANNER}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$SETTING_BANNER\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"da0a4151248552a7684079f90eba203a\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_ALBUM_BANNER\",\n" +
            "      \"type\": \"${ADType.BANNER}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$ALBUM_BANNER\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"da0a4151248552a7684079f90eba203a\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_GALLERY_BANNER\",\n" +
            "      \"type\": \"${ADType.BANNER}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$GALLERY_BANNER\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"da0a4151248552a7684079f90eba203a\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_PICK_BANNER\",\n" +
            "      \"type\": \"${ADType.BANNER}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$PICK_BANNER\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"da0a4151248552a7684079f90eba203a\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_GUIDE_BANNER\",\n" +
            "      \"type\": \"${ADType.BANNER}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$GUIDE_BANNER\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.MI}\",\n" +
            "          \"adPosition\": \"da0a4151248552a7684079f90eba203a\"\n" +
            "        }" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"$AD_NAME_SETTING_VIDEO\",\n" +
            "      \"type\": \"${ADType.VIDEO}\",\n" +
            "      \"chain\": [\n" +
            "        {\n" +
            "          \"business\": \"${AdBusiness.A}\",\n" +
            "          \"adPosition\": \"$SETTING_VIDEO\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n"
}