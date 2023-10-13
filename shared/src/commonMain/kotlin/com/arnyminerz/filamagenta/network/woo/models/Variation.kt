package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Variation(
    val id: Int = 0,
    val title: String? = null,
    val name: String? = null,
    val slug: String? = null,
    val permalink: String,
    val type: String? = null,
    val status: String,
    val isFeatured: Boolean = false,
    val catalog_visibility: String? = null,
    val description: String,
    val short_description: String? = null,
    val sku: String,
    val price: String,
    val regular_price: String,
    val sale_price: String,
    val date_on_sale_from: LocalDateTime? = null,
    val date_on_sale_from_gmt: LocalDateTime? = null,
    val date_on_sale_to: LocalDateTime? = null,
    val date_on_sale_to_gmt: LocalDateTime? = null,
    val price_html: String? = null,
    val isOn_sale: Boolean = false,
    val isPurchasable: Boolean = false,
    val total_sales: Int = 0,
    val isVirtual: Boolean = false,
    val isDownloadable: Boolean = false,
    val downloads: ArrayList<Download>,
    val download_limit: Int = 0,
    val download_expiry: Int = 0,
    val external_url: String? = null,
    val button_text: String? = null,
    val tax_status: String,
    val tax_class: String,
    val isManage_stock: Boolean = false,
    val stock_quantity: Int = 0,
    val isIn_stock: Boolean = false,
    val backorders: String,
    val isBackorders_allowed: Boolean = false,
    val isBackordered: Boolean = false,
    val isSold_individually: Boolean = false,
    val weight: String,
    val dimensions: Dimensions,
    val isShipping_required: Boolean = false,
    val isShipping_taxable: Boolean = false,
    val shipping_class: String,
    val shipping_class_id: Int = 0,
    val isReviews_allowed: Boolean = false,
    val average_rating: String? = null,
    val rating_count: Int = 0,
    val related_ids: ArrayList<Int>? = null,
    val upsell_ids: ArrayList<Int>? = null,
    val cross_sell_ids: ArrayList<Int>? = null,
    val parent_id: Int = 0,
    val purchase_note: String? = null,
    val categories: ArrayList<Category>? = null,
    val tags: ArrayList<Tag>? = null,
    val attributes: ArrayList<ProductAttribute>? = null,
    val default_attributes: ArrayList<DefaultAttribute>? = null,
    val grouped_products: ArrayList<Int>? = null,
    val menu_order: Int = 0,
    val meta_data: ArrayList<Metadata>,
    val images: ArrayList<Image>? = null
)
