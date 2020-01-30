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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.config.JaxTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionTreeContentProvider implements ITreeContentProvider {

   private final Active active;

   public TeamDefinitionTreeContentProvider(Active active) {
      super();
      this.active = active;
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      } else if (parentElement instanceof IAtsTeamDefinition && active != null) {
         try {
            IAtsTeamDefinition teamDef = (IAtsTeamDefinition) parentElement;
            return TeamDefinitions.getActive(TeamDefinitions.getChildren(teamDef, false), active).toArray();
         } catch (Exception ex) {
            // do nothing
         }
      } else if (parentElement instanceof JaxTeamDefinition && active != null) {
         try {
            JaxTeamDefinition teamDef = (JaxTeamDefinition) parentElement;
            List<JaxTeamDefinition> teamDefs = new LinkedList<>();
            for (Long id : teamDef.getChildren()) {
               JaxTeamDefinition td =
                  AtsClientService.get().getConfigService().getConfigurations().getIdToTeamDef().get(id);
               if (active == Active.Both || (active == Active.Active && td.isActive()) || (active == Active.InActive && !td.isActive())) {
                  teamDefs.add(td);
               }
            }
            return teamDefs.toArray();
         } catch (Exception ex) {
            // do nothing
         }
      }
      return new Object[] {};
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof IAtsTeamDefinition) {
         return (AtsClientService.get().getTeamDefinitionService().getParentTeamDef((IAtsTeamDefinition) element));
      } else if (element instanceof JaxTeamDefinition) {
         Long id = ((JaxTeamDefinition) element).getId();
         if (id > 0) {
            JaxTeamDefinition td =
               AtsClientService.get().getConfigService().getConfigurations().getIdToTeamDef().get(id);
            return td;
         }
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

}
