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
package org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.xscript;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.customize.FileStoreCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XScriptTableFactory extends XViewerFactory {
   private static String COLUMN_NAMESPACE = "xviewer.script.table";
   public static XViewerColumn RUN =
      new XViewerColumn(COLUMN_NAMESPACE + ".run", "Run", 42, SWT.LEFT, true, SortDataType.Boolean, false, null);
   public static XViewerColumn TEST =
      new XViewerColumn(COLUMN_NAMESPACE + ".test", "Test", 275, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn STATUS =
      new XViewerColumn(COLUMN_NAMESPACE + ".status", "Status", 125, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn RESULT =
      new XViewerColumn(COLUMN_NAMESPACE + ".result", "Result", 125, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn OUPUT_FILE =
      new XViewerColumn(COLUMN_NAMESPACE + ".outfile", "Output File", 70, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn TEST_LOCATION =
      new XViewerColumn(COLUMN_NAMESPACE + ".testlocation", "Test Location", 160, SWT.LEFT, true, SortDataType.String, false, null);

   private FileStoreCustomizations propertyStoreCustomizations;
   
   private static final String defaultCustomDataXml = "<XTreeProperties name=\"default\" namespace=\"xviewer.script.table\" guid=\"710h7sah9dtt01464nvkkv\"><xSorter><id>xviewer.script.table.test</id></xSorter><xSorter><id>xviewer.script.table.test</id></xSorter><xFilter></xFilter><xCol><id>xviewer.script.table.run</id><name>Run</name><wdth>42</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.script.table.test</id><name>Test</name><wdth>351</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.script.table.result</id><name>Result</name><wdth>110</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.script.table.status</id><name>Status</name><wdth>125</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.script.table.outfile</id><name>Output File</name><wdth>75</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol><xCol><id>xviewer.script.table.testlocation</id><name>Test Location</name><wdth>160</wdth><algn>left</algn><srtFwd>true</srtFwd><show>true</show></xCol></XTreeProperties>";
   
   public XScriptTableFactory() {
      super(COLUMN_NAMESPACE);
      File folder;
      try {
         folder = OseeData.getFolder("OteScriptTable").getLocation().toFile();
      } catch (OseeCoreException ex) {
         OseeLog.log(XScriptTableFactory.class, Level.SEVERE, ex.toString(), ex);
         folder = new File(System.getProperty("java.io.tmpdir"));
      }
      propertyStoreCustomizations = new FileStoreCustomizations(folder, "OteScript", ".xml", "DefaultOteScript.xml", defaultCustomDataXml);
      registerColumn(RUN, TEST, RESULT, STATUS, OUPUT_FILE, TEST_LOCATION);
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
