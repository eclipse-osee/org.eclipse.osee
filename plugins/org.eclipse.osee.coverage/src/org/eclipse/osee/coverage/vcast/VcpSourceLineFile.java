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

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverageUnitFileContentsProvider;
import org.eclipse.osee.coverage.vcast.VcpSourceFile.SourceValue;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;

/**
 * Reads results.dat file that contains <file num> <procedure num> <execution line num>
 * 
 * @author Donald G. Dunne
 */
public class VcpSourceLineFile {

   Pattern pattern = Pattern.compile("NAME:(\\w*)\\s+FILE:\\s*(.+?)\\s+START:\\s*([0-9]+)\\s+END:\\s*([0-9]+)");
   File resultsFile = null;
   private final VcpSourceFile vcpSourceFile;

   public VcpSourceLineFile(String vcastDirectory, VcpSourceFile vcpSourceFile) throws OseeCoreException {
      this.vcpSourceFile = vcpSourceFile;
      String lineFilename = vcastDirectory + "/vcast/LINE." + vcpSourceFile.getValue(SourceValue.UNIT_NUMBER);
      resultsFile = new File(lineFilename);
      if (!resultsFile.exists()) {
         throw new OseeArgumentException(String.format("VectorCast LINE.<num> file doesn't exist [%s]", lineFilename));
      }
   }

   public void createCoverageUnits(CoverageUnit parentCoverageUnit, ICoverageUnitFileContentsProvider fileContentsProvider) throws OseeCoreException {
      VcpSourceLisFile vcpSourceLisFile = vcpSourceFile.getVcpSourceLisFile();
      String contents = AFile.readFile(resultsFile);
      Matcher m = pattern.matcher(contents);
      while (m.find()) {
         CoverageUnit coverageUnit =
               new CoverageUnit(parentCoverageUnit, m.group(1), m.group(2) + ":" + m.group(3) + "-" + m.group(4),
                     fileContentsProvider);
         String source = Arrays.toString(vcpSourceLisFile.getSection(m.group(3), m.group(4)));
         coverageUnit.setFileContents(source);
         parentCoverageUnit.addCoverageUnit(coverageUnit);
      }
   }
}
