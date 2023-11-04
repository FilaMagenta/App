package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class SettingGroup {

    internal var id: String? = null
    internal var label: String? = null
    internal var description: String? = null
    internal var parent_id: String? = null
    internal var sub_groups: List<String>? = null

}
