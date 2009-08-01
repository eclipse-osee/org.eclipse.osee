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
package org.eclipse.osee.framework.server.admin.management;

import java.util.Arrays;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;

/**
 * @author Roberto E. Escobar
 */
public class AddServerVersionWorker extends BaseServerCommand {

   protected AddServerVersionWorker() {
      super("Add Version");
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      String versionToAdd = getCommandInterpreter().nextArgument();
      CoreServerActivator.getApplicationServerManager().addSupportedVersion(versionToAdd);
      StringBuffer buffer = new StringBuffer();
      buffer.append("Osee Application Server: ");
      buffer.append(Arrays.deepToString(CoreServerActivator.getApplicationServerManager().getSupportedVersions()));
      buffer.append("\n");
      println(buffer.toString());
   }
}
