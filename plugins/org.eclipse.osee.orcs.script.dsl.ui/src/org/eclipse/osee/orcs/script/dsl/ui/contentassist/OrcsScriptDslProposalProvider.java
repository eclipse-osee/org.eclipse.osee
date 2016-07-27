/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl.ui.contentassist;

import com.google.inject.Inject;
import java.util.Collections;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver.OsField;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.OsCollectType;
import org.eclipse.osee.orcs.script.dsl.ui.IOrcsImageProvider;
import org.eclipse.osee.orcs.script.dsl.ui.IOrcsObjectProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

/**
 * see http://www.eclipse.org/Xtext/documentation.html#contentAssist on how to customize content assistant
 *
 * @author Roberto E. Escobar
 */
public class OrcsScriptDslProposalProvider extends AbstractOrcsScriptDslProposalProvider {

   @Inject
   private IOrcsObjectProvider provider;

   @Inject
   private IOrcsImageProvider imageProvider;

   @Inject
   private IFieldResolver fieldResolver;

   @Override
   public void complete_OsBranchIdOrName(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      super.complete_OsBranchIdOrName(model, ruleCall, context, acceptor);
      if (acceptor.canAcceptMoreProposals()) {
         Image image = imageProvider.getBranchImage();

         Iterable<? extends NamedId> entries = provider.getBranches();
         for (NamedId entry : entries) {
            ICompletionProposal proposal = createCompletionProposal(OrcsScriptUtil.quote(entry.getName()),
               new StyledString(entry.getName()), image, Integer.MIN_VALUE, context.getPrefix(), context);
            acceptor.accept(proposal);
         }
      }
   }

   @Override
   public void complete_OsMetaTypeId(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      super.complete_OsMetaTypeId(model, ruleCall, context, acceptor);
      String name = model.getClass().getName().toLowerCase();
      Iterable<? extends NamedId> entries;
      if (name.contains("artifact")) {
         entries = provider.getArtifactTypes();
      } else if (name.contains("attribute")) {
         entries = provider.getAttributeTypes();
      } else if (name.contains("relation")) {
         entries = provider.getRelationTypes();
      } else {
         entries = Collections.emptyList();
      }
      for (NamedId entry : entries) {
         Image image = null;
         if (name.contains("artifact")) {
            image = imageProvider.getArtifactTypeImage(entry);
         } else if (name.contains("attribute")) {
            image = imageProvider.getAttributeTypeImage(entry);
         } else if (name.contains("relation")) {
            image = imageProvider.getRelationTypeImage(entry);
         }
         ICompletionProposal proposal = createCompletionProposal(OrcsScriptUtil.quote(entry.getName()),
            new StyledString(entry.getName()), image, Integer.MIN_VALUE, context.getPrefix(), context);
         acceptor.accept(proposal);
      }
   }

   @Override
   public void completeOsCollectObjectExpression_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      super.completeOsCollectObjectExpression_Name(model, assignment, context, acceptor);
      if (acceptor.canAcceptMoreProposals()) {
         Set<OsCollectType> types = fieldResolver.getAllowedCollectTypes(model);
         for (OsCollectType type : types) {
            Image image = null;
            switch (type) {
               case ATTRIBUTES:
                  image = imageProvider.getAttributeImage();
                  break;
               case BRANCHES:
                  image = imageProvider.getBranchImage();
                  break;
               case RELATIONS:
                  image = imageProvider.getRelationImage();
                  break;
               case TXS:
                  image = imageProvider.getTxImage();
                  break;
               default:
                  break;
            }
            ICompletionProposal proposal =
               createCompletionProposal(type.getLiteral(), type.getLiteral(), image, context);
            acceptor.accept(proposal);
         }
      }
   }

   @Override
   public void completeOsCollectObjectExpression_Expressions(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
      super.completeOsCollectObjectExpression_Expressions(model, assignment, context, acceptor);
      if (acceptor.canAcceptMoreProposals()) {
         Set<? extends OsField> remaining = fieldResolver.getRemainingAllowedFields(model);
         for (OsField field : remaining) {
            ICompletionProposal proposal =
               createCompletionProposal(field.getLiteral(), field.getLiteral(), null, context);
            acceptor.accept(proposal);
         }
      }
   }

}
