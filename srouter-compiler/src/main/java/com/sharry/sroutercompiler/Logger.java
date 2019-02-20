package com.sharry.sroutercompiler;


import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * IDEA console print log.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/17 23:46
 */
public class Logger {

    private static final String PREFIX_OF_LOGGER = "SRouterCompiler >>> ";

    private Messager mMsg;

    public Logger(Messager messager) {
        mMsg = messager;
    }

    /**
     * Print info log.
     */
    public void i(CharSequence info) {
        if (!TextUtils.isEmpty(info)) {
            mMsg.printMessage(Diagnostic.Kind.NOTE, PREFIX_OF_LOGGER + info);
        }
    }

    /**
     * Print waring log.
     */
    public void w(CharSequence warning) {
        if (!TextUtils.isEmpty(warning)) {
            mMsg.printMessage(Diagnostic.Kind.WARNING, PREFIX_OF_LOGGER + warning);
        }
    }

    /**
     * Print error log.
     */
    public void e(CharSequence error) {
        if (!TextUtils.isEmpty(error)) {
            mMsg.printMessage(Diagnostic.Kind.ERROR, PREFIX_OF_LOGGER +
                    "An exception is encountered, [" + error + "]");
        }
    }

    /**
     * Print exception.
     */
    public void e(Throwable error) {
        if (null != error) {
            mMsg.printMessage(Diagnostic.Kind.ERROR, PREFIX_OF_LOGGER +
                    "An exception is encountered, [" + error.getMessage() + "]" + "\n" +
                    formatStackTrace(error.getStackTrace()));
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element: stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
