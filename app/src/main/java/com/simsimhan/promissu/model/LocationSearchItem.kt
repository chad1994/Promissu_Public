package com.simsimhan.promissu.model


data class LocationSearch(
        val status: String,
        val meta: LocationSearchMeta,
        val places: List<LocationSearchItem>,
        val errorMessage: String
)

data class LocationSearchMeta(
        val totalCount: Int,
        val count: Int
)

data class LocationSearchItem(
        val name: String,
        val road_address: String,
        val jibun_address: String,
        val phone_number: String,
        val x: String, //127....
        val y: String, // 36....
        val distance: String
)