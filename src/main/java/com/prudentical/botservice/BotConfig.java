package com.prudentical.botservice;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "application.bot")
public record BotConfig(Duration coolDown, Duration UpdateFreq) {
    
}