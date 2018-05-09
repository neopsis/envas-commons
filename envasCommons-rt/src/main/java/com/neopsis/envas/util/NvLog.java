/*
 * @(#)NvLog.java   22.11.09
 *
 * Copyright (c) 2007-2012 Neopsis GmbH
 *
 *
 */



package com.neopsis.envas.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logging support class contains static logging methods
 *
 */
public class NvLog {

    public static final Logger logger = Logger.getLogger("envasCommons");

    public static void trace(String message) {

        if (logger.isLoggable(Level.FINE)) {
            logger.fine(message);
        }
    }

    public static void message(String message) {
        logger.info(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void error(String message) {
        logger.severe(message);
    }

    public static void error(String message, Exception ex) {
        logger.log(Level.SEVERE, message, ex);
    }
}
