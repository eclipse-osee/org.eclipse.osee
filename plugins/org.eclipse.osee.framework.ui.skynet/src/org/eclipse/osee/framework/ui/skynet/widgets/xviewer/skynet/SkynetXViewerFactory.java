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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTreeReport;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.OseeXViewerTreeReport;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.AttributeColumn;

/**
 * @author Donald G. Dunne
 */
public class SkynetXViewerFactory extends OseeTargetXViewerFactory {

   private IOseeTreeReportProvider reportProvider;
   private static List<XViewerColumn> attrColumns;

   public SkynetXViewerFactory(String namespace, IOseeTreeReportProvider reportProvider) {
      super(namespace);
      this.reportProvider = reportProvider;
   }

   private IXViewerCustomizations xViewerCustomizations;

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      try {
         if (DbConnectionUtility.areOSEEServicesAvailable().isTrue()) {
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
         registerColumns(
            getAllAttributeColumns().toArray(new XViewerColumn[AttributeTypeManager.getAllTypes().size()]));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void registerAllAttributeColumnsForArtifacts(Collection<? extends Artifact> artifacts, boolean show) {
      registerAllAttributeColumnsForArtifacts(artifacts, show, false);
   }

   public void registerAllAttributeColumnsForArtifacts(Collection<? extends Artifact> artifacts, boolean show, boolean multiColumnEditable) {
      try {
         for (XViewerColumn xCol : SkynetXViewerFactory.getAllAttributeColumnsForArtifacts(artifacts)) {
            xCol.setShow(show);
            xCol.setMultiColumnEditable(multiColumnEditable);
            registerColumns(xCol);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   static {
      attrColumns = new LinkedList<>();
      for (AttributeType attributeType : AttributeTypeManager.getAllTypes()) {
         attrColumns.add(getAttributeColumn(attributeType));
      }
   }

   public static synchronized List<XViewerColumn> getAllAttributeColumns() {
      return attrColumns;
   }

   public static XViewerColumn getAttributeColumn(AttributeTypeToken attributeType) {
      return new AttributeColumn("attribute." + attributeType.getName(), attributeType.getName(), attributeType, 75,
         XViewerAlign.Left, false, XViewerAttributeSortDataType.get(attributeType), false, null);
   }

   /**
    * @return columns for attributes valid for at least on of the given artifacts; those with content first
    */
   public static List<XViewerColumn> getAllAttributeColumnsForArtifacts(Collection<? extends Artifact> artifacts) {
      List<XViewerColumn> columns = new ArrayList<>();
      Set<AttributeTypeToken> attrTypesUsed = new HashSet<>();
      Set<AttributeTypeToken> attributeTypes = new HashSet<>();
      try {
         for (Artifact art : artifacts) {
            attributeTypes.addAll(art.getAttributeTypes());
            // include attribute types that are used even if invalid
            for (AttributeTypeToken attrType : art.getAttributeTypesUsed()) {
               attributeTypes.add(attrType);
               attrTypesUsed.add(attrType);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      Set<String> attrNames = new HashSet<>();
      // Add Name first
      columns.add(new ArtifactNameColumn(true));
      attrNames.add("Name");
      // Add attribute types used next
      for (AttributeTypeToken attributeType : attrTypesUsed) {
         if (!attrNames.contains(attributeType.getName())) {
            columns.add(getAttributeColumn(attributeType));
            attrNames.add(attributeType.getName());
         }
      }
      // Add remainder last
      for (AttributeTypeToken attributeType : attributeTypes) {
         if (!attrNames.contains(attributeType.getName())) {
            columns.add(getAttributeColumn(attributeType));
            attrNames.add(attributeType.getName());
         }
      }
      return columns;
   }

   @Override
   public boolean isAdmin() {
      return true;
   }

   @Override
   public XViewerTreeReport getXViewerTreeReport(XViewer viewer) {
      return new OseeXViewerTreeReport(viewer, reportProvider);
   }

   public void setReportProvider(IOseeTreeReportProvider reportProvider) {
      this.reportProvider = reportProvider;
   }

   @Override
   public String getOseeTarget() {
      return "";
   }
}
