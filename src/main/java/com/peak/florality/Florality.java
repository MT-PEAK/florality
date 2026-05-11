package com.peak.florality;

import com.peak.florality.init.Entities;
import net.fabricmc.api.ModInitializer;

public class Florality implements ModInitializer {
    public static final String MODID = "florality";

    @Override
    public void onInitialize() {
        Entities.init();
    }
}
