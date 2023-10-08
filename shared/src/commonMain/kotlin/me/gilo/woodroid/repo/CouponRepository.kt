package me.gilo.woodroid.repo

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.CouponAPI
import me.gilo.woodroid.models.Coupon
import me.gilo.woodroid.models.filters.CouponFilter

class CouponRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: CouponAPI = ktorfit.create()

    fun create(coupon: Coupon): Call<Coupon> {
        return apiService.create(coupon)
    }


    fun coupon(id: Int): Call<Coupon> {
        return apiService.view(id)
    }

    fun coupons(): Call<List<Coupon>> {
        return apiService.list()
    }

    fun coupons(couponFilter: CouponFilter): Call<List<Coupon>> {
        return apiService.filter(couponFilter.filters)
    }

    fun update(id: Int, coupon: Coupon): Call<Coupon> {
        return apiService.update(id, coupon)
    }

    fun delete(id: Int): Call<Coupon> {
        return apiService.delete(id)
    }

    fun delete(id: Int, force: Boolean): Call<Coupon> {
        return apiService.delete(id, force)
    }


}
