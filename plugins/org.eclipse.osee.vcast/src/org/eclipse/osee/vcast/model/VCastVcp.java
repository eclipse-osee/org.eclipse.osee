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

   private final List<VcpSourceFile> sourceFiles = new ArrayList<VcpSourceFile>();
   private final List<VcpResultsFile> resultsFiles = new ArrayList<VcpResultsFile>();
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
