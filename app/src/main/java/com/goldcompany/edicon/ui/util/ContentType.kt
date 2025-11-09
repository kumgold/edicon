package com.goldcompany.edicon.ui.util

import kotlinx.serialization.Serializable

@Serializable
enum class ContentType {
    IMAGE,
    VIDEO;

    companion object {
        /**
         * API 등에서 받은 문자열 값을 기반으로 해당하는 ContentType을 반환
         *
         * @param type 변환할 문자열 (예: "image", "video")
         * @return 변환된 ContentType enum 객체
         */
        fun fromString(type: String?): ContentType {
            return when (type?.lowercase()) {
                "video" -> VIDEO
                else -> IMAGE
            }
        }
    }
}

val String?.toContentType: ContentType
    get() = ContentType.fromString(this)