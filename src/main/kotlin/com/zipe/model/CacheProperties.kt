package com.zipe.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "application.cache")
data class CacheProperties(
    var enable: Boolean = true,
    var key: Map<String, String> = mapOf()
)
