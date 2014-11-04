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
package org.eclipse.osee.coverage.internal.vcast.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.vcast.VCastDataStore;
import org.eclipse.osee.vcast.model.VCastBranchCoverageTable;
import org.eclipse.osee.vcast.model.VCastBranchDataTable;
import org.eclipse.osee.vcast.model.VCastFunctionTable;
import org.eclipse.osee.vcast.model.VCastInstrumentedFileTable;
import org.eclipse.osee.vcast.model.VCastMcdcCoverageConditionTable;
import org.eclipse.osee.vcast.model.VCastMcdcCoveragePairRowTable;
import org.eclipse.osee.vcast.model.VCastMcdcCoveragePairTable;
import org.eclipse.osee.vcast.model.VCastMcdcCoverageTable;
import org.eclipse.osee.vcast.model.VCastMcdcDataConditionTable;
import org.eclipse.osee.vcast.model.VCastMcdcDataTable;
import org.eclipse.osee.vcast.model.VCastProjectFileTable;
import org.eclipse.osee.vcast.model.VCastProjectTable;
import org.eclipse.osee.vcast.model.VCastResultTable;
import org.eclipse.osee.vcast.model.VCastSettingTable;
import org.eclipse.osee.vcast.model.VCastSourceFileTable;
import org.eclipse.osee.vcast.model.VCastStatementCoverageTable;
import org.eclipse.osee.vcast.model.VCastStatementDataTable;
import org.eclipse.osee.vcast.model.VCastTableData;
import org.eclipse.osee.vcast.model.VCastVersionTable;
import org.eclipse.osee.vcast.model.VCastWritableTable;

/**
 * This operation convert a VectorCast 6.0 SQLite database. This utility is not a generalized utility for converting
 * SQLite databases into Excel spreadsheets, but rather a specialized converter for the database file produced by
 * VectorCast 6.0.
 * 
 * @author Shawn F. Cook
 */
public class VCastDataStoreToExcelOperation extends AbstractOperation {

   private final VCastDataStore vcastDataStore;
   private final File file;

   public VCastDataStoreToExcelOperation(VCastDataStore vcastDataStore, File file) {
      super("Convert Sqlite To Excel", Activator.PLUGIN_ID);
      this.vcastDataStore = vcastDataStore;
      this.file = file;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      BufferedWriter writer = null;
      try {
         writer = new BufferedWriter(new FileWriter(file));
         ISheetWriter excelWriter = new ExcelXmlWriter(writer, null, "");
         try {
            List<VCastTableData<?>> datas = getDataTables();
            double stepPercent = 1 / datas.size();
            for (VCastTableData<?> data : datas) {
               checkForCancelledStatus(monitor);
               writeSheet(monitor, excelWriter, stepPercent, data);
            }
         } finally {
            excelWriter.endWorkbook();
         }
      } finally {
         Lib.close(writer);
      }
   }

   private List<VCastTableData<?>> getDataTables() {
      List<VCastTableData<?>> datas = new ArrayList<VCastTableData<?>>();
      datas.add(new VCastBranchCoverageTable());
      datas.add(new VCastBranchDataTable());
      datas.add(new VCastFunctionTable());
      datas.add(new VCastInstrumentedFileTable());
      datas.add(new VCastMcdcCoverageTable());
      datas.add(new VCastMcdcCoverageConditionTable());
      datas.add(new VCastMcdcCoveragePairRowTable());
      datas.add(new VCastMcdcCoveragePairTable());
      datas.add(new VCastMcdcDataTable());
      datas.add(new VCastMcdcDataConditionTable());
      datas.add(new VCastProjectFileTable());
      datas.add(new VCastProjectTable());
      datas.add(new VCastResultTable());
      datas.add(new VCastSettingTable());
      datas.add(new VCastSourceFileTable());
      datas.add(new VCastStatementDataTable());
      datas.add(new VCastStatementCoverageTable());
      datas.add(new VCastVersionTable());
      datas.add(new VCastWritableTable());
      return datas;
   }

   private <T> void writeSheet(IProgressMonitor monitor, ISheetWriter excelWriter, double workPercent, VCastTableData<T> data) throws Exception {
      monitor.setTaskName(String.format("Converting [%s]", data.getName()));

      Object[] columns = data.getColumns();
      excelWriter.startSheet(data.getName(), columns.length);
      try {
         excelWriter.writeRow(columns);

         Collection<T> rows = data.getRows(vcastDataStore);
         double stepPercent = !rows.isEmpty() ? workPercent / rows.size() : workPercent;
         int workStep = calculateWork(stepPercent);

         for (T item : rows) {
            checkForCancelledStatus(monitor);
            excelWriter.writeRow(data.toRow(item));
            monitor.worked(workStep);
         }

         if (rows.isEmpty()) {
            monitor.worked(workStep);
         }
      } finally {
         excelWriter.endSheet();
      }
   }
}
