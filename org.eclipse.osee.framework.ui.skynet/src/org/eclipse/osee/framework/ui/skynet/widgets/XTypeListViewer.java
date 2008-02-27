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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Jeff C. Phillips
 */
public abstract class XTypeListViewer extends XListViewer {
   private static final BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();

   public XTypeListViewer(String name) {
      super(name);

      setLabelProvider(new LabelProvider());
      setSorter(new ViewerSorter());
   }

   public Branch resolveBranch(String keyedBranchName) {
      Branch branch = null;
      try {
         if (keyedBranchName != null) {
            branch = branchPersistenceManager.getKeyedBranch(keyedBranchName);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      if (branch == null) {
         branch = branchPersistenceManager.getDefaultBranch();
      }
      return branch;
   }
}
