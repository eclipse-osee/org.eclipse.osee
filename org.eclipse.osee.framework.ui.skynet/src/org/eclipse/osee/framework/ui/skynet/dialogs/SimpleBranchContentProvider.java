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

package org.eclipse.osee.framework.ui.skynet.dialogs;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Roberto E. Escobar
 */
public final class SimpleBranchContentProvider implements ITreeContentProvider {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(SimpleBranchContentProvider.class);
   private static final Object[] EMPTY_ARRAY = new Object[0];

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      if (parentElement instanceof Branch) {
         try {
            Collection<Branch> branches = ((Branch) parentElement).getChildBranches();
            return branches.toArray(new Branch[branches.size()]);
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Unable to get child branches", ex);
         }
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      //      if (element instanceof DataItem) {
      //         return ((DataItem) element).getParent();
      //      }
      return null;
   }

   public boolean hasChildren(Object element) {
      if (element instanceof Collection) return true;
      if (element instanceof Branch) {
         try {
            Collection<Branch> branches = ((Branch) element).getChildBranches();
            return branches.size() > 0;
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Unable to get child branches", ex);
         }
      }
      return false;
   }

   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
