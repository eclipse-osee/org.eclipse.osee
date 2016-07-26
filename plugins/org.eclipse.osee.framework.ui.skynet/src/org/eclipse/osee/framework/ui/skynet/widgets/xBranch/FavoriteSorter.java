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

import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class FavoriteSorter extends XViewerSorter {
   private boolean favoritesFirst;

   public FavoriteSorter(XViewer viewer) {
      super(viewer);
      this.favoritesFirst = false;
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {

      if (favoritesFirst) {
         if (o1 instanceof BranchId && o2 instanceof BranchId) {
            try {
               User user = UserManager.getUser();
               boolean fav1 = user.isFavoriteBranch((BranchId) o1);
               boolean fav2 = user.isFavoriteBranch((BranchId) o2);

               if (fav1 ^ fav2) {
                  return fav1 ? -1 : 1;
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         } else if (o1 instanceof BranchId && !(o2 instanceof BranchId)) {
            return -1;
         } else if (!(o1 instanceof BranchId) && o2 instanceof BranchId) {
            return 1;
         }
      }
      return super.compare(viewer, o1, o2);
   }

   /**
    * @return Returns the favoritesFirst.
    */
   public boolean isFavoritesFirst() {
      return favoritesFirst;
   }

   /**
    * @param favoritesFirst The favoritesFirst to set.
    */
   public void setFavoritesFirst(boolean favoritesFirst) {
      this.favoritesFirst = favoritesFirst;
   }
}