package me.josscoder.jbridge;

import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.logger.ILogger;

public class NukkitLogger implements ILogger {

    private final PluginLogger logger = JBridgeNukkit.getInstance().getLogger();

    @Override
    public void info(String message) {
        logger.info(TextFormat.GREEN + message);
    }

    @Override
    public void warn(String message) {
        logger.warning(TextFormat.AQUA + message);
    }

    @Override
    public void debug(String message) {
        logger.info(TextFormat.DARK_BLUE + "[DEBUG] " + TextFormat.WHITE + message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }
}
