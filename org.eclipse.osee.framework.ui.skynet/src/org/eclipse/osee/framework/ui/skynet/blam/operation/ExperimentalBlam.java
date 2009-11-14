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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchControlled;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeBranchOperation;
import org.eclipse.osee.framework.skynet.core.types.impl.BranchStoreOperation;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class ExperimentalBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Experimental Blam";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      int increment = 0;
      if (!variableMap.getBoolean("Even Branches")) {
         increment = 1;
      }

      for (Branch branch : BranchManager.getBranches(BranchArchivedState.ARCHIVED, BranchControlled.ALL,
            BranchType.WORKING)) {
         if ((branch.getId() + increment) % 2 == 0) {
            if (ConnectionHandler.runPreparedQueryFetchInt(0, PurgeBranchOperation.TEST_TXS, branch.getId()) == 1) {
               BranchStoreOperation.moveBranchAddressing(null, branch, true);
            }
         }
      }
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam#getXWidgetsXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Even Branches\" /></xWidgets>";
   }
}