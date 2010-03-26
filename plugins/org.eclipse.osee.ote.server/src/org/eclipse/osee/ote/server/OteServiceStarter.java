/*
 * Created on Mar 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.server;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;

public interface OteServiceStarter {

   public IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, String environmentFactoryClass) throws Exception;
   
   public IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, TestEnvironmentFactory factory) throws Exception;
  
   public void stop() throws Exception;
   
}
