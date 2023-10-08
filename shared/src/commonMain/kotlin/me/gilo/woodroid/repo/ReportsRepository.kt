package me.gilo.woodroid.repo

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.ReportAPI
import me.gilo.woodroid.models.filters.ReportsDateFilter
import me.gilo.woodroid.models.report.CouponsTotal
import me.gilo.woodroid.models.report.CustomersTotal
import me.gilo.woodroid.models.report.OrdersTotal
import me.gilo.woodroid.models.report.ProductsTotal
import me.gilo.woodroid.models.report.ReviewsTotal
import me.gilo.woodroid.models.report.SalesTotal
import me.gilo.woodroid.models.report.TopSellerProducts

class ReportsRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: ReportAPI = ktorfit.create()

    fun sales(): Call<List<SalesTotal>> {
        return apiService.sales()
    }

    fun sales(reportsDateFilter: ReportsDateFilter): Call<List<SalesTotal>> {
        return apiService.sales(reportsDateFilter.filters)
    }

    fun top_sellers(): Call<List<TopSellerProducts>> {
        return apiService.top_sellers()
    }

    fun top_sellers(reportsDateFilter: ReportsDateFilter): Call<List<TopSellerProducts>> {
        return apiService.top_sellers(reportsDateFilter.filters)
    }

    fun coupons_totals(): Call<List<CouponsTotal>> {
        return apiService.coupons_totals()
    }

    fun customer_totals(): Call<List<CustomersTotal>> {
        return apiService.customers_totals()
    }

    fun order_totals(): Call<List<OrdersTotal>> {
        return apiService.orders_totals()
    }

    fun product_totals(): Call<List<ProductsTotal>> {
        return apiService.products_totals()
    }

    fun review_totals(): Call<List<ReviewsTotal>> {
        return apiService.reviews_totals()
    }

}
