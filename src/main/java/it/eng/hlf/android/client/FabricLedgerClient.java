package it.eng.hlf.android.client;

import android.content.Context;

import it.eng.hlf.android.client.config.ConfigManager;
import it.eng.hlf.android.client.config.Configuration;
import it.eng.hlf.android.client.config.Organization;
import it.eng.hlf.android.client.exception.HLFClientException;
import it.eng.hlf.android.client.helper.LedgerInteractionHelper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;


final public class FabricLedgerClient implements LedgerClient {

    private final static Logger log = LogManager.getLogger(FabricLedgerClient.class);

    private LedgerInteractionHelper ledgerInteractionHelper;
    private ConfigManager configManager;
    //private Validator validator;

    public FabricLedgerClient(Context context) throws HLFClientException {
        doLedgerClient(context);
    }


    private void doLedgerClient(Context context) throws HLFClientException {
        try {
            //ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            //validator = factory.getValidator();
            configManager = ConfigManager.getInstance(context);
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
/*

    public void storeProcessStepRouting(Collection<ChassisDTO> chassisDTOs) throws HLFClientException {
        if (chassisDTOs == null || chassisDTOs.isEmpty())
            throw new HLFClientException(Function.storeProcessStepRouting.name() + " is in error, No input data!");
        for (ChassisDTO chassisDTO : chassisDTOs) {
            Set<ConstraintViolation<ChassisDTO>> violations = validator.validate(chassisDTO);
            Utils.getMessageViolations(violations);
        }
        String json = JsonConverter.convertToJson(chassisDTOs);
        final String payload = doInvokeByJson(Function.storeProcessStepRouting, json);
        log.debug("Payload retrieved: " + payload);
    }


    @Override
    public void storeProcessStepResult(ProcessStepResultDTO processStepResultDTO) throws HLFClientException {
        if (processStepResultDTO == null || processStepResultDTO.getOperationResults().isEmpty())
            throw new HLFClientException(Function.storeProcessStepResult.name() + " is in error, No input data!");
        String json = JsonConverter.convertToJson(processStepResultDTO);
        final String payload = doInvokeByJson(Function.storeProcessStepResult, json);
        log.debug("Payload retrieved: " + payload);
    }

    @Override
    public void storeProcessStepRouting(String json) throws HLFClientException {
        if (null == json || json.isEmpty())
            throw new HLFClientException(Function.storeProcessStepRouting.name() + " is in error, No input data!");
        final String payload = doInvokeByJson(Function.storeProcessStepRouting, json);
        log.debug("Payload retrieved: " + payload);

    }

    @Override
    public void storeProcessStepResult(String json) throws HLFClientException {
        if (null == json || json.isEmpty())
            throw new HLFClientException(Function.storeProcessStepResult.name() + " is in error, No input data!");
        final String payload = doInvokeByJson(Function.storeProcessStepResult, json);
        log.debug("Payload retrieved: " + payload);

    }

    public final Collection<ChassisDTO> getProcessStepRouting(String component, String subComponent) throws HLFClientException {
        if (StringUtils.isEmpty(component) || StringUtils.isEmpty(subComponent))
            throw new HLFClientException(Function.getProcessStepRouting.name() + " is in error, No input data!");
        List<String> args = new ArrayList<>();
        args.add(component);
        args.add(subComponent);
        return doChassisQueryByJson(Function.getProcessStepRouting, args, true);
    }

    @Override
    public final ChassisDTO getProcessStepRouting(String chassisID, String component, String subComponent) throws HLFClientException {
        if (StringUtils.isEmpty(component) || StringUtils.isEmpty(subComponent) || StringUtils.isEmpty(chassisID))
            throw new HLFClientException(Function.getProcessStepRouting.name() + " is in error, No input data!");
        List<String> args = new ArrayList<>();
        args.add(chassisID);
        args.add(component);
        args.add(subComponent);
        List<ChassisDTO> chassisDTOS = doChassisQueryByJson(Function.getProcessStepRouting, args, false);
        if (null != chassisDTOS && !chassisDTOS.isEmpty())
            return chassisDTOS.get(0);
        return null;

    }

    @Override
    public final Collection<ProcessStep> getProcessStep(String chassisID, String component, String subComponent, String workCellResourceID) throws HLFClientException {
        if (StringUtils.isEmpty(component) || StringUtils.isEmpty(subComponent) || StringUtils.isEmpty(chassisID) || StringUtils.isEmpty(workCellResourceID))
            throw new HLFClientException(Function.getProcessStep.name() + " is in error, No input data!");
        List<String> args = new ArrayList<>();
        args.add(chassisID);
        args.add(component);
        args.add(subComponent);
        args.add(workCellResourceID);
        return doProcessStepQueryByJson(Function.getProcessStep, args, true);
    }

    @Override
    public Collection<ProcessStep> getProcessStep(String chassisID, String component, String subComponent) throws HLFClientException {
        if (StringUtils.isEmpty(component) || StringUtils.isEmpty(subComponent) || StringUtils.isEmpty(chassisID))
            throw new HLFClientException(Function.getProcessStep.name() + " is in error, No input data!");
        List<String> args = new ArrayList<>();
        args.add(chassisID);
        args.add(component);
        args.add(subComponent);
        return doProcessStepQueryByJson(Function.getProcessStep, args, true);
    }

    @Override
    public final ProcessStepResultDTO getProcessStepResult(String chassisID, String component, String subComponent, String workcellResourceID) throws HLFClientException {
        if (StringUtils.isEmpty(component) || StringUtils.isEmpty(subComponent) || StringUtils.isEmpty(chassisID))
            throw new HLFClientException(Function.getProcessStepResult.name() + " is in error, No input data!");
        List<String> args = new ArrayList<>();
        args.add(chassisID);
        args.add(component);
        args.add(subComponent);
        args.add(workcellResourceID);
        return (ProcessStepResultDTO) doProcessStepResultQueryByJson(Function.getProcessStepResult, args);
    }


    private String doInvokeByJson(Function fcn, String json) throws HLFClientException {
        List<String> args = new ArrayList<>();
        args.add(json);
        final InvokeReturn invokeReturn = ledgerInteractionHelper.invokeChaincode(fcn.name(), args);
        try {
            log.debug("BEFORE -> Store Completable Future at "+System.currentTimeMillis());
            invokeReturn.getCompletableFuture().get(configManager.getConfiguration().getTimeout(), TimeUnit.MILLISECONDS);
            log.debug("AFTER -> Store Completable Future at "+System.currentTimeMillis());
            final String payload = invokeReturn.getPayload();
            return payload;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error(fcn.name().toUpperCase() + " " + e.getMessage());
            throw new HLFClientException(fcn.name() + " " + e.getMessage());
        }
    }


    private List<ChassisDTO> doChassisQueryByJson(Function fcn, List<String> args, boolean isCollection) throws HLFClientException {
        List<ChassisDTO> chassisDTOS = new ArrayList<>();
        try {
            final List<QueryReturn> queryReturns = ledgerInteractionHelper.queryChainCode(fcn.name(), args, null);
            for (QueryReturn queryReturn : queryReturns) {
                if (isCollection)
                    chassisDTOS = (List<ChassisDTO>) JsonConverter.convertFromJson(queryReturn.getPayload(), ChassisDTO.class, isCollection);
                else {
                    ChassisDTO chassisDTO = (ChassisDTO) JsonConverter.convertFromJson(queryReturn.getPayload(), ChassisDTO.class, isCollection);
                    chassisDTOS.add(chassisDTO);
                }
            }
            return chassisDTOS;
        } catch (Exception e) {
            log.error(fcn.name() + " " + e.getMessage());
            throw new HLFClientException(fcn, e.getMessage());
        }
    }


    private ProcessStepResultDTO doProcessStepResultQueryByJson(Function fcn, List<String> args) throws HLFClientException {
        ProcessStepResultDTO processStepResultDTO = null;
        try {
            final List<QueryReturn> queryReturns = ledgerInteractionHelper.queryChainCode(fcn.name(), args, null);
            for (QueryReturn queryReturn : queryReturns) {
                processStepResultDTO = (ProcessStepResultDTO) JsonConverter.convertFromJson(queryReturn.getPayload(), ProcessStepResultDTO.class, false);
            }
            return processStepResultDTO;
        } catch (Exception e) {
            log.error(fcn.name() + " " + e.getMessage());
            throw new HLFClientException(fcn, e.getMessage());
        }
    }

    private Collection<ProcessStep> doProcessStepQueryByJson(Function fcn, List<String> args, boolean isCollection) throws HLFClientException {
        Collection<ProcessStep> processSteps = new ArrayList<>();
        try {
            final List<QueryReturn> queryReturns = ledgerInteractionHelper.queryChainCode(fcn.name(), args, null);
            for (QueryReturn queryReturn : queryReturns) {
                if (isCollection)
                    processSteps = (Collection<ProcessStep>) JsonConverter.convertFromJson(queryReturn.getPayload(), ProcessStep.class, isCollection);
                else {
                    ProcessStep processStep = (ProcessStep) JsonConverter.convertFromJson(queryReturn.getPayload(), ProcessStep.class, isCollection);
                    processSteps.add(processStep);
                }
            }
            return processSteps;
        } catch (Exception e) {
            log.error(fcn.name() + " " + e.getMessage());
            throw new HLFClientException(fcn, e.getMessage());
        }
    }
*/


}
