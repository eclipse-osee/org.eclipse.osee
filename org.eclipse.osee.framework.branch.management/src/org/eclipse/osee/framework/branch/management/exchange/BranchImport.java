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

import java.io.File;
import java.util.List;
import org.eclipse.osee.framework.branch.management.IBranchImport;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class BranchImport implements IBranchImport {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.IBranchImport#importBranch()
    */
   @Override
   public void importBranch(File fileToImport, Options options, int... branchesToImport) throws Exception {
      ImportController importController = new ImportController(fileToImport, options, branchesToImport);
      importController.execute();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.IBranchImport#importBranch()
    */
   @Override
   public void importBranch(File fileToImport, Options options, List<Integer> branchesToImport) throws Exception {
      int[] branchIdsArray = new int[branchesToImport.size()];
      for (int index = 0; index < branchesToImport.size(); index++) {
         branchIdsArray[index] = branchesToImport.get(index);
      }
      importBranch(fileToImport, options, branchIdsArray);
   }
}
