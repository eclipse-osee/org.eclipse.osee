/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.build.report.util;

import java.io.File;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;

/**
 * @author John Misinco
 */
public class InputFilesUtil {

   private InputFilesUtil() {
      // utility
   }

   private static final String CHANGE_REPORTS_PATH = "/atsData/changeReports/";

   public static File getBuildFile() {
      return new File(getBaseFileLocation() + AtsElementData.ATS_BUILD_DATA_XML);
   }

   public static File getProgramFile() {
      return new File(getBaseFileLocation() + AtsElementData.ATS_PROGRAM_DATA_XML);
   }

   public static File getWorkflowFile() {
      return new File(getBaseFileLocation() + AtsElementData.ATS_WORKFLOW_DATA_XML);
   }

   public static File getChangeReportIds(String rpcr) {
      return new File(String.format("%s/changeReports/%s_ids.txt", getBaseFileLocation(), rpcr));
   }

   public static File getChangeReportFile(String pcrId) {
      return new File(getBaseFileLocation() + CHANGE_REPORTS_PATH + pcrId + ".xml");
   }

   private static String getBaseFileLocation() {
      return System.getProperty("osee.application.server.data", null) + AtsElementData.ATS_DATA;
   }

}
