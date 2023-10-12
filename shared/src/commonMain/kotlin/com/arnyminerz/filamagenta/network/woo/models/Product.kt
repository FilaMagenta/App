package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("LongParameterList")
class Product(
    val id: Long = 0,
    val name: String,
    val slug: String? = null,
    val permalink: String? = null,
    @SerialName("date_created") val dateCreated: LocalDateTime? = null,
    @SerialName("date_created_gmt") val dateCreatedGmt: LocalDateTime? = null,
    @SerialName("date_modified") val dateModified: LocalDateTime? = null,
    @SerialName("date_modified_gmt") val dateModifiedGmt: LocalDateTime? = null,
    val type: String? = null,
    val status: String,
    val isFeatured: Boolean = false,
    @SerialName("catalog_visibility") val catalogVisibility: String,
    val description: String,
    @SerialName("short_description") val shortDescription: String,
    val sku: String,
    val price: String,
    @SerialName("regular_price") val regularPrice: String? = null,
    @SerialName("sale_price") val salePrice: String? = null,
    @SerialName("date_on_sale_from") val dateOnSaleFrom: LocalDateTime? = null,
    @SerialName("date_on_sale_from_gmt") val dateOnSaleFromGmt: LocalDateTime? = null,
    @SerialName("date_on_sale_to") val dateOnSaleTo: LocalDateTime? = null,
    @SerialName("date_on_sale_to_gmt") val dateOnSaleToGmt: LocalDateTime? = null,
    @SerialName("price_html")  val priceHtml: String? = null,
    @SerialName("isOn_sale")  val isOnSale: Boolean = false,
    val isPurchasable: Boolean = false,
    @SerialName("total_sales") val totalSales: Int = 0,
    val isVirtual: Boolean = false,
    val isDownloadable: Boolean = false,
    val downloads: ArrayList<Download>,
    val download_limit: Int = 0,
    val download_expiry: Int = 0,
    val external_url: String,
    val button_text: String,
    val tax_status: String,
    val tax_class: String,
    val isManage_stock: Boolean = false,
    val stock_quantity: Int? = null,
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
    val average_rating: String,
    val rating_count: Int = 0,
    val related_ids: ArrayList<Int>,
    val upsell_ids: ArrayList<Int>,
    val cross_sell_ids: ArrayList<Int>,
    val parent_id: Int = 0,
    val purchase_note: String,
    val categories: ArrayList<Category>,
    val tags: ArrayList<Tag>,

    @SerialName("attributes")
    val productAttributes: ArrayList<ProductAttribute>,

    val default_attributes: ArrayList<DefaultAttribute>,
    val variations: ArrayList<Int>,
    val grouped_products: ArrayList<Int>,
    val menu_order: Int = 0,
    val meta_data: ArrayList<Metadata>,
    val images: ArrayList<Image>
) {

    fun getFeatureImage(): String {
        if (this.images.isEmpty()) {
            return ""
        }

        return this.images.first().src!!
    }
}

