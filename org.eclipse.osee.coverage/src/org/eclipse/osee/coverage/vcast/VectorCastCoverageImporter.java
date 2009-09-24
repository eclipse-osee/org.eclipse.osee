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
package org.eclipse.osee.coverage.vcast;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.TestUnit;
import org.eclipse.osee.coverage.vcast.VcpResultsFile.ResultsValue;
import org.eclipse.osee.coverage.vcast.VcpSourceFile.SourceValue;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class VectorCastCoverageImporter implements ICoverageImporter {

   private final String vcastDirectory;
   private CoverageImport coverageImport;

   public VectorCastCoverageImporter(String vcastDirectory) {
      this.vcastDirectory = vcastDirectory;
   }

   @Override
   public CoverageImport run() {
      coverageImport = new CoverageImport();
      if (!Strings.isValid(vcastDirectory)) {
         coverageImport.getLog().logError("VectorCast directory must be specified");
         return coverageImport;
      }
      File file = new File(vcastDirectory);
      if (!file.exists()) {
         coverageImport.getLog().logError(String.format("VectorCast directory doesn't exist [%s]", vcastDirectory));
         return coverageImport;
      }
      coverageImport.getLog().logError("Changing code to read info out of coverate_data.xml");

      return coverageImport;
   }

   public CoverageImport runOld() {
      coverageImport = new CoverageImport();
      if (!Strings.isValid(vcastDirectory)) {
         coverageImport.getLog().logError("VectorCast directory must be specified");
         return coverageImport;
      }
      File file = new File(vcastDirectory);
      if (!file.exists()) {
         coverageImport.getLog().logError(String.format("VectorCast directory doesn't exist [%s]", vcastDirectory));
         return coverageImport;
      }
      VCastVcp vCastVcp = null;
      try {
         vCastVcp = new VCastVcp(vcastDirectory);
      } catch (Exception ex) {
         coverageImport.getLog().logError("Exception reading vcast.vcp file: " + ex.getLocalizedMessage());
         return coverageImport;
      }
      Map<String, CoverageUnit> fileNumToCoverageUnit = new HashMap<String, CoverageUnit>();
      for (VcpSourceFile vcpSourceFile : vCastVcp.sourceFiles) {
         CoverageUnit coverageUnit =
               new CoverageUnit(null, vcpSourceFile.getValue(SourceValue.SOURCE_FILENAME),
                     vcpSourceFile.getValue(SourceValue.SOURCE_DIRECTORY));
         coverageUnit.setText(Arrays.toString(vcpSourceFile.getVcpSourceLisFile().get()));
         // Create children coverage units from procedures/functions/methods
         vcpSourceFile.getVcpSourceLineFile().createCoverageUnits(coverageUnit);
         fileNumToCoverageUnit.put(vcpSourceFile.getValue(SourceValue.UNIT_NUMBER), coverageUnit);
         coverageImport.addCoverageUnit(coverageUnit);
      }
      for (VcpResultsFile vcpResultsFile : vCastVcp.resultsFiles) {
         TestUnit testUnit =
               new TestUnit(vcpResultsFile.getValue(ResultsValue.FILENAME),
                     vcpResultsFile.getValue(ResultsValue.DIRECTORY));
         for (String fileNum : vcpResultsFile.getVcpResultsDatFile().getFileNumbers()) {
            CoverageUnit coverageUnit = fileNumToCoverageUnit.get(fileNum);
            if (coverageUnit == null) {
               coverageImport.getLog().logError(
                     String.format("coverageUnit doesn't exist for unit_number [%s]", fileNum));
               continue;
            }
            for (Pair<String, String> methodExecutionPair : vcpResultsFile.getVcpResultsDatFile().getMethodExecutionPairs(
                  fileNum)) {
               String methodNum = methodExecutionPair.getFirst();
               String executeNum = methodExecutionPair.getSecond();
               // Find or create new coverage item for mehod num /execution line
               CoverageItem coverageItem = coverageUnit.getCoverageItem(methodNum, executeNum);
               if (coverageItem == null) {
                  try {
                     CoverageUnit methodCoverageUnit = coverageUnit.getCoverageUnit(methodNum);
                     coverageItem = new CoverageItem(methodCoverageUnit, CoverageMethodEnum.Test_Unit, executeNum);
                     coverageItem.setMethodNum(methodNum);
                     coverageUnit.addCoverageItem(coverageItem);
                  } catch (IndexOutOfBoundsException ex) {
                     coverageImport.getLog().logError(
                           String.format("Can't retrieve method [%s] from coverageUnit [%s] for test unit [%s]",
                                 methodNum, coverageUnit, testUnit));
                  }
               }
               // Relate that coverage item to the test unit that covers it
               testUnit.addCoverageItem(coverageItem);
            }
         }
         coverageImport.addTestUnit(testUnit);
      }
      return coverageImport;
   }
}
