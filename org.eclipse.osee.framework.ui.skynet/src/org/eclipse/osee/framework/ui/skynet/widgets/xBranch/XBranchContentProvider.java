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
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchControlled;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchState;

/**
 * @author Jeff C. Phillips
 */
public class XBranchContentProvider implements ITreeContentProvider {

   private final BranchXViewer changeXViewer;
   private boolean showChildBranchesAtMainLevel;
   private boolean showMergeBranches;
   private static Object[] EMPTY_ARRAY = new Object[0];

   public XBranchContentProvider(BranchXViewer commitXViewer) {
      super();
      this.changeXViewer = commitXViewer;
   }

   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof BranchManager) {
         List<BranchType> branchTypes = new ArrayList<BranchType>(4);
         branchTypes.add(BranchType.TOP_LEVEL);

         try {
            if (AccessControlManager.isOseeAdmin() && showMergeBranches) {
               branchTypes.add(BranchType.MERGE);
            }

            if (showChildBranchesAtMainLevel) {
               branchTypes.add(BranchType.BASELINE);
               branchTypes.add(BranchType.WORKING);
            }

            List<Branch> branches =
                  BranchManager.getBranches(BranchState.ACTIVE, BranchControlled.ALL,
                        branchTypes.toArray(new BranchType[branchTypes.size()]));
            return branches.toArray();

         } catch (OseeCoreException ex) {
            OseeLog.log(this.getClass(), Level.WARNING, ex);
         }
      }
      if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {
      if (element instanceof BranchManager) return true;
      if (element instanceof Collection) return true;
      return false;
   }

   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   /**
    * @return the changeXViewer
    */
   public BranchXViewer getChangeXViewer() {
      return changeXViewer;
   }

}
