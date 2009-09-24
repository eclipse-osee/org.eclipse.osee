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
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.vcast.VcpSourceFile.Value;
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
      if (!Strings.isValid(vcastDirectory)) {
         throw new IllegalArgumentException("VectorCast directory must be specified");
      }
      File file = new File(vcastDirectory);
      if (!file.exists()) {
         throw new IllegalArgumentException(String.format("VectorCast directory doesn't exist [%s]", vcastDirectory));
      }
      coverageImport = new CoverageImport();
      File vCastVcpFile = new File(vcastDirectory + "/vcast.vcp");
      if (!vCastVcpFile.exists()) {
         throw new IllegalArgumentException(String.format("VectorCast vcast.vcp file doesn't exist [%s]",
               vcastDirectory));
      }
      VCastVcp vCastVcp = new VCastVcp(vCastVcpFile);
      for (VcpSourceFile vcpSourceFile : vCastVcp.sourceFiles) {
         CoverageUnit coverageUnit =
               new CoverageUnit(null, vcpSourceFile.getValue(Value.SOURCE_FILENAME),
                     vcpSourceFile.getValue(Value.SOURCE_DIRECTORY));
         coverageImport.addCoverageUnit(coverageUnit);
      }
      return coverageImport;
   }
}
