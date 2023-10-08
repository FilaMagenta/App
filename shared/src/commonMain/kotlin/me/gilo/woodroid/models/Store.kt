package me.gilo.woodroid.models

import kotlinx.serialization.Serializable

@Serializable
class Store {

    var wc_version: String? = null
    var description: String? = null
    var name: String? = null
    var url: String? = null
    var meta: Meta? = null

    var version: String? = null

    override fun toString(): String {
        return "ClassPojo [wc_version = $wc_version, description = $description, name = $name, URL = $url, meta = $meta, version = $version]"
    }

}
