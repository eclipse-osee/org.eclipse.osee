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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;

/**
 * @author Roberto E. Escobar
 */
public class ServerRequestsWorker extends BaseServerCommand {

   protected ServerRequestsWorker() {
      super("Set Servlet Requests Allowed");
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      String value = getCommandInterpreter().nextArgument();
      CoreServerActivator.getApplicationServerManager().setServletRequestsAllowed(new Boolean(value));
   }
}
