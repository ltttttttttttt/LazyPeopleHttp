package com.lt.lazy_people_http.common

import kotlinx.serialization.Serializable

/**
 * creator: lt  2023/3/8  lt.dygzs@qq.com
 * effect :
 * warning:
 */
@Serializable
class MData(
    var cityInfo: CityInfo? = null,
    var `data`: Data? = null,
    var date: String? = null,
    var message: String? = null,
    var status: Int = 0,
    var time: String? = null
) {
    @Serializable
    class CityInfo(
        var city: String? = null,
        var cityId: String? = null,
        var parent: String? = null,
        var updateTime: String? = null
    )

    @Serializable
    class Data(
        var forecast: List<Forecast?>? = null,
        var ganmao: String? = null,
        var pm10: Double = 0.0,
        var pm25: Double = 0.0,
        var quality: String? = null,
        var shidu: String? = null,
        var wendu: String? = null,
        var yesterday: Yesterday? = null
    ) {
        @Serializable
        class Forecast(
            var aqi: Double = 0.0,
            var date: String? = null,
            var fl: String? = null,
            var fx: String? = null,
            var high: String? = null,
            var low: String? = null,
            var notice: String? = null,
            var sunrise: String? = null,
            var sunset: String? = null,
            var type: String? = null,
            var week: String? = null,
            var ymd: String? = null
        )

        @Serializable
        class Yesterday(
            var aqi: Double = 0.0,
            var date: String? = null,
            var fl: String? = null,
            var fx: String? = null,
            var high: String? = null,
            var low: String? = null,
            var notice: String? = null,
            var sunrise: String? = null,
            var sunset: String? = null,
            var type: String? = null,
            var week: String? = null,
            var ymd: String? = null
        )
    }
}