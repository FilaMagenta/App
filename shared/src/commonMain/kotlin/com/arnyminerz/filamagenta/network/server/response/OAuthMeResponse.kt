package com.arnyminerz.filamagenta.network.server.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthMeResponse(
    @SerialName("ID") val id: Int,
    @SerialName("user_login") val userLogin: String,
    @SerialName("user_email") val userEmail: String,
    @SerialName("user_registered") val userRegistered: String,
    @SerialName("user_status") val userStatus: Int,
    @SerialName("display_name") val displayName: String,
    @SerialName("user_nicename") val userNiceName: String,
    @SerialName("user_roles") val userRoles: List<String>,
    @SerialName("capabilities") val capabilities: Map<String, Boolean>
)
