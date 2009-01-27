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

package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

public class TeamDefinitionTreeContentProvider implements ITreeContentProvider {

   private final Active active;

   public TeamDefinitionTreeContentProvider() {
      super();
      this.active = null;
   }

   public TeamDefinitionTreeContentProvider(Active active) {
      super();
      this.active = active;
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection)
         return ((Collection) parentElement).toArray();
      else if (parentElement instanceof TeamDefinitionArtifact && active != null) {
         try {
            TeamDefinitionArtifact teamDef = ((TeamDefinitionArtifact) parentElement);
            return AtsLib.getActiveSet(Artifacts.getChildrenOfTypeSet(teamDef, TeamDefinitionArtifact.class, false),
                  active, TeamDefinitionArtifact.class).toArray();
         } catch (Exception ex) {
            // do nothing
         }
      }
      return new Object[] {};
   }

   public Object getParent(Object element) {
      try {
         if (element instanceof TeamDefinitionArtifact) {
            return ((TeamDefinitionArtifact) element).getParent();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
