/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemProvider;
import org.eclipse.osee.ats.api.team.IAtsTeamWorkflowProvider;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Provides for rollup of actionable items
 *
 * @author Donald G. Dunne
 */
public class ActionableItemsColumn extends AbstractServicesColumn {

   public ActionableItemsColumn(IAtsServices services) {
      super(services);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      return getActionableItemsStr(atsObject);
   }

   public String getActionableItemsStr(IAtsObject atsObject)  {
      List<IAtsActionableItem> ais = new ArrayList<>();
      ais.addAll(getActionableItems(atsObject));
      java.util.Collections.sort(ais, new Comparator<IAtsActionableItem>() {

         @Override
         public int compare(IAtsActionableItem arg0, IAtsActionableItem arg1) {
            return arg0.getName().compareTo(arg1.getName());
         }
      });
      if (ais.size() == 1) {
         return ais.iterator().next().getName();
      }
      StringBuilder sb = new StringBuilder();
      for (IAtsActionableItem ai : ais) {
         sb.append(ai.getName());
         sb.append(", ");
      }
      return sb.toString().replace(", $", "");
   }

   public static Collection<IAtsActionableItem> getActionableItems(IAtsObject atsObject)  {
      Set<IAtsActionableItem> ais = new HashSet<>();
      // If object has children team workflows, roll-up results of all ais
      if (atsObject instanceof IAtsTeamWorkflowProvider) {
         for (IAtsTeamWorkflow team : ((IAtsTeamWorkflowProvider) atsObject).getTeamWorkflows()) {
            ais.addAll(getActionableItems(team));
         }
      }
      // Or, add actionable items if provided by object
      else if (atsObject instanceof IAtsActionableItemProvider) {
         ais.addAll(((IAtsActionableItemProvider) atsObject).getActionableItems());
      }

      // Children work items inherit the actionable items of their parent team workflow
      if (atsObject instanceof IAtsWorkItem) {
         IAtsTeamWorkflow teamWf = ((IAtsWorkItem) atsObject).getParentTeamWorkflow();
         if (teamWf != null && teamWf.notEqual(atsObject)) {
            ais.addAll(getActionableItems(teamWf));
         }
      }
      return ais;
   }
}
