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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * Represents the <dir>.wrk/vcast.vcp file which lists all the source files and results files specified in this
 * directory.
 * 
 * @author Donald G. Dunne
 */
public class VCastVcp {

   List<VcpSourceFile> sourceFiles = new ArrayList<VcpSourceFile>();
   List<VcpResultsFile> resultsFiles = new ArrayList<VcpResultsFile>();
   private final String vcastDirectory;

   public VCastVcp(String vcastDirectory) throws OseeCoreException, IOException {
      this.vcastDirectory = vcastDirectory;
      File vCastVcpFile = getFile();
      if (!vCastVcpFile.exists()) {
         throw new OseeArgumentException("VectorCast vcast.vcp file doesn't exist [%s]", vcastDirectory);
      }
      VcpSourceFile vcpSourceFile = null;
      VcpResultsFile vcpResultsFile = null;
      FileInputStream fstream = new FileInputStream(vCastVcpFile);
      // Get the object of DataInputStream
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String line;
      // Loop through results file and log coverageItem as Test_Unit for each entry
      while ((line = br.readLine()) != null) {
         if (line.startsWith("SOURCE_FILE_BEGIN")) {
            vcpSourceFile = new VcpSourceFile(this);
         } else if (line.startsWith("SOURCE_FILE_END")) {
            sourceFiles.add(vcpSourceFile);
            vcpSourceFile = null;
         } else if (vcpSourceFile != null) {
            vcpSourceFile.addLine(line);
         } else if (line.startsWith("RESULT_FILE_BEGIN")) {
            vcpResultsFile = new VcpResultsFile(this);
         } else if (line.startsWith("RESULT_FILE_END")) {
            resultsFiles.add(vcpResultsFile);
            vcpResultsFile = null;
         } else if (vcpResultsFile != null) {
            vcpResultsFile.addLine(line);
         }
      }
      in.close();
      fstream.close();
      br.close();
   }

   public File getFile() {
      return new File(vcastDirectory + "/vcast.vcp");
   }

   public VcpSourceFile getSourceFile(int index) {
      for (VcpSourceFile vcpSourceFile : sourceFiles) {
         if (vcpSourceFile.getUnitNumber().equals(String.valueOf(index))) {
            return vcpSourceFile;
         }
      }
      return null;
   }

   public List<VcpSourceFile> getSourceFiles() {
      return sourceFiles;
   }

   public List<VcpResultsFile> getResultsFiles() {
      return resultsFiles;
   }

   public String getVCastDirectory() {
      return vcastDirectory;
   }

}
