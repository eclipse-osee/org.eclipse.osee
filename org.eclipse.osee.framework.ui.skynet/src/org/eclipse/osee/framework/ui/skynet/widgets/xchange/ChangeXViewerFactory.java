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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerAttributeSortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeFromChangeColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ChangeXViewerFactory extends SkynetXViewerFactory {

   public static String COLUMN_NAMESPACE = "framework.change.";
   public static final XViewerColumn Name =
         new XViewerColumn(COLUMN_NAMESPACE + "artifactNames", "Artifact name(s)", 250, SWT.LEFT, true,
               SortDataType.String, false);
   public static final XViewerColumn Item_Type =
         new XViewerColumn(COLUMN_NAMESPACE + "itemType", "Item Type", 100, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Item_Kind =
         new XViewerColumn(COLUMN_NAMESPACE + "itemKind", "Item Kind", 70, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Change_Type =
         new XViewerColumn(COLUMN_NAMESPACE + "changeType", "Change Type", 50, SWT.LEFT, true, SortDataType.String,
               false);
   public static final XViewerColumn Is_Value =
         new XViewerColumn(COLUMN_NAMESPACE + "isValue", "Is Value", 150, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Was_Value =
         new XViewerColumn(COLUMN_NAMESPACE + "wasValue", "Was Value", 300, SWT.LEFT, true, SortDataType.String, false);

   public static List<XViewerColumn> columns;
   public static Map<String, XViewerColumn> idToColumn = null;

   public ChangeXViewerFactory() {
      loadColumnData();
   }

   private static synchronized void loadColumnData() {
      if (idToColumn == null) {
         idToColumn = new HashMap<String, XViewerColumn>(50);
         columns = new ArrayList<XViewerColumn>(50);
         columns.addAll(Arrays.asList(Name, Item_Type, Item_Kind, Change_Type, Is_Value, Was_Value));
         for (XViewerColumn xCol : columns) {
            idToColumn.put(xCol.getId(), xCol);
         }
         try {
            for (AttributeType attributeType : AttributeTypeManager.getTypes()) {
               XViewerAttributeFromChangeColumn newCol =
                     new XViewerAttributeFromChangeColumn(null, attributeType.getName(), attributeType.getName(), 75,
                           75, SWT.LEFT, false, XViewerAttributeSortDataType.get(attributeType));
               columns.add(newCol);
               idToColumn.put(newCol.getId(), newCol);
            }
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
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

   private Branch getBranch(XViewer xViewer) throws OseeCoreException, SQLException {
      Branch branch = ((ChangeXViewer) xViewer).getXChangeViewer().getBranch();
      if (branch == null) {
         TransactionId transId = ((ChangeXViewer) xViewer).getXChangeViewer().getTransactionId();
         if (transId != null) return transId.getBranch();
      }
      return null;
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
