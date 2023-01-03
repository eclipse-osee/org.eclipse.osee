/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ats.ide.program;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.ats.core.rule.validate.AttributeFormatRule;
import org.eclipse.osee.ats.core.rule.validate.AttributeSetRule;
import org.eclipse.osee.ats.core.rule.validate.DeletedArtifactValidationRule;
import org.eclipse.osee.ats.core.rule.validate.ListAndBulletRule;
import org.eclipse.osee.ats.core.rule.validate.OrphanAndDuplicateParentValidationRule;
import org.eclipse.osee.ats.core.rule.validate.UniqueNameRule;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.validate.MatchingApplicabilityTagsRule;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class DemoCreateValidationRulesOperation extends AbstractOperation {
   private final TeamWorkFlowArtifact teamArt;
   public Collection<AbstractValidationRule> rules;
   private final WorkType workType;

   public DemoCreateValidationRulesOperation(XResultData results, TeamWorkFlowArtifact teamArt, Collection<AbstractValidationRule> rules, WorkType workType) {
      super("Multi Processor Build Validation Rules Operation - " + teamArt.getName(), Activator.PLUGIN_ID, null);
      this.teamArt = teamArt;
      this.rules = rules;
      this.workType = workType;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (workType == null) {
         String errorMsg =
            String.format("WorkType can not be determined for [%s][%s]", teamArt.getArtifactTypeName(), teamArt);
         throw new OseeCoreException(errorMsg);
      }

      rules.add(new AttributeSetRule(AtsApiService.get(), CoreArtifactTypes.AbstractSoftwareRequirement,
         CoreAttributeTypes.CSCI, 1, AttributeId.UNSPECIFIED));

      rules.add(new MatchingApplicabilityTagsRule(AtsApiService.get()));
      rules.add(new OrphanAndDuplicateParentValidationRule(AtsApiService.get()));
      rules.add(new DeletedArtifactValidationRule(AtsApiService.get()));

      if (workType == WorkType.Code || workType == WorkType.Requirements) {
         //Requirement word format should use approved styles for lists and bullets
         rules.add(new ListAndBulletRule(workType, AtsApiService.get(), ""));
      }

      if (workType == WorkType.Requirements) {
         rules.add(new AttributeSetRule(AtsApiService.get(), CoreArtifactTypes.AbstractSoftwareRequirement,
            CoreAttributeTypes.DataRightsClassification, 1, AttributeId.UNSPECIFIED));

         rules.add(new AttributeSetRule(AtsApiService.get(), CoreArtifactTypes.AbstractSoftwareRequirement,
            CoreAttributeTypes.Subsystem, 1, AttributeId.UNSPECIFIED));

         //Qualification Method must have a value other than "Unspecified"
         rules.add(new AttributeSetRule(AtsApiService.get(), CoreArtifactTypes.AbstractSoftwareRequirement,
            CoreAttributeTypes.QualificationMethod, 1, AttributeId.UNSPECIFIED));

         //Requirement must have a unique name
         rules.add(new UniqueNameRule(CoreArtifactTypes.AbstractSoftwareRequirement, AtsApiService.get()));

         rules.add(new AttributeSetRule(AtsApiService.get(), CoreArtifactTypes.ImplementationDetailsMsWord,
            CoreAttributeTypes.CSCI, 1, AttributeId.UNSPECIFIED));
         rules.add(new AttributeSetRule(AtsApiService.get(), CoreArtifactTypes.ImplementationDetailsMsWord,
            CoreAttributeTypes.Subsystem, 1, AttributeId.UNSPECIFIED));

         //Ensure appropriate names follow a certain format
         String ReqNameFormat = String.format("\\{.*\\}");
         rules.add(new AttributeFormatRule(AtsApiService.get(), CoreArtifactTypes.SoftwareRequirementMsWord,
            CoreAttributeTypes.Name, 1, ReqNameFormat));

         rules.add(new AttributeFormatRule(AtsApiService.get(), CoreArtifactTypes.SoftwareRequirementProcedureMsWord,
            CoreAttributeTypes.Name, 1, ReqNameFormat));

         rules.add(new AttributeFormatRule(AtsApiService.get(), CoreArtifactTypes.SoftwareRequirementFunctionMsWord,
            CoreAttributeTypes.Name, 1, ReqNameFormat));

         rules.add(new AttributeFormatRule(AtsApiService.get(), CoreArtifactTypes.ImplementationDetailsMsWord,
            CoreAttributeTypes.Name, 1, ReqNameFormat));

         rules.add(new AttributeFormatRule(AtsApiService.get(), CoreArtifactTypes.ImplementationDetailsProcedureMsWord,
            CoreAttributeTypes.Name, 1, ReqNameFormat));

         rules.add(new AttributeFormatRule(AtsApiService.get(), CoreArtifactTypes.ImplementationDetailsFunctionMsWord,
            CoreAttributeTypes.Name, 1, ReqNameFormat));
      }
   }
}