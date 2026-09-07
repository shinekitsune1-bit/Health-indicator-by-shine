package com.healthindicator;

import net.fabricmc.api.ClientModInitializer;

public class HealthIndicatorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("Health Indicator loaded!");
    }
}
