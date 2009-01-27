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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerTreeReport;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.OseeXViewerTreeReport;
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
         if (SkynetGuiPlugin.areOSEEServicesAvailable().isTrue()) {
            if (xViewerCustomizations == null) {
               xViewerCustomizations = new SkynetCustomizations(this);
            }
            return xViewerCustomizations;
         }
      } catch (Throwable ex) {
         OseeLog.log(SkynetXViewerFactory.class, Level.SEVERE,
               "Failed to retrieve XViewer customizations from the persistence layer.", ex);
      }
      return new XViewerCustomizations();
   }

   public void registerAllAttributeColumns() {
      try {
         registerColumn(getAllAttributeColumns().toArray(new XViewerColumn[AttributeTypeManager.getAllTypes().size()]));
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public void registerAllAttributeColumnsForArtifacts(Collection<? extends Artifact> artifacts, boolean show) {
      try {
         for (XViewerColumn xCol : SkynetXViewerFactory.getAllAttributeColumnsForArtifacts(artifacts)) {
            xCol.setShow(show);
            registerColumn(xCol);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public static List<XViewerColumn> getAllAttributeColumns() throws OseeDataStoreException {
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
      for (AttributeType attributeType : AttributeTypeManager.getAllTypes()) {
         columns.add(getAttributeColumn(attributeType));
      }
      return columns;
   }

   public static XViewerColumn getAttributeColumn(AttributeType attributeType) {
      return new XViewerAttributeColumn("attribute." + attributeType.getName(), attributeType.getName(),
            attributeType.getName(), 75, SWT.LEFT, false, XViewerAttributeSortDataType.get(attributeType), false, null);
   }

   /**
    * @param artifacts
    * @return columns for attributes valid for at least on of the given artifacts
    */
   public static List<XViewerColumn> getAllAttributeColumnsForArtifacts(Collection<? extends Artifact> artifacts) {
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
      Set<AttributeType> attributeTypes = new HashSet<AttributeType>();
      try {
         for (Artifact art : artifacts) {
            attributeTypes.addAll(art.getAttributeTypes());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.IXViewerFactory#isAdmin()
    */
   @Override
   public boolean isAdmin() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewerFactory#getXViewerTreeReport(org.eclipse.nebula.widgets.xviewer.XViewer)
    */
   @Override
   public XViewerTreeReport getXViewerTreeReport(XViewer viewer) {
      return new OseeXViewerTreeReport(viewer);
   }

}
