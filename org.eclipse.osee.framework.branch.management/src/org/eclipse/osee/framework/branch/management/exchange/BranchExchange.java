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
package org.eclipse.osee.framework.branch.management.exchange;

import java.util.List;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.branch.management.exchange.handler.StandardOseeDbExportDataProvider;
import org.eclipse.osee.framework.branch.management.exchange.resource.ExchangeLocatorProvider;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.services.IOseeModelingServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class BranchExchange implements IBranchExchange {

   private final IOseeModelingServiceProvider modelingServiceProvider;

   public BranchExchange(IOseeModelingServiceProvider modelingServiceProvider) {
      this.modelingServiceProvider = modelingServiceProvider;
   }

   @Override
   public IResourceLocator exportBranch(String exportName, Options options, int... branchIds) throws Exception {
      ExportController controller =
            new ExportController(modelingServiceProvider.getOseeModelingService(), exportName, options, branchIds);
      controller.execute();
      return Activator.getInstance().getResourceLocatorManager().generateResourceLocator(
            ExchangeLocatorProvider.PROTOCOL, "", controller.getExchangeFileName());
   }

   @Override
   public IResourceLocator exportBranch(String exportName, Options options, List<Integer> branchIds) throws Exception {
      int[] branchIdsArray = new int[branchIds.size()];
      for (int index = 0; index < branchIds.size(); index++) {
         branchIdsArray[index] = branchIds.get(index);
      }
      return exportBranch(exportName, options, branchIdsArray);
   }

   @Override
   public void importBranch(IResourceLocator exportDataLocator, Options options, int... branchIds) throws Exception {

      ImportController importController =
            new ImportController(new StandardOseeDbExportDataProvider(exportDataLocator), options, branchIds);
      importController.execute();
   }

   @Override
   public void importBranch(IResourceLocator fileToImport, Options options, List<Integer> branchIds) throws Exception {
      int[] branchIdsArray = new int[branchIds.size()];
      for (int index = 0; index < branchIds.size(); index++) {
         branchIdsArray[index] = branchIds.get(index);
      }
      importBranch(fileToImport, options, branchIdsArray);
   }

   @Override
   public IResourceLocator checkIntegrity(IResourceLocator fileToCheck) throws Exception {
      ExchangeIntegrity exchangeIntegrityCheck =
            new ExchangeIntegrity(new StandardOseeDbExportDataProvider(fileToCheck));
      exchangeIntegrityCheck.execute();
      return Activator.getInstance().getResourceLocatorManager().generateResourceLocator(
            ExchangeLocatorProvider.PROTOCOL, "", exchangeIntegrityCheck.getExchangeCheckFileName());
   }
}
