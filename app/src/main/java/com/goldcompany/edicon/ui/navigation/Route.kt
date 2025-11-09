package com.goldcompany.edicon.ui.navigation

import com.goldcompany.edicon.ui.util.ContentType
import kotlinx.serialization.Serializable

/**
 * Compose Navigation이 화면의 주소 역할을 하는 Route 객체를
 * 안드로이드 시스템이 이해할 수 있는 형태로 저장하고, 복원하고, 전달할 수 있도록 하기 위한 Serializable
 * Compose Navigation은 Route를 포함한 BaxkStack 정보를 SvaedStateHandle에 저장한다.
 * SavedStateHandle은 내부적으로 Bundle 객체를 활용하기 때문에 Bundle에 저장하기 위해서 @Serializable이 필요하다.
 */

/**
 * 앱 Screen Navigation Route
 */
sealed class Route {
    @Serializable
    data object Home: Route()
    @Serializable
    data object Favorite: Route()
    @Serializable
    data class Detail(
        val id: Int,
        val contentType: ContentType
    ) : Route()
}