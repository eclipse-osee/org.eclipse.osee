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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class SkynetXViewerFactory extends XViewerFactory {

   /**
    * @param namespace
    */
   public SkynetXViewerFactory(String namespace) {
      super(namespace);
   }

   private IXViewerCustomizations xViewerCustomizations;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory#getXViewerCustomizations()
    */
   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      try {
         if (ConnectionHandler.isConnected()) {
            if (xViewerCustomizations == null) {
               xViewerCustomizations = new SkynetCustomizations(this);
            }
            return xViewerCustomizations;
         }
      } catch (IllegalStateException ex) {
         OSEELog.logException(SkynetXViewerFactory.class,
               "Failed to retrieve XViewer customizations from the persistence layer.", ex, false);
      }
      return new XViewerCustomizations();
   }

   public void registerAllAttributeColumns() {
      try {
         registerColumn(getAllAttributeColumns().toArray(new XViewerColumn[AttributeTypeManager.getTypes().size()]));
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
   }

   public void registerAllAttributeColumnsForArtifacts(Collection<? extends Artifact> artifacts, boolean show) {
      try {
         for (XViewerColumn xCol : SkynetXViewerFactory.getAllAttributeColumnsForArtifacts(artifacts)) {
            xCol.setShow(show);
            registerColumn(xCol);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
   }

   public static List<XViewerColumn> getAllAttributeColumns() throws SQLException {
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
      for (AttributeType attributeType : AttributeTypeManager.getTypes()) {
         columns.add(getAttributeColumn(attributeType));
      }
      return columns;
   }

   public static XViewerColumn getAttributeColumn(AttributeType attributeType) {
      return new XViewerAttributeColumn("attribute." + attributeType.getName(), attributeType.getName(),
            attributeType.getName(), 75, SWT.LEFT, false, XViewerAttributeSortDataType.get(attributeType), false, null);
   }

   /**
    * Return columns for attributes valid for at least on of the given artifacts
    * 
    * @param artifacts
    * @return
    * @throws SQLException
    */
   public static List<XViewerColumn> getAllAttributeColumnsForArtifacts(Collection<? extends Artifact> artifacts) throws SQLException {
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
      Set<AttributeType> attributeTypes = new HashSet<AttributeType>();
      try {
         for (Artifact art : artifacts) {
            attributeTypes.addAll(art.getAttributeTypes());
         }
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      Set<String> attrNames = new HashSet<String>();
      // Add Name first
      columns.add(new XViewerArtifactNameColumn("Name"));
      attrNames.add("Name");
      for (AttributeType attributeType : attributeTypes) {
         if (!attrNames.contains(attributeType.getName())) {
            columns.add(getAttributeColumn(attributeType));
            attrNames.add(attributeType.getName());
         }
      }
      return columns;
   }
}
