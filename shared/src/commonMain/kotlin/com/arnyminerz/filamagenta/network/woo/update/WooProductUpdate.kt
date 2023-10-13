package com.arnyminerz.filamagenta.network.woo.update

import kotlinx.serialization.Serializable

/**
 * Used for identifying WooCommerce product updates.
 */
@Serializable
sealed interface WooProductUpdate: WooUpdate
