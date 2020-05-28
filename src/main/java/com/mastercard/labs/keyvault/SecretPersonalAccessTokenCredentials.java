package com.mastercard.labs.keyvault;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.microsoft.jenkins.keyvault.SecretStringCredentials;
import hudson.Extension;
import hudson.util.Secret;
import io.jenkins.plugins.gitlabserverconfig.credentials.PersonalAccessToken;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

public class SecretPersonalAccessTokenCredentials extends SecretStringCredentials implements PersonalAccessToken {
    private static final long serialVersionUID = 1L;

    @DataBoundConstructor
    public SecretPersonalAccessTokenCredentials(
            CredentialsScope scope,
            String id,
            String description,
            String servicePrincipalId,
            String secretIdentifier) {
        super(scope, id, description, servicePrincipalId, secretIdentifier);
    }

    @Nonnull
    @Override
    public Secret getToken() {
        return getSecret();
    }

    @Extension
    public static class DescriptorImpl extends SecretStringCredentials.DescriptorImpl {

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public String getDisplayName() {

            return Messages.SecretPersonalAccessToken_Credentials_Display_Name();
        }
    }
}
