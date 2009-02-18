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
package org.eclipse.osee.framework.application.server;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class DbInitApplication implements IApplication {

   @Override
   public Object start(IApplicationContext context) throws Exception {
      //      DatabaseInitializationOperation.executeConfigureFromJvmProperties();
      return EXIT_OK;
   }

   @Override
   public void stop() {
   }
}
