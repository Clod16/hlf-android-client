package it.eng.hlf.android.client.exception;


import it.eng.hlf.android.client.helper.Function;

public class HLFClientException extends Exception {

    public HLFClientException() {
        super();
    }

    public HLFClientException(String message) {
        super(message);
    }

    public HLFClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HLFClientException(Throwable cause) {
        super(cause);
    }

    protected HLFClientException(String message, Throwable cause, boolean enableSuppression, boolean
            writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public HLFClientException(Function function, String message) {
        super(function.name() + " " + message);
    }


}

