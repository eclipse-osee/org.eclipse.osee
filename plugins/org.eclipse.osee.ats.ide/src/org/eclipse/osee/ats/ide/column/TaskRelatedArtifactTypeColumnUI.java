/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.core.column.TaskRelatedArtifactTypeColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;

/**
 * @author Donald G. Dunne
 */
public class TaskRelatedArtifactTypeColumnUI extends XViewerAtsCoreCodeXColumn implements IAtsXViewerPreComputedColumn {

   public static TaskRelatedArtifactTypeColumnUI instance = new TaskRelatedArtifactTypeColumnUI();

   public static TaskRelatedArtifactTypeColumnUI getInstance() {
      return instance;
   }

   private TaskRelatedArtifactTypeColumnUI() {
      super(AtsColumnTokensDefault.TaskToRelatedArtifactTypeColumnToken, AtsApiService.get());
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsCoreCodeXColumn copy() {
      TaskRelatedArtifactTypeColumnUI newXCol = new TaskRelatedArtifactTypeColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return AtsApiService.get().getColumnService().getColumnText(
         AtsColumnTokensDefault.TaskToRelatedArtifactTypeColumnToken, (IAtsWorkItem) obj);
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      TaskRelatedArtifactTypeColumn column =
         (TaskRelatedArtifactTypeColumn) AtsApiService.get().getColumnService().getColumn(
            AtsColumnTokensDefault.TaskToRelatedArtifactTypeColumnToken);
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (Object obj : objects) {
         if (obj instanceof IAtsWorkItem) {
            workItems.add((IAtsWorkItem) obj);
         }
      }
      column.populateCache(workItems);
   }

}
