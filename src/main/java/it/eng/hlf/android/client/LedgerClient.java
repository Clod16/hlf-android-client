package it.eng.hlf.android.client;

import it.eng.hlf.android.client.exception.HLFClientException;

/**
 * @author ascatox
 */
public interface LedgerClient {

    String getData() throws HLFClientException;

    void putData(String data) throws HLFClientException;

}
