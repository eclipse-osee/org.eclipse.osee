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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.coverage.vcast.VcpSourceFile.SourceValue;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;

/**
 * @author Donald G. Dunne
 */
public class VCastVcp {

   List<VcpSourceFile> sourceFiles = new ArrayList<VcpSourceFile>();
   List<VcpResultsFile> resultsFiles = new ArrayList<VcpResultsFile>();
   private final String vcastDirectory;

   public VCastVcp(String vcastDirectory) throws OseeCoreException {
      this.vcastDirectory = vcastDirectory;
      File vCastVcpFile = getFile();
      if (!vCastVcpFile.exists()) {
         throw new OseeArgumentException(String.format("VectorCast vcast.vcp file doesn't exist [%s]", vcastDirectory));
      }
      VcpSourceFile vcpSourceFile = null;
      VcpResultsFile vcpResultsFile = null;
      for (String line : AFile.readFile(vCastVcpFile).split("\n")) {
         if (line.startsWith("SOURCE_FILE_BEGIN")) {
            vcpSourceFile = new VcpSourceFile(vcastDirectory);
         } else if (line.startsWith("SOURCE_FILE_END")) {
            sourceFiles.add(vcpSourceFile);
            vcpSourceFile = null;
         } else if (vcpSourceFile != null) {
            vcpSourceFile.addLine(line);
         } else if (line.startsWith("RESULT_FILE_BEGIN")) {
            vcpResultsFile = new VcpResultsFile(vcastDirectory);
         } else if (line.startsWith("RESULT_FILE_END")) {
            resultsFiles.add(vcpResultsFile);
            vcpResultsFile = null;
         } else if (vcpResultsFile != null) {
            vcpResultsFile.addLine(line);
         }
      }
   }

   public File getFile() {
      return new File(vcastDirectory + "/vcast.vcp");
   }

   public VcpSourceFile getSourceFile(int index) {
      for (VcpSourceFile vcpSourceFile : sourceFiles) {
         if (vcpSourceFile.getValue(SourceValue.UNIT_NUMBER).equals(String.valueOf(index))) {
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

}
