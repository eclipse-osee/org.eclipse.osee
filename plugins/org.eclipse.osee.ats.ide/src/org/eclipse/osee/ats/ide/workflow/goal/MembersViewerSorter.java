/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.workflow.goal;

import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.FavoritesManager;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class MembersViewerSorter extends ViewerComparator {

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      if (o1 instanceof AbstractWorkflowArtifact && o2 instanceof AbstractWorkflowArtifact) {
         try {
            AbstractWorkflowArtifact awa1 = (AbstractWorkflowArtifact) o1;
            AbstractWorkflowArtifact awa2 = (AbstractWorkflowArtifact) o2;
            boolean g1Fav = FavoritesManager.isFavorite(awa1);
            boolean g2Fav = FavoritesManager.isFavorite(awa2);
            if (g1Fav && g2Fav) {
               return compare(awa1, awa2);
            } else if (g1Fav && !g2Fav) {
               return -1;
            } else if (g2Fav && !g1Fav) {
               return 1;
            }
            boolean g1Assignee = awa1.isAssigneeMe();
            boolean g2Assignee = awa2.isAssigneeMe();
            if (g1Assignee && g2Assignee) {
               return compare(awa1, awa2);
            } else if (g1Assignee && !g2Assignee) {
               return -1;
            } else if (g2Assignee && !g1Assignee) {
               return 1;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return compare((Named) o1, (Named) o2);
      }
      return 0;
   }

   private int compare(Named a1, Named a2) {
      return getComparator().compare(a1.getName(), a2.getName());
   }

}