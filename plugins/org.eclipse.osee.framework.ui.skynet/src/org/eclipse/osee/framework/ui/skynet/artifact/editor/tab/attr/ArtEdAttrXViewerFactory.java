/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class ArtEdAttrXViewerFactory extends SkynetXViewerFactory {

   public static XViewerColumn AttrTypeName = new XViewerColumn("osee.attribute.name", "Attr Type Name", 180,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Value =
      new XViewerColumn("osee.attribute.value", "Value", 330, XViewerAlign.Left, true, SortDataType.String, true, null);
   public static XViewerColumn Id =
      new XViewerColumn("osee.attribute.id", "Attr Id", 70, XViewerAlign.Left, true, SortDataType.Long, false, null);
   public static XViewerColumn AttrTypeId = new XViewerColumn("osee.attribute.type.id", "Attr Type Id", 130,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn GammaId = new XViewerColumn("osee.attribute.gamma.id", "Gamma Id", 80, XViewerAlign.Left,
      true, SortDataType.String, false, null);

   private final static String NAMESPACE = "ArtEdAttr";

   public ArtEdAttrXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      registerColumns(AttrTypeName, Value, Id, GammaId, AttrTypeId);
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(ArtEdAttrXViewerFactory.AttrTypeName.getId())) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(ArtEdAttrXViewerFactory.AttrTypeName.getId());
      return customizeData;
   }

}
