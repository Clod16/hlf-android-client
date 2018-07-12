package it.eng.hlf.android.client.helper;

import it.eng.hlf.android.client.exception.HLFClientException;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;
import java.util.regex.Pattern;

/**
 * @author ascatox
 */
public class EventHandler {

    private final static Logger log = LoggerFactory.getLogger(EventHandler.class);
    private static final String EXPECTED_EVENT_NAME = "event";
    private String chaincodeEventListenerHandle;

    private static EventHandler ourInstance;

    public static EventHandler getInstance() throws HLFClientException, InvalidArgumentException {
        if (ourInstance == null || ourInstance.chaincodeEventListenerHandle == null) { //1
            synchronized (EventHandler.class) {
                if (ourInstance == null) {  //2
                    ourInstance = new EventHandler();
                }
            }
        }
        return ourInstance;
    }

    private EventHandler() {
    }

    class ChaincodeEventCapture { //A test class to capture chaincode events
        final String handle;
        final BlockEvent blockEvent;
        final ChaincodeEvent chaincodeEvent;

        ChaincodeEventCapture(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
            this.handle = handle;
            this.blockEvent = blockEvent;
            this.chaincodeEvent = chaincodeEvent;
        }
    }

    private Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>(); // Test list to capture chaincode events.

    public void register(Channel channel, String eventName) throws InvalidArgumentException {
        // Register a chaincode event listener that will trigger for any chaincode id and only for EXPECTED_EVENT_NAME event.
        String event = EXPECTED_EVENT_NAME;
        if (null != eventName && !event.isEmpty())
            event = eventName;
        chaincodeEventListenerHandle = channel.registerChaincodeEventListener(Pattern.compile(".*"),
                Pattern.compile(Pattern.quote(event)),
                (handle, blockEvent, chaincodeEvent) -> {

                    chaincodeEvents.add(new ChaincodeEventCapture(handle, blockEvent, chaincodeEvent));

                    String es = blockEvent.getPeer() != null ? blockEvent.getPeer().getName() : blockEvent.getEventHub().getName();
                    log.info("RECEIVED Chaincode event with handle: %s, chaincode id: %s, chaincode event name: %s, "
                                    + "transaction id: %s, event payload: \"%s\", from eventhub: %s",
                            handle, chaincodeEvent.getChaincodeId(),
                            chaincodeEvent.getEventName(),
                            chaincodeEvent.getTxId(),
                            new String(chaincodeEvent.getPayload()), es);
                });

    }

    public Vector<ChaincodeEventCapture> getChaincodeEvents() {
        return chaincodeEvents;
    }

    public void unregister(Channel channel) throws InvalidArgumentException {
        channel.unregisterChaincodeEventListener(chaincodeEventListenerHandle);
        chaincodeEventListenerHandle = null;
    }


}
