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
package org.eclipse.osee.framework.server.admin.branch;

import java.net.URI;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.resource.management.util.ResourceLocator;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ExchangeIntegrityOperation extends AbstractOperation {
   private final List<String> importFiles;

   public ExchangeIntegrityOperation(OperationLogger logger, List<String> importFiles) {
      super("Verify Exchange File", Activator.PLUGIN_ID, logger);
      this.importFiles = importFiles;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      for (String fileToImport : importFiles) {
         URI uri = new URI("exchange://" + fileToImport);
         Activator.getBranchExchange().checkIntegrity(new ResourceLocator(uri));
      }
   }
}
