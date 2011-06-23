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

import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Represents a single RESULTS block found in the <dir>.wrk/vcast.vcp file.
 * 
 * @author Donald G. Dunne
 */
public class VcpResultsFile {

   private String filename;
   private final static Pattern valuePattern = Pattern.compile("(.*?):(.*?)$");
   private final VCastVcp vCastVcp;

   public VcpResultsFile(VCastVcp vCastVcp) {
      this.vCastVcp = vCastVcp;
   }

   public void addLine(String line) {
      Matcher m = valuePattern.matcher(line);
      if (m.find()) {
         if ("FILENAME".equals(m.group(1))) {
            filename = m.group(2);
         }
      } else {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unhandled VcpResultsFile line [%s]", line));
      }
   }

   @Override
   public String toString() {
      return filename;
   }

   public String getFilename() {
      return filename;
   }

   public void processResultsFiles(CoverageImport coverageImport, Map<String, CoverageUnit> fileNumToCoverageUnit) throws OseeCoreException {
      VcpResultsDatFile vcpResultsDatFile = new VcpResultsDatFile(vCastVcp, this, filename);
      vcpResultsDatFile.process(coverageImport, fileNumToCoverageUnit);
   }
}
