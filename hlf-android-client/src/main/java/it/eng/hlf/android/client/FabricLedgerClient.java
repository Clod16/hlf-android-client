package it.eng.hlf.android.client;

import android.content.Context;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import it.eng.hlf.android.client.config.ConfigManager;
import it.eng.hlf.android.client.config.Configuration;
import it.eng.hlf.android.client.config.Organization;
import it.eng.hlf.android.client.exception.HLFClientException;
import it.eng.hlf.android.client.helper.Function;
import it.eng.hlf.android.client.helper.InvokeReturn;
import it.eng.hlf.android.client.helper.LedgerInteractionHelper;
import it.eng.hlf.android.client.helper.QueryReturn;


final public class FabricLedgerClient implements LedgerClient {

    private final static Logger log = LogManager.getLogger(FabricLedgerClient.class);

    private LedgerInteractionHelper ledgerInteractionHelper;
    private ConfigManager configManager;

    public FabricLedgerClient() throws HLFClientException {
        doLedgerClient();
    }


    private void doLedgerClient() throws HLFClientException {
        try {
            configManager = ConfigManager.getInstance();
            Configuration configuration = configManager.getConfiguration();
            if (null == configuration || null == configuration.getOrganizations() || configuration.getOrganizations().isEmpty()) {
                log.error("Configuration missing!!! Check you config file!!!");
                throw new HLFClientException("Configuration missing!!! Check you config file!!!");
            }
            List<Organization> organizations = configuration.getOrganizations();
            if (null == organizations || organizations.isEmpty())
                throw new HLFClientException("Organizations missing!!! Check you config file!!!");
            //for (Organization org : organizations) {
            //FIXME multiple Organizations
            ledgerInteractionHelper = new LedgerInteractionHelper(configManager, organizations.get(0));
            //}
        } catch (Exception e) {
            log.error(e);
            throw new HLFClientException(e);
        }
    }

    @Override
    public final String getData(String key ) throws HLFClientException {
        if( key.isEmpty()){
            throw new HLFClientException(Function.getData.name() + "is in error, No input data!");
        }
        List<String> args = new ArrayList<>();
        args.add(key);
       return doQueryByJson(Function.getData, args);
    }

    @Override
    public void putData(String key,  String data) throws HLFClientException {
        if( data == null){
            throw new HLFClientException((Function.putData.name() + "is in error, No input data!"));
        }
        List<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(key);
        stringArrayList.add(data);
        final String payload = doInvokeByJson(Function.putData, stringArrayList);
        log.debug("Payload retrieved: " + payload);
    }


    private String doInvokeByJson(Function fcn, List<String> args) throws HLFClientException {
        final InvokeReturn invokeReturn = ledgerInteractionHelper.invokeChaincode(fcn.name(), args);
        try {
            log.debug("BEFORE -> Store Completable Future at " + System.currentTimeMillis());
            invokeReturn.getCompletableFuture().get(configManager.getConfiguration().getTimeout(), TimeUnit.MILLISECONDS);
            log.debug("AFTER -> Store Completable Future at " + System.currentTimeMillis());
            final String payload = invokeReturn.getPayload();
            return payload;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error(fcn.name().toUpperCase() + " " + e.getMessage());
            throw new HLFClientException(fcn.name() + " " + e.getMessage());
        }
    }

    private String doQueryByJson(Function fcn, List<String> args) throws HLFClientException {
        String data = "";
        try {
            final List<QueryReturn> queryReturns = ledgerInteractionHelper.queryChainCode(fcn.name(), args, null);
            for (QueryReturn queryReturn : queryReturns) {
                data += queryReturn.getPayload();
            }
            return data;
        } catch (Exception e) {
            log.error(fcn.name() + " " + e.getMessage());
            throw new HLFClientException(fcn, e.getMessage());
        }
    }


}
