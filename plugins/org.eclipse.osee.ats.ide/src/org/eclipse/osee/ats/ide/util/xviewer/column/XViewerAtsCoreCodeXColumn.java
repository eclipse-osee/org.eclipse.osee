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
package org.eclipse.osee.ats.ide.util.xviewer.column;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsColumnUtil;
import org.eclipse.osee.ats.api.column.AtsCoreCodeColumnToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * XViewerAtsColumn for columns that provide their text through AtsColumnService and are not strictly attribute based.
 *
 * @author Donald G. Dunne
 */
public class XViewerAtsCoreCodeXColumn extends XViewerAtsColumn implements IAltLeftClickProvider, IMultiColumnEditProvider, IXViewerValueColumn, IAttributeColumn {

   private final AtsCoreCodeColumnToken colToken;
   private final AtsApi atsApi;

   public XViewerAtsCoreCodeXColumn(AtsCoreCodeColumnToken colToken, AtsApi atsApi) {
      super(colToken.getId(), colToken.getName(), colToken.getWidth(),
         AtsColumnUtil.getXViewerAlign(colToken.getAlign()), colToken.isVisible(),
         AtsColumnUtil.getSortDataType(colToken), colToken.isColumnMultiEdit(), colToken.getDescription());
      this.colToken = colToken;
      this.atsApi = atsApi;
      setInheritParent(colToken.isInheritParent());
      setActionRollup(colToken.isActionRollup());
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsCoreCodeXColumn copy() {
      XViewerAtsCoreCodeXColumn newXCol = new XViewerAtsCoreCodeXColumn(colToken, atsApi);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return colToken.getAttrType();
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      // do nothing
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String value = "";
      try {
         if (element instanceof IAtsObject) {
            value = atsApi.getColumnService().getColumnText(colToken.getColumnId(), (IAtsObject) element);
         }
      } catch (Exception ex) {
         value = LogUtil.getCellExceptionString(ex);
      }
      return value;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn != null && !treeColumn.isDisposed() && treeItem != null && !treeItem.isDisposed() && isMultiColumnEditable()) {
         return AtsColumnUtilIde.handleAltLeftClick(treeColumn.getData(), treeItem.getData(), true);
      }
      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      AtsColumnUtilIde.handleColumnMultiEdit(treeItems, colToken.getAttrType(), (XViewer) getXViewer());
   }

}
