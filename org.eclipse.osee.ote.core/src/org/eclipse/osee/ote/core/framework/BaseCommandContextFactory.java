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
package org.eclipse.osee.ote.core.framework;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.command.ITestContext;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;

public class BaseCommandContextFactory implements ICommandContextFactory {

   public ITestContext getContext(final TestEnvironment testEnvironment, final ITestServerCommand cmd) {

      return testEnvironment;
      /*new ITestContext() {
         public File getOutDir() {
            return testEnvironment.getOutDir();
         }

         public IRunManager getRunManager() {
            return testEnvironment.getRunManager();
         }

         public void setActiveUser(UserTestSessionKey key) {
            testEnvironment.setActiveUser(key);
         }

         public void testComplete(String className, String serverOutfilePath, String clientOutfilePath, CommandEndedStatusEnum status, List<IHealthStatus> healthStatus) {
            testEnvironment.testComplete(className, serverOutfilePath, clientOutfilePath, status, healthStatus);
         }

         public void testStart(String string) {
            testEnvironment.testStart(string);
         }

         /* (non-Javadoc)
          * @see org.eclipse.osee.ote.core.framework.command.ITestContext#getControlInterface(java.lang.String)
          */
//         public Object getControlInterface(String name) {
//            return null;
//         }
//
//      };
   }

}
