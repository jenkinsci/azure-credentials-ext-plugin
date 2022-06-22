package com.mastercard.labs.keyvault;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.cloudbees.jenkins.plugins.sshcredentials.SSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.microsoft.jenkins.keyvault.SecretStringCredentials;
import hudson.Extension;
import hudson.util.Secret;
import org.jenkins.ui.icon.Icon;
import org.jenkins.ui.icon.IconSet;
import org.jenkins.ui.icon.IconType;
import org.kohsuke.stapler.DataBoundConstructor;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SecretSSHUserPrivateKeyCredentials extends SecretStringCredentials implements SSHUserPrivateKey {
    private static final long serialVersionUID = 1L;

    private Secret username = null;
    private Secret privateKey = null;

    @DataBoundConstructor
    public SecretSSHUserPrivateKeyCredentials(
            CredentialsScope scope,
            String id,
            String description,
            String servicePrincipalId,
            String secretIdentifier) {
        super(scope, id, description, servicePrincipalId, secretIdentifier);
    }

    @Nonnull
    @Override
    public String getPrivateKey() {
        processAzureSecret();
        return privateKey.getPlainText();
    }

    @Override
    public Secret getPassphrase() {
        return null;
    }

    @Nonnull
    @Override
    public synchronized List<String> getPrivateKeys() {
        return Collections.singletonList(getPrivateKey());
    }

    @Nonnull
    @Override
    public String getUsername() {
        processAzureSecret();
        return username.getPlainText();
    }


    private synchronized void processAzureSecret() {
        final String fieldUsername = "username";
        final String fieldPrivateKey = "privateKey";

        if (username == null) {
            try {
                final KeyVaultSecret secretBundle = getKeyVaultSecret();
                Yaml parser = new Yaml(new SafeConstructor());
                Map<String, Object> parsed = parser.load(secretBundle.getValue());
                username = Secret.fromString(parsed.get(fieldUsername).toString());
                privateKey = Secret.fromString(parsed.get(fieldPrivateKey).toString());
            } catch (Exception e) {
                username = null;
                privateKey = null;
                throw e;
            }
        }
    }

    @Extension
    public static class DescriptorImpl extends SecretStringCredentials.DescriptorImpl {

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public String getDisplayName() {

            return Messages.SecretSSHUserPrivateKey_Credentials_Display_Name();
        }

        /**
         * {@inheritDoc}
         */
        public String getIconClassName() {
            return "icon-fingerprint";
        }
    }
}
