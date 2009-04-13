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
package org.eclipse.nebula.widgets.xviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerFactory implements IXViewerFactory {

   private String namespace;

   /**
    * @param namespace the namespace to set
    */
   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   private final List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
   private final Map<String, XViewerColumn> idToColumn = new HashMap<String, XViewerColumn>();

   public XViewerFactory(String namespace) {
      this.namespace = namespace;
   }

   public void registerColumn(XViewerColumn... columns) {
      if (columns.length == 0) throw new IllegalArgumentException("columns can't be null");
      for (XViewerColumn xCol : columns) {
         if (!columnRegistered(xCol)) {
            this.columns.add(xCol);
            idToColumn.put(xCol.getId(), xCol);
         }
      }
   }

   public boolean columnRegistered(XViewerColumn column) {
      return this.columns.contains(column);
   }

   public void clearColumnRegistration() {
      this.columns.clear();
      idToColumn.clear();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#createNewXSorter(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer)
    */
   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultTableCustomizeData(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer)
    */
   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      custData.setNameSpace(namespace);
      custData.getColumnData().setColumns(getColumns());
      return custData;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getDefaultXViewerColumn(java.lang.String)
    */
   @Override
   public XViewerColumn getDefaultXViewerColumn(String id) {
      // Return a copy so don't corrupt original definition of column
      XViewerColumn col = idToColumn.get(id);
      if (col == null) return null;
      return col.copy();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getXViewerCustomizations(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer)
    */
   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return new XViewerCustomizations();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getXViewerCustomMenu()
    */
   @Override
   public XViewerCustomMenu getXViewerCustomMenu() {
      return new XViewerCustomMenu();
   }

   public String getNamespace() {
      return namespace;
   }

   public List<XViewerColumn> getColumns() {
      // Return a copy so don't corrupt original definition of column
      List<XViewerColumn> columnCopy = new ArrayList<XViewerColumn>();
      for (XViewerColumn xCol : columns) {
         columnCopy.add(xCol.copy());
      }
      return columnCopy;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.IXViewerFactory#getXViewerTreeReport(org.eclipse.nebula.widgets.xviewer.XViewer)
    */
   @Override
   public XViewerTreeReport getXViewerTreeReport(XViewer viewer) {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.customize.IXViewerFactory#isFilterUiAvailable()
    */
   @Override
   public boolean isFilterUiAvailable() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.customize.IXViewerFactory#isHeaderBarAvailable()
    */
   @Override
   public boolean isHeaderBarAvailable() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.customize.IXViewerFactory#isLoadedStatusLabelAvailable()
    */
   @Override
   public boolean isLoadedStatusLabelAvailable() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.customize.IXViewerFactory#isSearchUiAvailable()
    */
   @Override
   public boolean isSearchUiAvailable() {
      return true;
   }

}
