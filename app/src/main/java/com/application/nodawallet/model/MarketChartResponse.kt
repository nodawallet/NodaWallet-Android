package com.application.nodawallet.model


data class MarketChartResponse(val prices: List<List<Number>>?,val market_caps:List<List<Float>>?,val total_volumes:List<List<Float>>?)
