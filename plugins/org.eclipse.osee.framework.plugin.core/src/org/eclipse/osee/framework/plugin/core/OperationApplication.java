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
package org.eclipse.osee.framework.plugin.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class OperationApplication implements IApplication {

   @Override
   public Object start(IApplicationContext context) {
      String[] inputArgs = Platform.getApplicationArgs();
      if (inputArgs.length < 2) {
         System.out.printf("usage: bundle class; not %d arguments", inputArgs.length);
      }

      try {
         IOperation operation = loadOperation(inputArgs[0], inputArgs[1]);
         Operations.executeWorkAndCheckStatus(operation);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return IApplication.EXIT_OK;
   }

   @SuppressWarnings("unchecked")
   private IOperation loadOperation(String bundleName, String className) throws IllegalAccessException, ClassNotFoundException {
      try {
         Bundle bundle = Platform.getBundle(bundleName);
         if (bundle == null) {
            throw new OseeArgumentException("Platform.getBundle found not find [%s]", bundleName);
         }
         Class<IOperation> clazz = (Class<IOperation>) bundle.loadClass(className);
         return clazz.newInstance();

      } catch (InstantiationException ex) {
         throw new OseeArgumentException("%s: does [%s] have an no argument constructor?", ex, className);
      }
   }

   @Override
   public void stop() {
      // this application is unstoppable
   }
}
