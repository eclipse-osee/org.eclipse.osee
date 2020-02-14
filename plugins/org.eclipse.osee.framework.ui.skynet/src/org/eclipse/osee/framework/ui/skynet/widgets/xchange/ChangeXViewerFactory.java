/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.HierarchyIndexColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionCommentColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ViewApplicabilityColumn;

/**
 * @author Donald G. Dunne
 */
public class ChangeXViewerFactory extends SkynetXViewerFactory {

   public final static XViewerColumn Name = new XViewerColumn("framework.change.artifactNames", "Artifact name(s)", 250,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Item_Type = new XViewerColumn("framework.change.itemType", "Item Type", 100,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Item_Kind = new XViewerColumn("framework.change.itemKind", "Item Kind", 70,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Change_Type = new XViewerColumn("framework.change.changeType", "Change Type", 50,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Is_Value = new XViewerColumn("framework.change.isValue", "Is Value", 150,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Was_Value = new XViewerColumn("framework.change.wasValue", "Was Value", 150,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn paraNumber =
      new XViewerColumn("attribute.Paragraph Number", CoreAttributeTypes.ParagraphNumber.getName(), 50,
         XViewerAlign.Left, false, SortDataType.Paragraph_Number, false, null);

   public final static String NAMESPACE = "ChangeXViewer";

   public ChangeXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      registerColumns(Name, Item_Type, Item_Kind, Change_Type, Is_Value, Was_Value, paraNumber);
      registerColumns(HierarchyIndexColumn.getInstance());
      registerColumns(new IdColumn(false));
      registerColumns(new IdColumn(false));
      registerColumns(new ArtifactTypeColumn("framework.change.artifactType"));
      registerColumns(new ViewApplicabilityColumn(false));
      registerColumns(new LastModifiedDateColumn(false));
      registerColumns(new LastModifiedByColumn(false));
      registerColumns(new LastModifiedTransactionColumn(false));
      registerColumns(new LastModifiedTransactionCommentColumn(false));
      registerAllAttributeColumns();
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }
}
