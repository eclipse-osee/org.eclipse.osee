/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.internal.vcast;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.internal.vcast.datastore.VCastDataStoreFactory;
import org.eclipse.osee.coverage.internal.vcast.operations.VCastDataStoreToExcelOperation;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * This Blam is a convenience utility that can be used to convert a VectorCast 6.0 SQLite database. This utility is not
 * a generalized utility for converting SQLite databases into Excel spreadsheets, but rather a specialized converter for
 * the database file produced by VectorCast 6.0.
 * 
 * @author Shawn F. Cook
 */
public class SqliteToExcelBlam extends AbstractBlam {
   private final String COVERAGE_LAST_SQLITE_DB_FILE = "coverage.sqlite.db.file";
   private static final String DBFILE = "The SQLite Database File";

   @Override
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"" + DBFILE + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      String dbFilePathName = variableMap.getString(DBFILE);

      if (!dbFilePathName.equals(UserManager.getSetting(COVERAGE_LAST_SQLITE_DB_FILE))) {
         UserManager.setSetting(COVERAGE_LAST_SQLITE_DB_FILE, dbFilePathName);
      }

      File dbFile = new File(dbFilePathName);
      Conditions.checkExpressionFailOnTrue(!dbFile.canRead(), "Unable to read [%s]", dbFilePathName);

      String filename = String.format("CoverageSQLite_%s.xml", Lib.getDateTimeString());
      final File outputFile = OseeData.getFile(filename);

      VCastDataStore vcastDataStore = VCastDataStoreFactory.createDataStore(dbFile.getAbsolutePath());
      IOperation op1 = new VCastDataStoreToExcelOperation(vcastDataStore, outputFile);
      IOperation op2 = new AbstractOperation(String.format("Open %s", filename), Activator.PLUGIN_ID) {

         @Override
         protected void doWork(IProgressMonitor monitor) throws Exception {
            String path = outputFile.getAbsolutePath();
            monitor.setTaskName(String.format("Opening [%s]", path));
            Program.launch(path);
         }
      };
      return new CompositeOperation(getName(), Activator.PLUGIN_ID, op1, op2);
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(DBFILE)) {
         String sqliteDbFile = UserManager.getSetting(COVERAGE_LAST_SQLITE_DB_FILE);
         if (!Strings.isValid(sqliteDbFile)) {
            sqliteDbFile = "";
         }
         XFileTextWithSelectionDialog dbFileSelector = ((XFileTextWithSelectionDialog) xWidget);
         dbFileSelector.set(sqliteDbFile);
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "Reads a VectorCast v6.0 SQLite database and produces an Excel workbook containing one worksheet per table in the database. This is NOT related to coverage import or disposition merging functions.";
   }

   @Override
   public String getName() {
      return "SQLite Coverage DB to Excel";
   }

   @Override
   public Collection<String> getCategories() {
      return Collections.singletonList("Coverage");
   }
}
