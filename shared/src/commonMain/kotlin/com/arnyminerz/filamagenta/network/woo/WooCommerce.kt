package com.arnyminerz.filamagenta.network.woo

import com.arnyminerz.filamagenta.BuildKonfig
import me.gilo.woodroid.WooCommerce
import me.gilo.woodroid.data.ApiVersion

val wooCommerce = WooCommerce.Builder()
    .setApiVersion(ApiVersion.API_VERSION3)
    .setConsumerKey(BuildKonfig.WooClientId)
    .setConsumerSecret(BuildKonfig.WooClientSecret)
    .setSiteUrl("https://${BuildKonfig.ServerHostname}")
    .build()
