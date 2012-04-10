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
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Represents a single SOURCE block found in the <dir>.wrk/vcast.vcp file.
 * 
 * @author Donald G. Dunne
 */
public class VcpSourceFile {

   private static final Pattern valuePattern = Pattern.compile("(.*?):(.*?)$");
   private VcpSourceLisFile vcpSourceLisFile = null;
   private CoverageDataFile coverageDataFile = null;
   private String filename;
   private String unitNumber;
   private final VCastVcp vCastVcp;

   public VcpSourceFile(VCastVcp vCastVcp) {
      this.vCastVcp = vCastVcp;
   }

   public void addLine(String line) {
      Matcher m = valuePattern.matcher(line);
      if (m.find()) {
         if ("SOURCE_FILENAME".equals(m.group(1))) {
            filename = m.group(2);
         } else if ("UNIT_NUMBER".equals(m.group(1))) {
            unitNumber = m.group(2);
         }
      } else {
         OseeLog.logf(Activator.class, Level.SEVERE, "Unhandled VcpSourceFile line [%s]", line);
      }
   }

   public synchronized VcpSourceLisFile getVcpSourceLisFile() {
      if (vcpSourceLisFile == null) {
         vcpSourceLisFile = new VcpSourceLisFile(vCastVcp, this);
      }
      return vcpSourceLisFile;
   }

   public synchronized CoverageDataFile getCoverageDataFile(CoverageImport coverageImport) throws OseeCoreException {
      if (coverageDataFile == null) {
         coverageDataFile =
            new CoverageDataFile(coverageImport,
               vCastVcp.getVCastDirectory() + File.separator + "vcast" + File.separator + filename.replaceAll(
                  "\\.(ada|adb|c)$", "\\.xml"));
      }
      return coverageDataFile;
   }

   @Override
   public String toString() {
      return filename;
   }

   public String getFilename() {
      return filename;
   }

   public String getUnitNumber() {
      return unitNumber;
   }

   public void cleanup() {
      filename = null;
      unitNumber = null;
      if (coverageDataFile != null) {
         coverageDataFile.cleanup();
      }
      if (vcpSourceLisFile != null) {
         vcpSourceLisFile.cleanup();
      }
   }
}
