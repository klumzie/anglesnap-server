package com.AngleSnapServer;

import me.contaria.anglesnapserver.ArrowTracker;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AngleSnapServer implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("anglesnap-server");

    /**
     * This method runs once when the mod is first loaded.
     */
    @Override
    public void onInitialize() {
        // This is where you run your mod's setup code.
        LOGGER.info("Initializing Angle Snap Server...");

        // Register the arrow tracking logic.
        new ArrowTracker().register();
    }
}