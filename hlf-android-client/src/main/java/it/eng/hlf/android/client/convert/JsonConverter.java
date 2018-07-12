package it.eng.hlf.android.client.convert;


import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.hlf.android.client.exception.HLFClientException;
import it.eng.hlf.android.client.utils.Utils;


public class JsonConverter {
    private final static Logger log = LoggerFactory.getLogger(JsonConverter.class);

    public enum TypeOfModel {
       // ChassisDTO, ProcessStep, ProcessStepResultDTO
    }


    public static String convertToJson(Object obj) throws HLFClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE); //This property put data in upper camel case
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HLFClientException(e);
        }
    }

    public static Object convertFromJson(String json, Class clazz, boolean isCollection) throws HLFClientException {
        try {
            if (Utils.isEmpty(json))
                throw new HLFClientException("Json data is EMPTY");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true); //This property serialize/deserialize not considering the case of fields
            /*if (isCollection) {
                ObjectReader objectReader = null;
                if (clazz.getSimpleName().equals(TypeOfModel.ChassisDTO.name()))
                    objectReader = mapper.reader().forType(new TypeReference<List<ChassisDTO>>() {
                    });
                else if (clazz.getSimpleName().equals(TypeOfModel.ProcessStep.name()))
                    objectReader = mapper.reader().forType(new TypeReference<List<ProcessStep>>() {
                    });
                return objectReader.readValue(json);
            }*/
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HLFClientException(e);
        }
    }

}


