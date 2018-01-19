package com.autolearn.pro.Log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultLoggerFactory {
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    public static Logger getDefaultLogger()
    {
        return logger;
    }
}
