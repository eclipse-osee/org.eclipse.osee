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
package org.eclipse.osee.coverage.internal.vcast.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.vcast.VCastValidateDatFileSyntax;
import org.eclipse.osee.vcast.model.VCastVcp;
import org.eclipse.osee.vcast.model.VcpResultsFile;

/**
 * @author Donald G. Dunne
 */
public class VcpResultsFileWorker extends AbstractVcpFileWorker<VcpResultsFile> {

   private final static Pattern VALUE_PATTERN = Pattern.compile("\\s*([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");

   private final Matcher valueMatcher;

   public VcpResultsFileWorker(XResultData logger, IProgressMonitor monitor, AtomicInteger numberProcessed, int totalSize, List<VcpResultsFile> toProcess, List<File> processed, Map<String, CoverageUnit> fileNumToCoverageUnit) {
      super(logger, monitor, numberProcessed, totalSize, toProcess, processed, fileNumToCoverageUnit);

      valueMatcher = VALUE_PATTERN.matcher("");
   }

   @Override
   protected void process(VcpResultsFile data) throws Exception {
      String testUnitName = data.getFilename();
      VCastVcp vCastVcp = data.getvCastVcp();

      String resultFilename =
         vCastVcp.getVCastDirectory() + File.separator + "vcast" + File.separator + "results" + File.separator + testUnitName;

      File resultsFile = new File(resultFilename);

      Conditions.checkExpressionFailOnTrue(!resultsFile.exists(), "VectorCast resultsFile file doesn't exist [%s]",
         resultFilename);

      try {
         getProcessed().add(resultsFile);
      } catch (Exception ex) {
         getLogger().logError("Error Adding Import Record File (see Error Log): " + ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      process(resultsFile, data.getFilename());
   }

   private void process(File resultsFile, String testUnitName) throws Exception {
      BufferedReader br = null;
      try {
         br = new BufferedReader(new FileReader(resultsFile));
         String resultsLine;
         // Loop through results file and log coverageItem as Test_Unit for each entry
         while ((resultsLine = br.readLine()) != null) {
            if (Strings.isValid(resultsLine)) {
               Result datFileSyntaxResult = VCastValidateDatFileSyntax.validateDatFileSyntax(resultsLine);
               if (!datFileSyntaxResult.isTrue()) {
                  getLogger().logErrorWithFormat("Invalid VCast DAT file syntax - %s -  [%s] ",
                     datFileSyntaxResult.getText(), testUnitName);
               } else {
                  valueMatcher.reset(resultsLine);
                  if (valueMatcher.find()) {
                     String fileNum = valueMatcher.group(1);
                     String methodNum = valueMatcher.group(2);
                     String executeNum = valueMatcher.group(3);

                     CoverageUnit coverageUnit = getFileNumToCoverageUnit().get(fileNum);
                     if (coverageUnit == null) {
                        getLogger().logErrorWithFormat("coverageUnit doesn't exist for unit_number [%s]", fileNum);
                     } else {
                        // Find or create new coverage item for method num /execution line
                        CoverageItem coverageItem = coverageUnit.getCoverageItem(methodNum, executeNum);
                        if (coverageItem == null) {
                           getLogger().logErrorWithFormat(
                              "Either Method [%s] or Line [%s] do not exist for Coverage Unit [%s] found in test unit vcast/results/.dat file [%s]",
                              methodNum, executeNum, coverageUnit, testUnitName);
                        } else {
                           coverageItem.setCoverageMethod(CoverageOptionManager.Test_Unit);
                           try {
                              coverageItem.addTestUnitName(testUnitName);
                           } catch (OseeCoreException ex) {
                              getLogger().logErrorWithFormat(
                                 "Can't store test unit [%s] for coverageUnit [%s]; exception [%s]", testUnitName,
                                 coverageUnit, ex.getLocalizedMessage());
                           }
                        }
                     }
                  }
               }
            }
         }
      } finally {
         Lib.close(br);
      }
   }

}