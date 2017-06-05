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
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

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
            boolean g1Fav = FavoritesManager.isFavorite(awa1, AtsClientService.get().getUserService().getCurrentUser());
            boolean g2Fav = FavoritesManager.isFavorite(awa2, AtsClientService.get().getUserService().getCurrentUser());
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
         return compare((Artifact) o1, (Artifact) o2);
      }
      return 0;
   }

   private int compare(Artifact a1, Artifact a2) {
      return getComparator().compare(a1.getName(), a2.getName());
   }

}