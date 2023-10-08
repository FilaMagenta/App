package me.gilo.woodroid

import me.gilo.woodroid.data.ApiVersion
import me.gilo.woodroid.repo.CouponRepository
import me.gilo.woodroid.repo.CustomerRepository
import me.gilo.woodroid.repo.OrderRepository
import me.gilo.woodroid.repo.PaymentGatewayRepository
import me.gilo.woodroid.repo.ProductRepository
import me.gilo.woodroid.repo.ReportsRepository
import me.gilo.woodroid.repo.SettingsRepository
import me.gilo.woodroid.repo.ShippingMethodRepository
import me.gilo.woodroid.repo.order.OrderNoteRepository
import me.gilo.woodroid.repo.order.RefundRepository
import me.gilo.woodroid.repo.product.AttributeRepository
import me.gilo.woodroid.repo.product.AttributeTermRepository
import me.gilo.woodroid.repo.product.CategoryRepository
import me.gilo.woodroid.repo.product.ReviewRepository
import me.gilo.woodroid.repo.product.ShippingClassRepository
import me.gilo.woodroid.repo.product.TagRepository
import me.gilo.woodroid.repo.product.VariationRepository

class WooCommerce(siteUrl: String, apiVersion: ApiVersion, consumerKey: String, consumerSecret: String) {
    companion object {
        val API_V1 = ApiVersion.API_VERSION1
        val API_V2 = ApiVersion.API_VERSION2
        val API_V3 = ApiVersion.API_VERSION3
    }

    private val orderNoteRepository: OrderNoteRepository
    private val refundRepository: RefundRepository
    private val attributeRepository: AttributeRepository
    private val attributeTermRepository: AttributeTermRepository
    private val categoryRepository: CategoryRepository
    private val shippingClassRepository: ShippingClassRepository
    private val tagRepository: TagRepository
    private val variationRepository: VariationRepository
    private val couponRepository: CouponRepository
    private val customerRepository: CustomerRepository
    private val orderRepository: OrderRepository
    private val productRepository: ProductRepository
    private val reviewRepository: ReviewRepository
    private val reportsRepository: ReportsRepository
    private val paymentGatewayRepository: PaymentGatewayRepository
    private val settingsRepository: SettingsRepository
    private val shippingMethodRepository: ShippingMethodRepository

    init {
        val baseUrl = "$siteUrl/wp-json/wc/v$apiVersion/"

        orderNoteRepository = OrderNoteRepository(baseUrl, consumerKey, consumerSecret)
        refundRepository = RefundRepository(baseUrl, consumerKey, consumerSecret)
        attributeRepository = AttributeRepository(baseUrl, consumerKey, consumerSecret)
        attributeTermRepository = AttributeTermRepository(baseUrl, consumerKey, consumerSecret)
        categoryRepository = CategoryRepository(baseUrl, consumerKey, consumerSecret)
        shippingClassRepository = ShippingClassRepository(baseUrl, consumerKey, consumerSecret)
        tagRepository = TagRepository(baseUrl, consumerKey, consumerSecret)
        variationRepository = VariationRepository(baseUrl, consumerKey, consumerSecret)
        couponRepository = CouponRepository(baseUrl, consumerKey, consumerSecret)
        customerRepository = CustomerRepository(baseUrl, consumerKey, consumerSecret)
        orderRepository = OrderRepository(baseUrl, consumerKey, consumerSecret)
        productRepository = ProductRepository(baseUrl, consumerKey, consumerSecret)
        reportsRepository = ReportsRepository(baseUrl, consumerKey, consumerSecret)
        reviewRepository = ReviewRepository(baseUrl, consumerKey, consumerSecret)
        paymentGatewayRepository = PaymentGatewayRepository(baseUrl, consumerKey, consumerSecret)
        settingsRepository = SettingsRepository(baseUrl, consumerKey, consumerSecret)
        shippingMethodRepository = ShippingMethodRepository(baseUrl, consumerKey, consumerSecret)

        println("baseURL: $baseUrl")
        println("Consumer key: $consumerKey, Consumer Secret: $consumerSecret")
    }

    fun OrderNoteRepository(): OrderNoteRepository {
        return orderNoteRepository
    }

    fun RefundRepository(): RefundRepository {
        return refundRepository
    }

    fun AttributeRepository(): AttributeRepository {
        return attributeRepository
    }

    fun AttributeTermRepository(): AttributeTermRepository {
        return attributeTermRepository
    }

    fun CategoryRepository(): CategoryRepository {
        return categoryRepository
    }

    fun ShippingClassRepository(): ShippingClassRepository {
        return shippingClassRepository
    }

    fun TagRepository(): TagRepository {
        return tagRepository
    }

    fun VariationRepository(): VariationRepository {
        return variationRepository
    }

    fun CouponRepository(): CouponRepository {
        return couponRepository
    }

    fun CustomerRepository(): CustomerRepository {
        return customerRepository
    }

    fun OrderRepository(): OrderRepository {
        return orderRepository
    }

    fun ProductRepository(): ProductRepository {
        return productRepository
    }

    fun ReviewRepository(): ReviewRepository {
        return reviewRepository
    }

    fun ReportsRepository(): ReportsRepository {
        return reportsRepository
    }

    fun PaymentGatewayRepository(): PaymentGatewayRepository {
        return paymentGatewayRepository
    }

    fun SettingsRepository(): SettingsRepository {
        return settingsRepository
    }

    fun ShippingMethodRepository(): ShippingMethodRepository {
        return shippingMethodRepository
    }

    class Builder {
        private lateinit var siteUrl: String
        private lateinit var apiVersion: ApiVersion
        private lateinit var consumerKey: String
        private lateinit var consumerSecret: String

        fun setSiteUrl(siteUrl: String): Builder {
            this.siteUrl = siteUrl
            return this
        }

        fun setApiVersion(apiVersion: ApiVersion): Builder {
            this.apiVersion = apiVersion
            return this
        }

        fun setConsumerKey(consumerKey: String): Builder {
            this.consumerKey = consumerKey
            return this
        }

        fun setConsumerSecret(consumerSecret: String): Builder {
            this.consumerSecret = consumerSecret
            return this
        }

        fun build(): WooCommerce {
            return WooCommerce(siteUrl, apiVersion, consumerKey, consumerSecret)
        }
    }
}
