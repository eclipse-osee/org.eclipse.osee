/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.config.AtsBulkLoad;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class SiblingTeamDefColumnUI extends BackgroundLoadingPreComputedColumnUI {

   public static SiblingTeamDefColumnUI instance = new SiblingTeamDefColumnUI();
   private final List<String> teamDefs = new ArrayList<>();
   private boolean preloaded = false;

   public static SiblingTeamDefColumnUI getInstance() {
      return instance;
   }

   private SiblingTeamDefColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".sibling.teamdef", "Sibling Team Defs", 100, XViewerAlign.Left,
         false, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public SiblingTeamDefColumnUI copy() {
      SiblingTeamDefColumnUI newXCol = new SiblingTeamDefColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      teamDefs.clear();

      if (workItem instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         for (IAtsTeamWorkflow sibTeamWf : AtsApiService.get().getWorkItemService().getSiblings(teamWf)) {
            teamDefs.add(sibTeamWf.getTeamDefinition().getName());
         }
      }
      return Collections.toString(", ", teamDefs);
   }

   /**
    * Bulk load related actions and those actions related team workflows to they're not loaded one at a time in getValue
    * above
    */
   @Override
   public void handlePreLoadingTasks(Collection<?> objects) {
      if (!preloaded) {
         Collection<Artifact> arts = Collections.castAll(objects);
         AtsBulkLoad.bulkLoadSiblings(arts);
         preloaded = true;
      }
   }

}
