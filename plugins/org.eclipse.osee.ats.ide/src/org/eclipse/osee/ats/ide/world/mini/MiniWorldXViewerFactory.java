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

package org.eclipse.osee.ats.ide.world.mini;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnIdValueColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.AtsColumnIdUi;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.ats.ide.world.WorldXViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public abstract class MiniWorldXViewerFactory extends SkynetXViewerFactory {

   public GoalArtifact soleGoalArtifact;
   public static final String COLUMN_NAMESPACE = "ats.column";
   private final String namespace2;

   public MiniWorldXViewerFactory(String namespace) {
      super(namespace, null);
      namespace2 = namespace;
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
      cols.add(getColumnServiceColumn(AtsColumnToken.TypeColumn));
      cols.add(getColumnServiceColumn(AtsColumnToken.StateColumn));
      cols.add(AssigneeColumnUI.getInstance());
      cols.add(getAttributeConfigColumn(AtsColumnToken.TitleColumn));
      cols.add(getColumnServiceColumn(AtsColumnToken.AtsIdColumnShow));
      cols.add(TargetedVersionColumnUI.getInstance());
      cols.add(getAttributeConfigColumn(AtsColumnToken.NotesColumn));
      addPostColumns(cols);

      for (XViewerColumn col : cols) {
         if (col.getName().equals(AtsColumnToken.NotesColumn.getName())) {
            col.setWidth(350);
         } else if (col.getName().equals(AtsColumnToken.TargetedVersionColumn.getName())) {
            col.setWidth(120);
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
