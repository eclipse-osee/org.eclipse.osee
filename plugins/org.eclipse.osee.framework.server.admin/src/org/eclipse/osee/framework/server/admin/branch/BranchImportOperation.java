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
import java.net.URISyntaxException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.util.ResourceLocator;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public final class BranchImportOperation extends AbstractOperation {
   private final List<String> importFiles;
   private final PropertyStore propertyStore;
   private final List<Integer> branchIds;

   public BranchImportOperation(OperationLogger logger, PropertyStore propertyStore, List<String> importFiles, List<Integer> branchIds) {
      super("Branch Import", Activator.PLUGIN_ID, logger);
      this.importFiles = importFiles;
      this.propertyStore = propertyStore;
      this.branchIds = branchIds;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException, URISyntaxException {

      if (importFiles.isEmpty()) {
         throw new OseeArgumentException("Files to import were not specified");
      }

      for (String fileToImport : importFiles) {
         URI uri = new URI("exchange://" + fileToImport);
         Activator.getBranchExchange().importBranch(new ResourceLocator(uri), propertyStore, branchIds, getLogger());
      }
   }
}
