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
package org.eclipse.osee.coverage.internal.vcast.operations;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.internal.vcast.executor.ExecutorWorkerFactory;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.SimpleCoverageUnitFileContentsProvider;
import org.eclipse.osee.coverage.vcast.CoverageImportData;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.vcast.model.CoverageDataSubProgram;
import org.eclipse.osee.vcast.model.VCastVcp;

/**
 * @author Donald G. Dunne
 */
public class VCastAdaCoverage_V5_3_ImportOperation extends AbstractOperation {

   private final CoverageImport coverageImport;
   private final CoverageImportData input;

   public VCastAdaCoverage_V5_3_ImportOperation(CoverageImportData input, CoverageImport coverageImport) {
      super("VectorCast Import (v5.3)", Activator.PLUGIN_ID);
      this.input = input;
      this.coverageImport = coverageImport;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!Strings.isValid(input.getVCastDirectory())) {
         coverageImport.getLog().logError("VectorCast directory must be specified");
         throw new OseeArgumentException("VectorCast directory must be specified");
      }

      File file = new File(input.getVCastDirectory());
      if (!file.exists()) {
         String message = String.format("VectorCast directory doesn't exist [%s]", input.getVCastDirectory());
         coverageImport.getLog().logError(message);
         throw new OseeArgumentException(message);
      }

      coverageImport.setImportDirectory(input.getVCastDirectory());

      coverageImport.setCoverageUnitFileContentsProvider(new SimpleCoverageUnitFileContentsProvider());

      addConfigFiles();

      VCastVcp vCastVcp = createVCastVcp();
      if (vCastVcp != null) {

         try {
            coverageImport.addImportRecordFile(vCastVcp.getFile());
         } catch (Exception ex) {
            coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
         }

         coverageImport.setLocation(input.getVCastDirectory());

         // Create file and subprogram Coverage Units and execution line Coverage Items
         Map<String, CoverageUnit> fileNumToCoverageUnit = new ConcurrentHashMap<String, CoverageUnit>();

         ExecutorWorkerFactory factory =
            new ExecutorWorkerFactory(monitor, fileNumToCoverageUnit, coverageImport, input);

         factory.processSourceFilesProcessor(vCastVcp.getSourceFiles());
         System.gc();

         factory.processResultFilesProcessor(vCastVcp.getResultsFiles());
         fileNumToCoverageUnit.clear();
         fileNumToCoverageUnit = null;
         System.gc();

         Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram =
            new ConcurrentHashMap<CoverageUnit, CoverageDataSubProgram>();
         verifyCoveredTotalFromXmlWithVCastVcpAndResultsDir(monitor, methodCoverageUnitToCoverageDataSubProgram);
         methodCoverageUnitToCoverageDataSubProgram.clear();
         methodCoverageUnitToCoverageDataSubProgram = null;
         System.gc();

         IOperation operation =
            VCastOperationFactory.createValidateAggregateReportOp(coverageImport.getLog(), input.getVCastDirectory(),
               coverageImport);
         Operations.executeWorkAndCheckStatus(operation);
         operation = null;
         System.gc();

         IOperation operation1 = VCastOperationFactory.createVerifyVcpFilesOp(coverageImport.getLog(), input, vCastVcp);
         Operations.executeWorkAndCheckStatus(operation1);
         operation1 = null;
         vCastVcp = null;
         System.gc();
      }
   }

   public void verifyCoveredTotalFromXmlWithVCastVcpAndResultsDir(IProgressMonitor progressMonitor, Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram) {
      progressMonitor.beginTask("Verifing Covered Total From .xml with VCastVcp and Results Files", 1);
      // Verifying VectorCast covered/total items from vcast/<unit>.xml with data imported from results read from vcast.vcp and results dir
      coverageImport.getLog().log(
         "\nVerifying VectorCast covered/total items from vcast/<unit>.xml with data imported from results read from vcast.vcp and results dir");
      boolean error = false;
      for (Entry<CoverageUnit, CoverageDataSubProgram> entry : methodCoverageUnitToCoverageDataSubProgram.entrySet()) {
         CoverageUnit methodCoverageUnit = entry.getKey();
         CoverageDataSubProgram coverageDataSubProgram = entry.getValue();
         String usevectorcast53 = System.getProperty("usevectorcast53", null);
         if (Strings.isValid(usevectorcast53)) {
            int totalCoverageItems = methodCoverageUnit.getCoverageItems(false).size();
            int coveredCoverageItems = methodCoverageUnit.getCoverageItemsCount(false, CoverageOptionManager.Test_Unit);
            if (totalCoverageItems != coverageDataSubProgram.getTotal() || coveredCoverageItems != coverageDataSubProgram.getCovered()) {
               coverageImport.getLog().logError(
                  String.format(
                     "Imported covered/total items [%d/%d] doesn't match VectorCast [%d/%d] reported in .xml file for coverage unit [%s]",
                     coveredCoverageItems, totalCoverageItems, coverageDataSubProgram.getCovered(),
                     coverageDataSubProgram.getTotal(), methodCoverageUnit));
               error = true;
            }
         }
      }
      if (!error) {
         coverageImport.getLog().log("Ok");
      }
      progressMonitor.worked(1);
   }

   private VCastVcp createVCastVcp() {
      VCastVcp toReturn = null;
      try {
         File vCastVcpFile = new File(input.getVCastDirectory() + "/vcast.vcp");
         Conditions.checkExpressionFailOnTrue(!vCastVcpFile.exists(), "VectorCast file doesn't exist [%s]",
            vCastVcpFile);
         VCastVcp vCastVcp = new VCastVcp(vCastVcpFile);
         IOperation op = new VCastVcpLoadOperation(vCastVcp.getFile().toURI(), vCastVcp);
         Operations.executeWorkAndCheckStatus(op);
         toReturn = vCastVcp;
      } catch (Exception ex) {
         coverageImport.getLog().logError("Exception reading vcast.vcp file: " + ex.getLocalizedMessage());
      }
      return toReturn;
   }

   private void addConfigFiles() {
      // Add config files to import record
      try {
         coverageImport.addImportRecordFile(new File(input.getVCastDirectory() + File.separator + "CCAST_.CFG"));
      } catch (Exception ex) {
         coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
      }
      try {
         coverageImport.addImportRecordFile(new File(
            input.getVCastDirectory() + File.separator + "vcast" + File.separator + "build_info.xml"));
      } catch (Exception ex) {
         coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
      }
   }

}
