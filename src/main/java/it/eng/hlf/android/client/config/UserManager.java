package it.eng.hlf.android.client.config;

import it.eng.hlf.android.client.exception.HLFClientException;
import it.eng.hlf.android.client.helper.ChannelInitializationManager;
import it.eng.hlf.android.client.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

/**
 * @author ascatox
 */
public class UserManager {
    private final static Logger log = LoggerFactory.getLogger(UserManager.class);

    private static UserManager instance;
    private Configuration configuration;
    private Organization organization;

    private UserManager(Configuration configuration, Organization organization) {
        this.configuration = configuration;
        this.organization = organization;
    }

    public static UserManager getInstance(Configuration configuration, Organization organization) throws HLFClientException, InvalidArgumentException {
        if (instance == null || !instance.organization.equals(organization)) { //1
            synchronized (ChannelInitializationManager.class) {
                if (instance == null || !instance.organization.equals(organization)) {  //2
                    instance = new UserManager(configuration, organization);
                }
            }
        }
        return instance;
    }


    public void completeUsers() throws HLFClientException {
        try {
            Set<User> users = organization.getUsers();
            for (User user : users) {
               doCompleteUser(user);
            }
        } catch (IOException | NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e.getMessage());
            throw new HLFClientException(e);
        }
    }


    private void doCompleteUser(User user) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException, HLFClientException {
        user.setMspId(organization.getMspID());
        File certConfigPath = ExternalStorageReader.getCertConfigPath(this.configuration.getContext(),organization.getDomainName(), user.getName(), configuration.getCryptoconfigdir());
        String certificate = new String(IOUtils.toByteArray(new FileInputStream(certConfigPath)), ConfigManager.UTF_8);
        File fileSk = Utils.findFileSk(this.configuration.getContext(), organization.getDomainName(), user.getName(), configuration.getCryptoconfigdir());
        PrivateKey privateKey = Utils.getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(fileSk)));
        user.setEnrollment(new Enrollment(privateKey, certificate));
    }


}
