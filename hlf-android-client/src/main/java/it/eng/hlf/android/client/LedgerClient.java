package it.eng.hlf.android.client;

import it.eng.hlf.android.client.exception.HLFClientException;

/**
 * @author ascatox
 */
public interface LedgerClient {


    String getData(String key) throws HLFClientException;

    void putData(String key, String data) throws HLFClientException;
}
