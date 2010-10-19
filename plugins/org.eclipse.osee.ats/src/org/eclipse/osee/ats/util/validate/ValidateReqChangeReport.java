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
package org.eclipse.osee.ats.util.validate;

import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ValidateReqChangeReport {

   public static void run(TeamWorkFlowArtifact teamArt, Set<AttributeSetRule> attributeSetRules, Set<RelationSetRule> relationSetRules) {
      XResultData resultData = new XResultData();
      IOperation operation = new ValidationReportOperation(resultData, teamArt, attributeSetRules, relationSetRules);
      Operations.executeAsJob(operation, true);
   }

   static void reportStatus(XResultData rd, IStatus status) {
      String message = status.getMessage();
      switch (status.getSeverity()) {
         case IStatus.ERROR:
            rd.logError(message);
            break;
         case IStatus.WARNING:
            rd.logWarning(message);
            break;
         default:
            rd.log(message);
            break;
      }
   }

   static String getRequirementHyperlink(Artifact art) {
      return XResultData.getHyperlink(art.getName() + "(" + art.getHumanReadableId() + ")", art.getHumanReadableId(),
         art.getBranch().getId());
   }
}