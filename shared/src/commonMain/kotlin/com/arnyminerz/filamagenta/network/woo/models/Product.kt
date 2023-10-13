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
    @SerialName("price_html") val priceHtml: String? = null,
    @SerialName("isOn_sale") val isOnSale: Boolean = false,
    val isPurchasable: Boolean = false,
    @SerialName("total_sales") val totalSales: Int = 0,
    val isVirtual: Boolean = false,
    val isDownloadable: Boolean = false,
    val downloads: ArrayList<Download>,
    @SerialName("download_limit") val downloadLimit: Int = 0,
    @SerialName("download_expiry") val downloadExpiry: Int = 0,
    @SerialName("external_url") val externalUrl: String,
    @SerialName("button_text") val buttonText: String,
    @SerialName("tax_status") val taxStatus: String,
    @SerialName("tax_class") val taxClass: String,
    @SerialName("isManage_stock") val isManageStock: Boolean = false,
    @SerialName("stock_quantity") val stockQuantity: Int? = null,
    @SerialName("isIn_stock") val isInStock: Boolean = false,
    val backorders: String,
    @SerialName("isBackorders_allowed") val isBackordersAllowed: Boolean = false,
    val isBackordered: Boolean = false,
    @SerialName("isSold_individually") val isSoldIndividually: Boolean = false,
    val weight: String,
    val dimensions: Dimensions,
    @SerialName("isShipping_required") val isShippingRequired: Boolean = false,
    @SerialName("isShipping_taxable") val isShippingTaxable: Boolean = false,
    @SerialName("shipping_class") val shippingClass: String,
    @SerialName("shipping_class_id") val shippingClassId: Int = 0,
    @SerialName("isReviews_allowed") val isReviewsAllowed: Boolean = false,
    @SerialName("average_rating") val averageRating: String,
    @SerialName("rating_count") val ratingCount: Int = 0,
    @SerialName("related_ids") val relatedIds: ArrayList<Int>,
    @SerialName("upsell_ids") val upsellIds: ArrayList<Int>,
    @SerialName("cross_sell_ids") val crossSellIds: ArrayList<Int>,
    @SerialName("parent_id") val parentId: Int = 0,
    @SerialName("purchase_note") val purchaseNote: String,
    val categories: ArrayList<Category>,
    val tags: ArrayList<Tag>,

    @SerialName("attributes")
    val productAttributes: ArrayList<ProductAttribute>,

    @SerialName("default_attributes") val defaultAttributes: ArrayList<DefaultAttribute>,
    val variations: ArrayList<Int>,
    @SerialName("grouped_products") val groupedProducts: ArrayList<Int>,
    @SerialName("menu_order") val menuOrder: Int = 0,
    @SerialName("meta_data") val metaData: ArrayList<Metadata>,
    val images: ArrayList<Image>
) {

    fun getFeatureImage(): String {
        if (this.images.isEmpty()) {
            return ""
        }

        return this.images.first().src!!
    }
}

