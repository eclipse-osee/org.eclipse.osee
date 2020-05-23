/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.vcast.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the <dir>.wrk/vcast.vcp file which lists all the source files and results files specified in this
 * directory.
 * 
 * @author Donald G. Dunne
 */
public class VCastVcp {

   private final List<VcpSourceFile> sourceFiles = new ArrayList<>();
   private final List<VcpResultsFile> resultsFiles = new ArrayList<>();
   private final File file;

   public VCastVcp(File file) {
      this.file = file;
   }

   public File getFile() {
      return file;
   }

   public List<VcpSourceFile> getSourceFiles() {
      return sourceFiles;
   }

   public List<VcpResultsFile> getResultsFiles() {
      return resultsFiles;
   }

   public String getVCastDirectory() {
      return file.getParent();
   }

}
