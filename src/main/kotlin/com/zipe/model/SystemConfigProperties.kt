package com.zipe.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "system")
data class SystemConfigProperties(
    var jobUsingDatabase: Boolean = false
)

