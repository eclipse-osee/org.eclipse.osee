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
import org.eclipse.osee.framework.branch.management.Activator;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.branch.management.exchange.resource.ExchangeLocatorProvider;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class BranchExchange implements IBranchExchange {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.IBranchExchange#exportBranch(java.lang.String, org.eclipse.osee.framework.resource.management.Options, int[])
    */
   @Override
   public IResourceLocator exportBranch(String exportName, Options options, int... branchIds) throws Exception {
      ExportController controller = new ExportController(exportName, options, branchIds);
      controller.execute();
      return Activator.getInstance().getResourceLocatorManager().generateResourceLocator(
            ExchangeLocatorProvider.PROTOCOL, "", controller.getExchangeFileName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.IBranchExchange#exportBranch(java.lang.String, org.eclipse.osee.framework.resource.management.Options, java.util.List)
    */
   @Override
   public IResourceLocator exportBranch(String exportName, Options options, List<Integer> branchIds) throws Exception {
      int[] branchIdsArray = new int[branchIds.size()];
      for (int index = 0; index < branchIds.size(); index++) {
         branchIdsArray[index] = branchIds.get(index);
      }
      return exportBranch(exportName, options, branchIdsArray);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.IBranchExchange#importBranch(org.eclipse.osee.framework.resource.management.IResourceLocator, org.eclipse.osee.framework.resource.management.Options, int[])
    */
   @Override
   public void importBranch(IResourceLocator fileToImport, Options options, int... branchIds) throws Exception {
      ImportController importController = new ImportController(fileToImport, options, branchIds);
      importController.execute();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.IBranchExchange#importBranch(org.eclipse.osee.framework.resource.management.IResourceLocator, org.eclipse.osee.framework.resource.management.Options, java.util.List)
    */
   @Override
   public void importBranch(IResourceLocator fileToImport, Options options, List<Integer> branchIds) throws Exception {
      int[] branchIdsArray = new int[branchIds.size()];
      for (int index = 0; index < branchIds.size(); index++) {
         branchIdsArray[index] = branchIds.get(index);
      }
      importBranch(fileToImport, options, branchIdsArray);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.IBranchExchange#checkIntegrity(org.eclipse.osee.framework.resource.management.IResourceLocator)
    */
   @Override
   public void checkIntegrity(IResourceLocator fileToCheck) throws Exception {
      ExchangeIntegrity exchangeIntegrityCheck = new ExchangeIntegrity(fileToCheck);
      exchangeIntegrityCheck.execute();
   }
}
