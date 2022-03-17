/*********************************************************************
 * Copyright (c) 2022 Boeing
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
public class SiblingAtsIdColumn extends BackgroundLoadingColumn {

   public static SiblingAtsIdColumn instance = new SiblingAtsIdColumn();
   private final List<String> ids = new ArrayList<>();

   public static SiblingAtsIdColumn getInstance() {
      return instance;
   }

   private SiblingAtsIdColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".sibling.ats.id", "Sibling ATS Ids", 100, XViewerAlign.Left, false,
         SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public SiblingAtsIdColumn copy() {
      SiblingAtsIdColumn newXCol = new SiblingAtsIdColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      ids.clear();
      ;
      if (workItem instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         for (IAtsTeamWorkflow sibTeamWf : AtsApiService.get().getWorkItemService().getSiblings(teamWf)) {
            ids.add(sibTeamWf.getAtsId());
         }
      }
      return Collections.toString(", ", ids);
   }

   /**
    * Bulk load related actions and those actions related team workflows to they're not loaded one at a time in getValue
    * above
    */
   @Override
   protected void getValues(Collection<?> objects, Map<Long, String> idToValueMap) {
      Collection<Artifact> arts = Collections.castAll(objects);
      AtsBulkLoad.bulkLoadSiblings(arts);
   }

}
