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
package org.eclipse.osee.ats.rest.internal.build.report.parser;

import java.io.File;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsWorkflowData;
import org.xml.sax.Attributes;

/**
 * @author Megumi Telles
 */
public class AtsWorkflowDataParser extends AtsAbstractSAXParser<AtsWorkflowData> {

   private AtsWorkflowData atsWorkflowData;
   private boolean inWorkflowBuildId = false;
   private boolean inPcrId = false;
   private boolean inWorkflowChangeReportPath = false;

   public AtsWorkflowDataParser(File workflowFileName, AtsDataHandler<AtsWorkflowData> handler) {
      super(workflowFileName, handler);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (inWorkflowBuildId) {
         atsWorkflowData.setWorkflowBuildId(new String(ch, start, length));
      } else if (inPcrId) {
         atsWorkflowData.setWorkflowPcrId(new String(ch, start, length));
      } else if (inWorkflowChangeReportPath) {
         atsWorkflowData.setWorkflowChangeReportPath(new String(ch, start, length));
      }
   }

   @Override
   public void startElement(String s, String s1, String elementName, Attributes attributes) {
      if (elementName.equals(AtsElementData.WORKFLOW)) {
         atsWorkflowData = new AtsWorkflowData();
      } else if (elementName.equals(AtsElementData.WORKFLOW_BUILD_ID)) {
         inWorkflowBuildId = true;
      } else if (elementName.equals(AtsElementData.PCR_ID)) {
         inPcrId = true;
      } else if (elementName.equals(AtsElementData.WORKFLOW_CHANGE_REPORT_PATH)) {
         inWorkflowChangeReportPath = true;
      }
   }

   @Override
   public void endElement(String s, String s1, String element) {
      if (element.equals(AtsElementData.WORKFLOW)) {
         handleData(atsWorkflowData);
      } else if (element.equals(AtsElementData.WORKFLOW_BUILD_ID)) {
         inWorkflowBuildId = false;
      } else if (element.equals(AtsElementData.PCR_ID)) {
         inPcrId = false;
      } else if (element.equals(AtsElementData.WORKFLOW_CHANGE_REPORT_PATH)) {
         inWorkflowChangeReportPath = false;
      }
   }

}
