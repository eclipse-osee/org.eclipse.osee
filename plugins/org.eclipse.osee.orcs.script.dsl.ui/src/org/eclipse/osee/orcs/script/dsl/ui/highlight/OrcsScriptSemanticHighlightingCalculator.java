/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl.ui.highlight;

import java.util.Iterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNullLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.util.OrcsScriptDslSwitch;
import org.eclipse.osee.orcs.script.dsl.ui.OrcsScriptDslUiConstants;
import org.eclipse.xtext.impl.TerminalRuleImpl;
import org.eclipse.xtext.nodemodel.BidiTreeIterator;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

   @Override
   public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
      if (resource == null || resource.getParseResult() == null) {
         return;
      }

      INode root = resource.getParseResult().getRootNode();
      BidiTreeIterator<INode> it = root.getAsTreeIterable().iterator();
      while (it.hasNext()) {
         INode node = it.next();
         EObject grammarElement = node.getGrammarElement();
         if (grammarElement instanceof TerminalRuleImpl) {
            TerminalRuleImpl ge = (TerminalRuleImpl) grammarElement;
            if (ge.getName().contains("_COMMENT")) {
               acceptor.addPosition(node.getOffset(), node.getLength(), OrcsScriptDslUiConstants.STYLE_ID__COMMENT);
            }
         }
      }

      HighlightingSwitch switcher = new HighlightingSwitch(acceptor);
      Iterator<EObject> iter = EcoreUtil.getAllContents(resource, true);
      while (iter.hasNext()) {
         EObject current = iter.next();
         switcher.doSwitch(current);
      }
   }

   private static final class HighlightingSwitch extends OrcsScriptDslSwitch<Void> {
      private final IHighlightedPositionAcceptor acceptor;

      public HighlightingSwitch(IHighlightedPositionAcceptor acceptor) {
         this.acceptor = acceptor;
      }

      @Override
      public Void caseOsVariable(OsVariable object) {
         INode node = getFirstFeatureNode(object);
         highlightNode(acceptor, node, OrcsScriptDslUiConstants.STYLE_ID__VARIABLE);
         return null;
      }

      @Override
      public Void caseOsNullLiteral(OsNullLiteral object) {
         INode node = getFirstFeatureNode(object);
         highlightNode(acceptor, node, OrcsScriptDslUiConstants.STYLE_ID__NULL);
         return null;
      }

      @Override
      public Void caseOsNumberLiteral(OsNumberLiteral object) {
         INode node = getFirstFeatureNode(object);
         highlightNode(acceptor, node, OrcsScriptDslUiConstants.STYLE_ID__NUMBER);
         return null;
      }

      @Override
      public Void caseOsStringLiteral(OsStringLiteral object) {
         INode node = getFirstFeatureNode(object);
         highlightNode(acceptor, node, OrcsScriptDslUiConstants.STYLE_ID__STRING);
         return null;
      }

      @Override
      public Void caseOsVariableReference(OsVariableReference object) {
         INode node = getFirstFeatureNode(object);
         highlightNode(acceptor, node, OrcsScriptDslUiConstants.STYLE_ID__VARIABLE);
         return null;
      }
   }

   private static void highlightNode(IHighlightedPositionAcceptor acceptor, INode node, String id) {
      if (node == null) {
         return;
      }
      if (node instanceof ILeafNode) {
         acceptor.addPosition(node.getOffset(), node.getLength(), id);
      } else {
         for (ILeafNode leaf : node.getLeafNodes()) {
            if (!leaf.isHidden()) {
               acceptor.addPosition(leaf.getOffset(), leaf.getLength(), id);
            }
         }
      }
   }

   private static INode getFirstFeatureNode(EObject semantic) {
      return NodeModelUtils.findActualNodeFor(semantic);
   }

}
