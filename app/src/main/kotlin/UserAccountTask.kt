package com.example.yoshi.viewpagertodo1

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2

fun getDropBoxClient(accessToken: String): DbxClientV2 {
    val requestConfig = DbxRequestConfig.newBuilder("Name/Version")
            .build()
    return DbxClientV2(requestConfig, accessToken)
}
