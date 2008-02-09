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

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

public class TeamDefinitionTreeContentProvider implements ITreeContentProvider {

   private final Active active;

   public TeamDefinitionTreeContentProvider(Active active) {
      super();
      this.active = active;
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection)
         return ((Collection) parentElement).toArray();
      else if (parentElement instanceof TeamDefinitionArtifact) {
         try {
            TeamDefinitionArtifact teamDef = ((TeamDefinitionArtifact) parentElement);
            return AtsLib.getActiveSet(Artifacts.getChildrenOfTypeSet(teamDef, TeamDefinitionArtifact.class, false),
                  active, TeamDefinitionArtifact.class).toArray();
         } catch (SQLException ex) {
         }
      }
      return new Object[] {};
   }

   public Object getParent(Object element) {
      try {
         if (element instanceof TeamDefinitionArtifact) {
            return ((TeamDefinitionArtifact) element).getParent();
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
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
