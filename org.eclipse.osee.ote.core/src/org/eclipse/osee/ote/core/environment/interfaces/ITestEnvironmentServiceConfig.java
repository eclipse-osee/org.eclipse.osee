/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.environment.interfaces;

import java.lang.reflect.Constructor;
import org.eclipse.osee.ote.core.enums.SupportedScriptTypes;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TestServerMode;

public interface ITestEnvironmentServiceConfig {
   /**
    * gets the maximum number of environments allowed in this service
    */
   public int getMaxEnvironments();

   public int getMaxUsersPerEnvironment();

   /**
	 * 
	 */
   public String getName();

   public TestServerMode getMode();

   public String getServerTitle();

   public String getOutfileLocation();

   public SupportedScriptTypes getSupportedScriptTypes();

   /**
    * gets the Constructor for the creating new environments
    */
   public Constructor<? extends TestEnvironment> getEnvironmentConstructor();

   /**
    * gets the parameter instances need to calling the constructor when new environments are created
    */
   public Object[] getConstructorParameters();

   /**
    * tells whether the environment should stay running when the last user disconnects
    * 
    * @return true if the environment should stay active when no users are connected or false otherwise
    */
   public boolean keepEnvAliveWithNoUsers();

   /**
    * returns whether an environment should be created and started up upon service initialization
    * 
    * @return true if an environment should be started up or false if no evionment should be created until requested
    */
   public boolean startEnvionrmnetOnServiceInit();
}
