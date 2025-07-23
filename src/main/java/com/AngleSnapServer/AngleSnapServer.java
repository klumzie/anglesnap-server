package me.contaria.anglesnapserver;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AngleSnapServer implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("anglesnap-server");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing AngleSnap Server...");
        new ArrowTracker().register();
    }
}