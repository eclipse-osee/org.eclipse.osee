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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class ConsolidateArtifactVersionsCommand extends BaseServerCommand {

   public ConsolidateArtifactVersionsCommand(CommandInterpreter ci) {
      super("Consolidate Artifact Versions", ci);
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      try {

         Operations.executeWorkAndCheckStatus(new ConsolidateArtifactVersionTxOperation(Activator.getInstance(),
               getCommandInterpreter()), monitor, 0);
      } catch (OseeCoreException ex) {
         printStackTrace(ex);
      }
   }
}
