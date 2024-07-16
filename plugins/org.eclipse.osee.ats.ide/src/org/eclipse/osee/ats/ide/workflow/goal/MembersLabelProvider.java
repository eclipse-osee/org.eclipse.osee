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

import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.FavoritesManager;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;

/**
 * @author Donald G. Dunne
 */
public class MembersLabelProvider extends ArtifactLabelProvider {

   @Override
   public String getText(Object element) {
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) element;
      try {
         if (FavoritesManager.isFavorite(awa)) {
            return "(Favorite) " + awa.toString();
         } else if (awa.isAssigneeMe()) {
            return "(Assignee) " + awa.toString();
         }
         return awa.toString();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

}
