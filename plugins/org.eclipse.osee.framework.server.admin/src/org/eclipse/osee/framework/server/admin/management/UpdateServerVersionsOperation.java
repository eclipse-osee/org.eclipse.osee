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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class UpdateServerVersionsOperation extends AbstractOperation {
   private final String version;
   private final boolean add;

   public UpdateServerVersionsOperation(OperationLogger logger, String version, boolean add) {
      super("Add Version", Activator.PLUGIN_ID, logger);
      this.version = version;
      this.add = add;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      IApplicationServerManager appManager = Activator.getApplicationServerManager();
      if (add) {
         appManager.addSupportedVersion(version);
      } else {
         appManager.removeSupportedVersion(version);
      }
      logf("Osee Application Server: %s",
         Arrays.deepToString(Activator.getApplicationServerManager().getSupportedVersions()));
   }
}