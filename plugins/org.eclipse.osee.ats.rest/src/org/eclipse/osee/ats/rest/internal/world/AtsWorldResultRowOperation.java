/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.ResultRow;
import org.eclipse.osee.framework.jdk.core.result.ResultRows;

/**
 * @author Donald G. Dunne
 */
public class AtsWorldResultRowOperation {

   private final AtsApi atsApi;
   private final AtsSearchData atsSearchData;

   public AtsWorldResultRowOperation(AtsApi atsApi, AtsSearchData atsSearchData) {
      this.atsApi = atsApi;
      this.atsSearchData = atsSearchData;
      AtsDemoOseeTypes.Action.getName();
   }

   public ResultRows run() {
      ResultRows rows = new ResultRows();

      Collection<ArtifactToken> artifacts = atsApi.getQueryService().getArtifacts(atsSearchData, null);
      int x = 0;
      List<XViewerColumn> showCols = new ArrayList<>();
      for (XViewerColumn col : atsSearchData.getCustomizeData().getColumnData().getColumns()) {
         if (col.isShow()) {
            showCols.add(col);
         }
      }
      for (ArtifactToken art : artifacts) {
         System.err.println(x++ + " - " + art.toStringWithId());
         ResultRow row = new ResultRow(art.getId(), atsApi.getAtsBranch().getId());
         rows.add(row);
         for (XViewerColumn col : showCols) {
            if (col.isShow()) {
               addCellData(atsApi, art, row, col);
            }
         }
      }
      return rows;
   }

   private void addCellData(AtsApi atsApi, ArtifactToken art, ResultRow row, XViewerColumn xCol) {
      String value = "";
      if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
         value = atsApi.getColumnService().getColumnText(xCol.getId(), workItem);
      }
      row.addValue(value);
   }
}
