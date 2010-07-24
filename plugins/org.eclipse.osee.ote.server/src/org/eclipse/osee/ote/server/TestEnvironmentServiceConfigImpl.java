/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.server;

import java.lang.reflect.Constructor;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestEnvironmentServiceConfigImpl implements ITestEnvironmentServiceConfig {

   private final boolean keepAliveWithNoUsers;
   private final String title;
   private final String name;
   private final String outfileLocation;
   private final Constructor<? extends TestEnvironment> constructor;

   public TestEnvironmentServiceConfigImpl(boolean keepAliveWithNoUsers, String title, String name, String outfileLocation, Constructor<? extends TestEnvironment> constructor) {
      this.keepAliveWithNoUsers = keepAliveWithNoUsers;
      this.title = title;
      this.name = name;
      this.outfileLocation = outfileLocation;
      this.constructor = constructor;
   }

   @Override
   public Object[] getConstructorParameters() {
      return new Object[0];
   }

   @Override
   public Constructor<? extends TestEnvironment> getEnvironmentConstructor() {
      return constructor;
   }

   @Override
   public int getMaxEnvironments() {
      return 1;
   }

   @Override
   public int getMaxUsersPerEnvironment() {
      return Integer.MAX_VALUE;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getOutfileLocation() {
      return outfileLocation;
   }

   @Override
   public String getServerTitle() {
      return title;
   }

   @Override
   public boolean keepEnvAliveWithNoUsers() {
      return keepAliveWithNoUsers;
   }

   @Override
   public boolean startEnvionrmnetOnServiceInit() {
      return true;
   }

}
