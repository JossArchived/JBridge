package me.josscoder.jbridge.waterdogpe;

import me.josscoder.jbridge.logger.ILogger;
import dev.waterdog.waterdogpe.logger.Color;
import org.apache.logging.log4j.Logger;

public class WaterdogPELogger implements ILogger {

    private final Logger pluginLogger = JBridgeWaterdogPE.getInstance().getLogger();

    @Override
    public void info(String message) {
        pluginLogger.info(Color.GREEN + message);
    }

    @Override
    public void warn(String message) {
        pluginLogger.warn(Color.AQUA + message);
    }

    @Override
    public void debug(String message) {
        pluginLogger.info(Color.DARK_BLUE + "[DEBUG] " + Color.WHITE + message);
    }

    @Override
    public void error(String message) {
        pluginLogger.error(message);
    }
}
