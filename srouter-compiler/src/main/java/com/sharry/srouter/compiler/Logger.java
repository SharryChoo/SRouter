package com.sharry.srouter.compiler;


import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * IDEA console print log.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/17 23:46
 */
class Logger {

    private static final String PREFIX_OF_LOGGER = "SRouterCompiler >>> ";

    private Messager mMsg;

    Logger(Messager messager) {
        mMsg = messager;
    }

    /**
     * Print info log.
     */
    void i(CharSequence info) {
        if (null != info && info.length() > 0) {
            mMsg.printMessage(Diagnostic.Kind.NOTE, PREFIX_OF_LOGGER + info);
        }
    }

    /**
     * Print waring log.
     */
    void w(CharSequence warning) {
        if (null != warning && warning.length() > 0) {
            mMsg.printMessage(Diagnostic.Kind.WARNING, PREFIX_OF_LOGGER + warning);
        }
    }

    /**
     * Print error log.
     */
    void e(CharSequence error) {
        if (null != error && error.length() > 0) {
            mMsg.printMessage(Diagnostic.Kind.ERROR, PREFIX_OF_LOGGER +
                    "An exception is encountered, [" + error + "]");
        }
    }

    /**
     * Print exception.
     */
    void e(Throwable error) {
        if (null != error) {
            mMsg.printMessage(Diagnostic.Kind.ERROR, PREFIX_OF_LOGGER +
                    "An exception is encountered, [" + error.getMessage() + "]" + "\n" +
                    formatStackTrace(error.getStackTrace()));
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
