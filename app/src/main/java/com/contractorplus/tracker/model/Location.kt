package com.contractorplus.tracker.model

data class LocationInfo(
    var startX: String,
    var startY: String,
    var endX: String,
    var endY: String,
    var time: String
){
    constructor() : this("0.0", "0.0", "0.0", "0.0", "0") {}
}
