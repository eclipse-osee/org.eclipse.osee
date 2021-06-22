/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.mini;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnIdValueColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.AssumptionsColumn;
import org.eclipse.osee.ats.ide.column.AtsColumnIdUi;
import org.eclipse.osee.ats.ide.column.DescriptionColumn;
import org.eclipse.osee.ats.ide.column.PointsColumn;
import org.eclipse.osee.ats.ide.column.TleReviewedColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.ats.ide.world.WorldXViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public abstract class MiniTaskXViewerFactory extends SkynetXViewerFactory {

   public static XViewerColumn Check_Col = new XViewerColumn("ats.taskest.check", "Select", 53, XViewerAlign.Left, true,
      SortDataType.String, false, "Check and plus to create canned tasks.  Add task to create manual tasks.");
   public static XViewerColumn Attachments_Col = new XViewerColumn("ats.taskest.attachments", "Attachments", 20,
      XViewerAlign.Left, true, SortDataType.String, false, "Shows number of attachments.  Double-click to open task.");

   public static final String COLUMN_NAMESPACE = "ats.column";
   private final String namespace2;

   public MiniTaskXViewerFactory(String namespace) {
      super(namespace, null);
      namespace2 = namespace;
   }

   @Override
   public List<XViewerColumn> getColumns() {
      List<XViewerColumn> cols = new ArrayList<>();
      cols.add(Check_Col);
      cols.add(Attachments_Col);
      cols.add(getAttributeConfigColumn(AtsColumnToken.TitleColumn));
      cols.add(getColumnServiceColumn(AtsColumnToken.StateColumn));
      cols.add(AssigneeColumnUI.getInstance());
      PointsColumn ptsCol = PointsColumn.instance.copy();
      ptsCol.setShow(true);
      ptsCol.setWidth(30);
      cols.add(ptsCol);
      cols.add(TleReviewedColumn.instance);
      DescriptionColumn descCol = DescriptionColumn.instance.copy();
      descCol.setShow(true);
      cols.add(descCol);
      cols.add(AssumptionsColumn.instance);
      cols.add(getAttributeConfigColumn(AtsColumnToken.NotesColumn));
      cols.add(getColumnServiceColumn(AtsColumnToken.AtsIdColumnShow));

      for (XViewerColumn col : cols) {
         if (col.getName().equals(AtsColumnToken.TitleColumn.getName())) {
            col.setWidth(250);
         } else if (col.getName().equals(AtsColumnToken.NotesColumn.getName())) {
            col.setWidth(150);
         }
      }
      for (XViewerColumn col : WorldXViewerFactory.getWorldViewColumns()) {
         if (!cols.contains(col)) {
            col.setShow(false);
            cols.add(col);
         }
      }
      return cols;
   }

   /**
    * Provides XViewerColumn for non-attribute based columns like Type and State
    */
   public XViewerColumn getColumnServiceColumn(AtsColumnIdValueColumn columnToken) {
      return new AtsColumnIdUi(columnToken, AtsApiService.get());
   }

   private XViewerColumn getAttributeConfigColumn(AtsAttributeValueColumn attrValueColumn) {
      XViewerColumn result = null;
      for (AtsAttributeValueColumn column : AtsApiService.get().getConfigService().getConfigurations().getViews().getAttrColumns()) {
         if (column.getNamespace().equals(namespace2) && column.getId().equals(attrValueColumn.getId())) {
            result = new XViewerAtsAttributeValueColumn(column);
            break;
         }
      }
      if (result == null) {
         result = new XViewerAtsAttributeValueColumn(attrValueColumn);
      }
      return result;
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

}
