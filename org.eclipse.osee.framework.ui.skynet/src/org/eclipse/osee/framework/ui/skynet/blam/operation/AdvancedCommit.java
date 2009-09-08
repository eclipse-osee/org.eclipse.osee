/*******************************************************************************
 * Copyright (c) 2009 Boeing.
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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.commit.ChangeCache;
import org.eclipse.osee.framework.skynet.core.commit.ChangeDatabaseDataAccessor;
import org.eclipse.osee.framework.skynet.core.commit.ChangeLocator;
import org.eclipse.osee.framework.skynet.core.commit.CommitDbOperation;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.OseeChange;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class AdvancedCommit extends AbstractBlam {

   @Override
   public String getName() {
      return "Advanced Commit";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ChangeLocator locator =
            new ChangeLocator(variableMap.getBranch("Source Branch"), variableMap.getBranch("Destination Branch"));
      List<OseeChange> changes = new ChangeCache(new ChangeDatabaseDataAccessor()).getRawChangeData(monitor, locator);
      ConflictManagerExternal conflictManager =
            new ConflictManagerExternal(locator.getDestinationBranch(), locator.getSourceBranch());

      Operations.executeWork(new ComputeNetChangeOperation(changes, conflictManager, null), monitor, -1);
      Operations.executeWork(new CommitDbOperation(conflictManager, changes), monitor, -1);
   }

   @Override
   public String getDescriptionUsage() {
      return "Commit branch regardless of branch heirarchy";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Source Branch\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Destination Branch\" /></xWidgets>";
   }
}