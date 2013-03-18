package org.eclipse.osee.ote;

import java.util.concurrent.ExecutionException;

/**
 * This is an API for loading bundles into the OTE Server.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public interface OTEApi {
   OTEFuture<OTEConfigurationStatus> loadConfiguration(OTEConfiguration configuration, OTEStatusCallback<OTEConfigurationStatus> callable) throws InterruptedException, ExecutionException;
   OTEFuture<OTEConfigurationStatus> resetConfiguration(OTEStatusCallback<OTEConfigurationStatus> callable) throws InterruptedException, ExecutionException;
   OTEFuture<OTEConfigurationStatus> getConfiguration();
}
