/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.core.column.TaskRelatedArtifactTypeColumn;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;

/**
 * @author Donald G. Dunne
 */
public class TaskRelatedArtifactTypeColumnUI extends XViewerAtsColumnIdColumn implements IAtsXViewerPreComputedColumn {

   public static TaskRelatedArtifactTypeColumnUI instance = new TaskRelatedArtifactTypeColumnUI();

   public static TaskRelatedArtifactTypeColumnUI getInstance() {
      return instance;
   }

   private TaskRelatedArtifactTypeColumnUI() {
      super(AtsColumnToken.TaskToRelatedArtifactTypeColumnToken);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsColumnIdColumn copy() {
      XViewerAtsColumnIdColumn newXCol = new TaskRelatedArtifactTypeColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return AtsClientService.get().getColumnService().getColumnText(AtsColumnId.TaskToRelatedArtifactType,
         (IAtsWorkItem) obj);
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      TaskRelatedArtifactTypeColumn column =
         (TaskRelatedArtifactTypeColumn) AtsClientService.get().getColumnService().getColumn(
            AtsColumnId.TaskToRelatedArtifactType);
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (Object obj : objects) {
         if (obj instanceof IAtsWorkItem) {
            workItems.add((IAtsWorkItem) obj);
         }
      }
      column.populateCache(workItems);
   }

}
