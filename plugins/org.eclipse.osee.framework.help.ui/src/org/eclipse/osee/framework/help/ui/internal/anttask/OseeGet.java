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
package org.eclipse.osee.framework.help.ui.internal.anttask;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.tools.ant.taskdefs.Get;


public class OseeGet extends Get {
   @Override
   public void execute() {
      disableCaching();
      super.execute();
   }

   private void disableCaching() {
      URLConnection c;
      try {
         c = new URL("http://www.eclipse.org/").openConnection();
      } catch (IOException ex) {
         return;
      }
      c.setDefaultUseCaches(false);
   }
}