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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerGuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerHridColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerLastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerLastModifiedDateColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ChangeXViewerFactory extends SkynetXViewerFactory {

   public static XViewerColumn Name =
         new XViewerColumn("framework.change.artifactNames", "Artifact name(s)", 250, SWT.LEFT, true,
               SortDataType.String, false, null);
   public static XViewerColumn Item_Type =
         new XViewerColumn("framework.change.itemType", "Item Type", 100, SWT.LEFT, true, SortDataType.String, false,
               null);
   public static XViewerColumn Item_Kind =
         new XViewerColumn("framework.change.itemKind", "Item Kind", 70, SWT.LEFT, true, SortDataType.String, false,
               null);
   public static XViewerColumn Change_Type =
         new XViewerColumn("framework.change.changeType", "Change Type", 50, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn Is_Value =
         new XViewerColumn("framework.change.isValue", "Is Value", 150, SWT.LEFT, true, SortDataType.String, false,
               null);
   public static XViewerColumn Was_Value =
         new XViewerColumn("framework.change.wasValue", "Was Value", 150, SWT.LEFT, true, SortDataType.String, false,
               null);
   public static XViewerColumn paraNumber =
         new XViewerColumn("attribute.Imported Paragraph Number", CoreAttributeTypes.PARAGRAPH_NUMBER.getName(), 50,
               SWT.LEFT, false, SortDataType.Paragraph_Number, false, null);

   public static String NAMESPACE = "osee.skynet.gui.ChangeXViewer";

   public ChangeXViewerFactory() {
      super(NAMESPACE);
      registerColumns(Name, Item_Type, Item_Kind, Change_Type, Is_Value, Was_Value, paraNumber);
      registerColumns(new XViewerHridColumn());
      registerColumns(new XViewerGuidColumn(false));
      registerColumns(new XViewerArtifactTypeColumn("framework.change.artifactType", "Artifact Type", true));
      registerColumns(new XViewerLastModifiedDateColumn(false));
      registerColumns(new XViewerLastModifiedByColumn(false));
      registerAllAttributeColumns();
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

}
