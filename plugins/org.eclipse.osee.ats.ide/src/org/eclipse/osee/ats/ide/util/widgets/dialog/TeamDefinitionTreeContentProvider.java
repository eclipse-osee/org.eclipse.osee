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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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
      Collection<IAtsTeamDefinition> results = new ArrayList<>();
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      } else if (parentElement instanceof IAtsTeamDefinition && active != null) {
         try {
            IAtsTeamDefinition teamDef = (IAtsTeamDefinition) parentElement;
            Set<IAtsTeamDefinition> children =
               AtsApiService.get().getTeamDefinitionService().getChildren(teamDef, false);
            Collection<IAtsTeamDefinition> teamDefs =
               AtsApiService.get().getTeamDefinitionService().getActive(children, active);
            results = teamDefs;
         } catch (Exception ex) {
            // do nothing
         }
      } else if (parentElement instanceof TeamDefinition && active != null) {
         try {
            TeamDefinition teamDef = (TeamDefinition) parentElement;
            for (Long id : teamDef.getChildren()) {
               TeamDefinition td = AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().get(id);
               if (active == Active.Both || (active == Active.Active && td.isActive()) || (active == Active.InActive && !td.isActive())) {
                  results.add(td);
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return results.toArray();
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof IAtsTeamDefinition) {
         return (AtsApiService.get().getTeamDefinitionService().getParentTeamDef((IAtsTeamDefinition) element));
      } else if (element instanceof TeamDefinition) {
         Long id = ((TeamDefinition) element).getId();
         if (id > 0) {
            TeamDefinition td = AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().get(id);
            return td;
         }
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof TeamDefinition) {
         return !((TeamDefinition) element).getChildren().isEmpty();
      }
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
