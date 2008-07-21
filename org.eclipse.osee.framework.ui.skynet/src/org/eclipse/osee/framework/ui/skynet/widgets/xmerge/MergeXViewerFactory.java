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
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class MergeXViewerFactory extends SkynetXViewerFactory {

   private XViewer xViewer;
   public static String COLUMN_NAMESPACE = "framework.change.";
   public static final XViewerColumn Conflict_Resolved =
         new XViewerColumn(COLUMN_NAMESPACE + "conflictResolved", "Conflict Resolution", 43, SWT.LEFT, true,
               SortDataType.String, false);
   public static final XViewerColumn Artifact_Name =
         new XViewerColumn(COLUMN_NAMESPACE + "artifactName", "Artifact Name", 200, SWT.LEFT, true,
               SortDataType.String, false);
   public static final XViewerColumn Type =
         new XViewerColumn(COLUMN_NAMESPACE + "artifactType", "Artifact Type", 150, SWT.LEFT, true,
               SortDataType.String, false);
   public static final XViewerColumn Change_Item =
         new XViewerColumn(COLUMN_NAMESPACE + "conflictingItem", "Conflicting Item", 150, SWT.LEFT, true,
               SortDataType.String, false);
   public static final XViewerColumn Source =
         new XViewerColumn(COLUMN_NAMESPACE + "sourceValue", "Source Value", 100, SWT.LEFT, true, SortDataType.String,
               false);
   public static final XViewerColumn Destination =
         new XViewerColumn(COLUMN_NAMESPACE + "destinationValue", "Destination Value", 100, SWT.LEFT, true,
               SortDataType.String, false);
   public static final XViewerColumn Merged =
         new XViewerColumn(COLUMN_NAMESPACE + "mergedValue", "Merged Value", 100, SWT.LEFT, true, SortDataType.String,
               false);

   public static final List<XViewerColumn> columns =
         Arrays.asList(Conflict_Resolved, Artifact_Name, Type, Change_Item, Source, Destination, Merged);
   public static Map<String, XViewerColumn> idToColumn = null;

   public MergeXViewerFactory() {
      if (idToColumn == null) {
         idToColumn = new HashMap<String, XViewerColumn>();
         for (XViewerColumn xCol : columns) {
            idToColumn.put(xCol.getId(), xCol);
         }
      }
   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   public CustomizeData getDefaultTableCustomizeData(XViewer xViewer) {
      CustomizeData custData = new CustomizeData();
      ArrayList<XViewerColumn> cols = new ArrayList<XViewerColumn>();
      for (XViewerColumn xCol : columns) {
         xCol.setXViewer(xViewer);
         cols.add(xCol);
      }
      custData.getColumnData().setColumns(cols);
      return custData;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultXViewerColumn()
    */
   public XViewerColumn getDefaultXViewerColumn(String id) {
      return idToColumn.get(id);
   }

}
