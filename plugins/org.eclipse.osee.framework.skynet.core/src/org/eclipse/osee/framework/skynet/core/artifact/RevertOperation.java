/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.skynet.core.artifact.revert.Revert;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class RevertOperation extends AbstractDbTxOperation {

   private final Revert revert;

   public RevertOperation(Revert revert, String operationTitle) {
      this(revert, Activator.getInstance().getOseeDatabaseService(), operationTitle, Activator.PLUGIN_ID);
   }

   public RevertOperation(Revert revert, IOseeDatabaseService databaseService, String operationTitle, String pluginId) {
      super(databaseService, operationTitle, pluginId);
      this.revert = revert;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      revert.revert(connection);
   }
}
