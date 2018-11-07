/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.ui.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.osee.ats.dsl.ui.internal.AtsDslActivator;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
/**
 * @author Donald G. Dunne
 */
public class AtsDslProposalProvider extends AbstractAtsDslProposalProvider {

   @Override
   public void completeToState_Options(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      if (acceptor.canAcceptMoreProposals()) {
         for (String stateName : Arrays.asList("AsDefault", "AsReturn", "OverrideAttributeValidation")) {
            String displayProposalAs = stateName;
            ICompletionProposal proposal = createCompletionProposal(stateName, displayProposalAs, null, context);
            acceptor.accept(proposal);
         }
      }
   }

   @Override
   public void completeStateDef_PageType(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      if (acceptor.canAcceptMoreProposals()) {
         for (String stateName : Arrays.asList("Working", "Completed", "Cancelled")) {
            String displayProposalAs = stateName;
            ICompletionProposal proposal = createCompletionProposal(stateName, displayProposalAs, null, context);
            acceptor.accept(proposal);
         }
      }
   }

   @Override
   public void completeStateDef_Rules(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      if (acceptor.canAcceptMoreProposals()) {
         List<String> rules = Arrays.asList("RequireStateHourSpentPrompt", "AddDecisionValidateBlockingReview",
            "AddDecisionValidateNonBlockingReview", "AllowTransitionWithWorkingBranch", "ForceAssigneesToTeamLeads",
            "RequireTargetedVersion", "AllowPrivilegedEditToTeamMember", "AllowPrivilegedEditToTeamMemberAndOriginator",
            "AllowPrivilegedEditToAll", "AllowEditToAll", "AllowAssigneeToAll");
         Collections.sort(rules);
         for (String stateName : rules) {
            String displayProposalAs = stateName;
            ICompletionProposal proposal = createCompletionProposal(stateName, displayProposalAs, null, context);
            acceptor.accept(proposal);
         }
      }
   }

   @Override
   public void completeWidgetDef_Option(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      provideWidgetOptions(context, acceptor);
   }

   private void provideWidgetOptions(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      if (acceptor.canAcceptMoreProposals()) {
         List<String> rules = Arrays.asList("REQUIRED_FOR_TRANSITION", "NOT_REQUIRED_FOR_TRANSITION",
            "REQUIRED_FOR_COMPLETION", "NOT_REQUIRED_FOR_COMPLETION", "ENABLED", "NOT_ENABLED", "EDITABLE",
            "NOT_EDITABLE", "MULTI_SELECT", "HORIZONTAL_LABEL", "VERTICAL_LABEL", "LABEL_AFTER", "LABEL_BEFORE",
            "NO_LABEL", "SORTED", "ADD_DEFAULT_VALUE", "NO_DEFAULT_VALUE", "BEGIN_COMPOSITE_4", "BEGIN_COMPOSITE_6",
            "BEGIN_COMPOSITE_8", "BEGIN_COMPOSITE_10", "END_COMPOSITE", "FILL_NONE", "FILL_HORIZONTALLY",
            "FILL_VERTICALLY", "ALIGN_LEFT", "ALIGN_RIGHT", "ALIGN_CENTER");
         for (String stateName : rules) {
            String displayProposalAs = stateName;
            ICompletionProposal proposal = createCompletionProposal(stateName, displayProposalAs, null, context);
            acceptor.accept(proposal);
         }
      }
   }

   @Override
   public void completeWidgetDef_XWidgetName(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      if (acceptor.canAcceptMoreProposals()) {
         List<String> rules = Arrays.asList("XTextDam", "XComboBooleanDam", "XComboDam(option,option,option)",
            "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)", "XCheckboxDam", "XFloatDam", "XIntegerDam", "XLabel");
         for (String stateName : rules) {
            String proposalValue = "\"" + stateName + "\"";
            String displayProposalAs = proposalValue;
            ICompletionProposal proposal = createCompletionProposal(proposalValue, displayProposalAs, null, context);
            acceptor.accept(proposal);
         }
      }
   }

   @Override
   public void completeAttrWidget_Option(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      super.completeAttrWidget_Option(model, assignment, context, acceptor);
      provideWidgetOptions(context, acceptor);
   }

   @Override
   public void completeWidgetDef_AttributeName(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      super.completeWidgetDef_AttributeName(model, assignment, context, acceptor);
      provideAttributeNameOptions(context, acceptor);
   }

   private void provideAttributeNameOptions(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      if (acceptor.canAcceptMoreProposals()) {
         try {
            List<String> attrs = new ArrayList<>();
            for (AttributeType type : AttributeTypeManager.getAllTypes()) {
               attrs.add(type.getName());
            }
            Collections.sort(attrs);
            for (String attrName : attrs) {
               String displayProposalAs = attrName;
               ICompletionProposal proposal =
                  createCompletionProposal("\"" + attrName + "\"", displayProposalAs, null, context);
               acceptor.accept(proposal);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsDslActivator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void completeAttrWidget_AttributeName(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      super.completeAttrWidget_AttributeName(model, assignment, context, acceptor);
      provideAttributeNameOptions(context, acceptor);
   }

}
