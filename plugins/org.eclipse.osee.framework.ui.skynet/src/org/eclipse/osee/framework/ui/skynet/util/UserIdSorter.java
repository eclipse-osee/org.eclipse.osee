/*********************************************************************
 * Copyright (c) 2018 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeApiService;

/**
 * Sorter for user lists
 */
@SuppressWarnings("deprecation")
public class UserIdSorter extends ViewerSorter {

   private final Collection<? extends UserId> initialSel;
   private final Collection<? extends UserId> teamMembers;

   public UserIdSorter(Collection<? extends UserId> initialSel, Collection<? extends UserId> teamMembers) {
      this.initialSel = initialSel;
      this.teamMembers = teamMembers;
   }

   @SuppressWarnings("unlikely-arg-type")
   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      Named named1 = (Named) e1;
      Named named2 = (Named) e2;

      try {
         UserToken me = OseeApiService.user();
         if (me.equals(named1)) {
            return -1;
         }
         if (me.equals(named2)) {
            return 1;
         }
         if (initialSel != null) {
            if (initialSel.contains(named1) && initialSel.contains(named2)) {
               return named1.compareTo(named2);
            }
            if (initialSel.contains(named1)) {
               return -1;
            }
            if (initialSel.contains(named2)) {
               return 1;
            }
         }
         if (teamMembers != null) {
            if (teamMembers.contains(named1) && teamMembers.contains(named2)) {
               return named1.compareTo(named2);
            }
            if (teamMembers.contains(named1)) {
               return -1;
            }
            if (teamMembers.contains(named2)) {
               return 1;
            }
         }
         return named1.compareTo(named2);
      } catch (OseeCoreException ex) {
         return -1;
      }
   }
}