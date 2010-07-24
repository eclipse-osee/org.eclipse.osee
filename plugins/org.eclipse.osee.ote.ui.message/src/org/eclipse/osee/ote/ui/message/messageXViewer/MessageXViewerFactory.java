/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.messageXViewer;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.customize.FileStoreCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.swt.SWT;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageXViewerFactory extends XViewerFactory {

   private static String VIEWER_NAMESPACE = "org.eclipse.osee.ote.message.MessageXViewer";
   public static XViewerColumn name = new XViewerColumn("osee.message.name", "Name", 200, SWT.LEFT, true,
      SortDataType.String, false, null);
   public static XViewerColumn psUpdateCount = new XViewerColumn("osee.message.udpates", "Updates", 60, SWT.LEFT, true,
      SortDataType.Integer, false, null);
   public static XViewerColumn value = new XViewerColumn("osee.message.value", "Value", 100, SWT.LEFT, true,
      SortDataType.String, false, null);
   public static XViewerColumn byteOffset = new XViewerColumn("osee.message.offset", "Offset", 50, SWT.LEFT, true,
      SortDataType.Integer, false, null);
   public static XViewerColumn msb = new XViewerColumn("osee.message.msb", "MSB", 50, SWT.LEFT, true,
      SortDataType.Integer, false, null);
   public static XViewerColumn lsb = new XViewerColumn("osee.message.lsb", "LSB", 50, SWT.LEFT, true,
      SortDataType.Integer, false, null);
   public static XViewerColumn bitSize = new XViewerColumn("osee.message.bitSize", "Bit Size", 60, SWT.LEFT, true,
      SortDataType.Integer, false, null);
   public static XViewerColumn elementType = new XViewerColumn("osee.message.type", "Type", 100, SWT.LEFT, true,
      SortDataType.String, false, null);

   private final FileStoreCustomizations propertyStoreCustomizations;

   private static final String defaultCustomDataXml =
      "<XTreeProperties name=\"default\" namespace=\"org.eclipse.osee.ote.message.MessageXViewer\" guid=\"61ksp8mbrj8501466lgqc8\"><xSorter><id>osee.message.offset</id><id>osee.message.msb</id></xSorter><xFilter></xFilter><xCol><id>osee.message.name</id><name>Name</name><wdth>225</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>osee.message.udpates</id><name>Updates</name><wdth>60</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>osee.message.value</id><name>Value</name><wdth>100</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>osee.message.offset</id><name>Offset</name><wdth>50</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>osee.message.msb</id><name>MSB</name><wdth>50</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>osee.message.lsb</id><name>LSB</name><wdth>50</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>osee.message.bitSize</id><name>Bit Size</name><wdth>58</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>osee.message.type</id><name>Type</name><wdth>68</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol></XTreeProperties>";

   public MessageXViewerFactory() {
      super(VIEWER_NAMESPACE);

      File folder;
      try {
         folder = OseeData.getFolder("OteMessageWatch").getLocation().toFile();
      } catch (Exception ex) {
         OseeLog.log(MessageXViewerFactory.class, Level.SEVERE, ex);
         folder = new File(System.getProperty("java.io.tmpdir"));
      }
      propertyStoreCustomizations =
         new FileStoreCustomizations(folder, "OteMessageWatch", ".xml", "DefaultMessageWatch.xml", defaultCustomDataXml);
      registerColumns(name, psUpdateCount, value, /* hex, */byteOffset, msb, lsb, bitSize, elementType);
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return propertyStoreCustomizations;
   }

   @Override
   public boolean isAdmin() {
      return true;
   }

}
