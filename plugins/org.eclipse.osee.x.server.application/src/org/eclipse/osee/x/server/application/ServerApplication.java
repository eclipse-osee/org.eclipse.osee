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
package org.eclipse.osee.x.server.application;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * @author Roberto E. Escobar
 */
public class ServerApplication implements IApplication {

   private IApplicationContext context;

   @Override
   public Object start(IApplicationContext context) throws Exception {
      this.context = context;
      //      context.applicationRunning();
      return IApplicationContext.EXIT_ASYNC_RESULT;
   }

   @Override
   public void stop() {
      if (context != null) {
         context.setResult(IApplication.EXIT_OK, this);
      }
   }

}
