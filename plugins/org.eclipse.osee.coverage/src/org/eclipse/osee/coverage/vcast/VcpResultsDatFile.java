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
package org.eclipse.osee.coverage.vcast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Reads results.dat file that contains <file num> <procedure num> <execution line num>
 * 
 * @author Donald G. Dunne
 */
public class VcpResultsDatFile {
   private final static Pattern valuePattern = Pattern.compile("\\s*([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");
   private File resultsFile;
   private final String testUnitName;
   private final VCastVcp vCastVcp;

   public VcpResultsDatFile(VCastVcp vCastVcp, VcpResultsFile vcpResultsFile, String testUnitName) {
      this.vCastVcp = vCastVcp;
      this.testUnitName = testUnitName;
   }

   public void process(CoverageImport coverageImport, Map<String, CoverageUnit> fileNumToCoverageUnit) throws OseeCoreException {
      String resultFilename =
         vCastVcp.getVCastDirectory() + File.separator + "vcast" + File.separator + "results" + File.separator + testUnitName;
      resultsFile = new File(resultFilename);
      if (!resultsFile.exists()) {
         throw new OseeArgumentException(
            String.format("VectorCast resultsFile file doesn't exist [%s]", resultFilename));
      }

      // Add file to import record
      coverageImport.getImportRecordFiles().add(resultsFile);

      try {
         FileInputStream fstream = new FileInputStream(resultsFile);
         // Get the object of DataInputStream
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String resultsLine;
         // Loop through results file and log coverageItem as Test_Unit for each entry
         while ((resultsLine = br.readLine()) != null) {
            if (Strings.isValid(resultsLine)) {
               Matcher m = valuePattern.matcher(resultsLine);
               if (m.find()) {
                  String fileNum = m.group(1);
                  String methodNum = m.group(2);
                  String executeNum = m.group(3);

                  CoverageUnit coverageUnit = fileNumToCoverageUnit.get(fileNum);
                  if (coverageUnit == null) {
                     coverageImport.getLog().logError(
                        String.format("coverageUnit doesn't exist for unit_number [%s]", fileNum));
                     continue;
                  }
                  // Find or create new coverage item for method num /execution line
                  CoverageItem coverageItem = coverageUnit.getCoverageItem(methodNum, executeNum);
                  if (coverageItem == null) {
                     coverageImport.getLog().logError(
                        String.format(
                           "Either Method [%s] or Line [%s] do not exist for Coverage Unit [%s] found in test unit vcast/results/.dat file [%s]",
                           methodNum, executeNum, coverageUnit, testUnitName));
                  } else {
                     coverageItem.setCoverageMethod(CoverageOptionManager.Test_Unit);
                     try {
                        coverageItem.addTestUnitName(testUnitName);
                     } catch (OseeCoreException ex) {
                        coverageImport.getLog().logError(
                           String.format("Can't store test unit [%s] for coverageUnit [%s]; exception [%s]",
                              testUnitName, coverageUnit, ex.getLocalizedMessage()));
                     }
                  }
               }
            }
         }
         in.close();
         fstream.close();
         br.close();
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }
}
