package com.mastercard.labs.keyvault;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.microsoft.jenkins.keyvault.SecretStringCredentials;
import hudson.Extension;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import java.util.Map;

public class SecretUsernamePasswordCredentials extends SecretStringCredentials implements StandardUsernamePasswordCredentials {
    private static final long serialVersionUID = 1L;

    private Secret username = null;
    private Secret password = null;

    @DataBoundConstructor
    public SecretUsernamePasswordCredentials(
            CredentialsScope scope,
            String id,
            String description,
            String servicePrincipalId,
            String secretIdentifier) {
        super(scope, id, description, servicePrincipalId, secretIdentifier);
    }

    @Nonnull
    @Override
    public String getUsername() {
        loadAzureSecret();
        return username.getPlainText();
    }

    @Nonnull
    @Override
    public Secret getPassword() {
        loadAzureSecret();
        return password;
    }

    private synchronized void loadAzureSecret() {
        final String fieldUsername = "username";
        final String fieldPassword = "password";

        if (username == null) {
            try {
                final KeyVaultSecret secretBundle = getKeyVaultSecret();
                Yaml parser = new Yaml();
                Map<String, Object> parsed = parser.load(secretBundle.getValue());
                username = Secret.fromString(parsed.get(fieldUsername).toString());
                password = Secret.fromString(parsed.get(fieldPassword).toString());
            } catch (Exception e) {
                username = null;
                password = null;
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

            return Messages.SecretUsernamePassword_Credentials_Display_Name();
        }
    }
}
