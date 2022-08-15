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
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.column.AtsValColumn;
import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.AssumptionsColumn;
import org.eclipse.osee.ats.ide.column.AtsColumnIdUi;
import org.eclipse.osee.ats.ide.column.DescriptionColumn;
import org.eclipse.osee.ats.ide.column.PointsColumn;
import org.eclipse.osee.ats.ide.column.ReviewedByColumn;
import org.eclipse.osee.ats.ide.column.RiskFactorColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.ats.ide.world.WorldXViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public abstract class MiniTaskXViewerFactory extends SkynetXViewerFactory {

   private final String namespace;

   public MiniTaskXViewerFactory(String namespace) {
      super(namespace, null);
      this.namespace = namespace;
   }

   protected void addPreColumns(List<XViewerColumn> cols) {
      // for subclass implementation
   }

   protected void addPostColumns(List<XViewerColumn> cols) {
      // for subclass implementation
   }

   @Override
   public List<XViewerColumn> getColumns() {
      List<XViewerColumn> cols = new ArrayList<>();
      addPreColumns(cols);
      cols.add(getAttributeConfigColumn(AtsColumnTokens.TitleColumn));
      cols.add(getColumnServiceColumn(AtsColumnTokens.StateColumn));
      cols.add(AssigneeColumnUI.getInstance());
      PointsColumn ptsCol = PointsColumn.instance.copy();
      ptsCol.setShow(true);
      ptsCol.setWidth(30);
      cols.add(ptsCol);
      cols.add(ReviewedByColumn.instance);
      cols.add(RiskFactorColumn.instance);
      DescriptionColumn descCol = DescriptionColumn.instance.copy();
      descCol.setShow(true);
      cols.add(descCol);
      AssumptionsColumn assumptCol = AssumptionsColumn.instance.copy();
      assumptCol.setShow(true);
      cols.add(assumptCol);
      cols.add(getColumnServiceColumn(AtsColumnTokens.AtsIdColumnShow));
      addPostColumns(cols);

      for (XViewerColumn col : cols) {
         if (col.getName().equals(AtsColumnTokens.TitleColumn.getName())) {
            col.setWidth(250);
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
   public XViewerColumn getColumnServiceColumn(AtsValColumn columnToken) {
      return new AtsColumnIdUi(columnToken, AtsApiService.get());
   }

   private XViewerColumn getAttributeConfigColumn(AtsAttrValCol attrValueColumn) {
      XViewerColumn result = null;
      for (AtsAttrValCol column : AtsApiService.get().getConfigService().getConfigurations().getViews().getAttrColumns()) {
         if (column.getNamespace().equals(namespace) && column.getId().equals(attrValueColumn.getId())) {
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
