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
package org.eclipse.osee.ats.rest.internal.build.report.model;

/**
 * @author Megumi Telles
 */
public final class AtsElementData {

   public static final String PROGRAM = "program";
   public static final String PROGRAM_ID = "programId";
   public static final String PROGRAM_NAME = "programName";
   public static final String BUILD = "build";
   public static final String BUILD_PROGRAM_ID = "buildProgramId";
   public static final String BUILD_ID = "buildId";
   public static final String BUILD_NAME = "buildName";
   public static final String WORKFLOW_CHANGE_REPORT_PATH = "workflowChangeReportPath";
   public static final String PCR_ID = "workflowPcrId";
   public static final String WORKFLOW_BUILD_ID = "workflowBuildId";
   public static final String WORKFLOW = "workflow";

   public static final String TEST_SCRIPT = "Test Script";
   public static final String REQUIREMENT = "Requirement";
   public static final String RPCR = "RPCR";
   public static final String BUILD_TRACE_REPORT = "Build Trace Report";

   public static final String ATS_DATA = "/atsData/";
   public static final String ATS_WORKFLOW_DATA_XML = "ats.workflow.data.xml";
   public static final String ATS_PROGRAM_DATA_XML = "ats.program.data.xml";
   public static final String ATS_BUILD_DATA_XML = "ats.build.data.xml";

   public static final String CHANGE_REPORT_URL_TEMPLATE = "../../../../osee/ats/changeReports/%s.xml";
   public static final String ARCHIVE_REPORT_TEMPLATE = "changeReports/%s.xml";
   public static final String ARCHIVE_SCRIPT_DIR = "testScripts/";
   public static final String ARCHIVE_SCRIPT_TEMPLATE = ARCHIVE_SCRIPT_DIR + "%s/%s.html";

   private AtsElementData() {
      //Constants
   }

}
