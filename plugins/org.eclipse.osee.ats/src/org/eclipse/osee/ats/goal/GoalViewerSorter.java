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
package org.eclipse.osee.ats.goal;

import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class GoalViewerSorter extends ViewerSorter {

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      if (o1 instanceof GoalArtifact && o2 instanceof GoalArtifact) {
         try {
            GoalArtifact g1 = (GoalArtifact) o1;
            GoalArtifact g2 = (GoalArtifact) o2;
            boolean g1Fav = FavoritesManager.isFavorite(g1, AtsClientService.get().getUserAdmin().getCurrentUser());
            boolean g2Fav = FavoritesManager.isFavorite(g2, AtsClientService.get().getUserAdmin().getCurrentUser());
            if (g1Fav && g2Fav) {
               return compare(g1, g2);
            } else if (g1Fav && !g2Fav) {
               return -1;
            } else if (g2Fav && !g1Fav) {
               return 1;
            }
            boolean g1Assignee = g1.isAssigneeMe();
            boolean g2Assignee = g2.isAssigneeMe();
            if (g1Assignee && g2Assignee) {
               return compare(g1, g2);
            } else if (g1Assignee && !g2Assignee) {
               return -1;
            } else if (g2Assignee && !g1Assignee) {
               return 1;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return compare((Artifact) o1, (Artifact) o2);
      }
      return 0;
   }

   @SuppressWarnings("unchecked")
   private int compare(Artifact a1, Artifact a2) {
      return getComparator().compare(a1.getName(), a2.getName());
   }

}