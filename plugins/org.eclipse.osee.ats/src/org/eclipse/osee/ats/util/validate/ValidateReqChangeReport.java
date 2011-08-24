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
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class ValidateReqChangeReport {

   private static ValidationReportOperation prepareValidationOperation(TeamWorkFlowArtifact teamArt, Set<AttributeSetRule> attributeSetRules, Set<RelationSetRule> relationSetRules, Set<UniqueNameRule> uniqueNameRules) {
      XResultData resultData = new XResultData(false);
      ValidationReportOperation operation =
         new ValidationReportOperation(resultData, teamArt, attributeSetRules, relationSetRules, uniqueNameRules);
      return operation;
   }

   public static void run(TeamWorkFlowArtifact teamArt, Set<AttributeSetRule> attributeSetRules, Set<RelationSetRule> relationSetRules, Set<UniqueNameRule> uniqueNameRules) {
      IOperation operation = prepareValidationOperation(teamArt, attributeSetRules, relationSetRules, uniqueNameRules);
      Operations.executeAsJob(operation, true);
   }

   public static void run(TeamWorkFlowArtifact teamArt, Set<AttributeSetRule> attributeSetRules, Set<RelationSetRule> relationSetRules) {
      run(teamArt, attributeSetRules, relationSetRules, null);
   }

   public static String performValidation(TeamWorkFlowArtifact teamArt, Set<AttributeSetRule> attributeSetRules, Set<RelationSetRule> relationSetRules, Set<UniqueNameRule> uniqueNameRules) throws OseeCoreException {
      ValidationReportOperation operation =
         prepareValidationOperation(teamArt, attributeSetRules, relationSetRules, uniqueNameRules);
      return operation.performValidation();
   }

   public static String performValidation(TeamWorkFlowArtifact teamArt, Set<AttributeSetRule> attributeSetRules, Set<RelationSetRule> relationSetRules) throws OseeCoreException {
      return performValidation(teamArt, attributeSetRules, relationSetRules, null);
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
      return XResultDataUI.getHyperlink(art.getName() + "(" + art.getHumanReadableId() + ")", art.getHumanReadableId(),
         art.getBranch().getId());
   }
}