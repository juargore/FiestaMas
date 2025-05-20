package com.universal.fiestamas.domain.models.request

data class FilterRequest(
    val filters: List<Filters>
)

data class Filters(
    val fieldPath: String,
    val opStr: String,
    val value: Any
)

