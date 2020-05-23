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

package org.eclipse.osee.orcs.script.dsl.formatting;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsCollectAllFieldsExpressionElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsCollectClauseElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsCollectObjectExpressionElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsFindClauseElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsFollowClauseElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsQueryExpressionElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsQueryStatementElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsTemplateLiteralElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.OsVariableDeclarationElements;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess.ScriptVersionElements;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.formatting.IElementMatcherProvider.IElementMatcher;
import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.AbstractFormattingConfig.ElementPattern;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.eclipse.xtext.formatting.impl.FormattingConfigBasedStream;
import org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper;
import org.eclipse.xtext.parsetree.reconstr.ITokenStream;
import org.eclipse.xtext.util.Pair;

/**
 * This class contains custom formatting description. see : http://www.eclipse.org/Xtext/documentation.html#formatting
 * on how and when to use it Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 * 
 * @author Roberto E. Escobar
 */
public class OrcsScriptDslFormatter extends AbstractDeclarativeFormatter implements IIndentationInformation {

   @Override
   public String getIndentString() {
      return OrcsScriptDslConstants.FORMATTING__INDENT_STRING;
   }

   @Override
   protected void configureFormatting(FormattingConfig cfg) {
      OrcsScriptDslGrammarAccess grmr = (OrcsScriptDslGrammarAccess) getGrammarAccess();

      cfg.setAutoLinewrap(120);

      // Comments
      cfg.setLinewrap(0, 1, 2).before(grmr.getSL_COMMENTRule());

      cfg.setLinewrap(2, 2, 2).before(grmr.getML_COMMENTRule());
      cfg.setLinewrap(0, 1, 1).after(grmr.getML_COMMENTRule());

      // General Keywords
      for (Keyword keyword : grmr.findKeywords(";")) {
         cfg.setNoSpace().before(keyword);
      }
      for (Keyword keyword : grmr.findKeywords(".")) {
         cfg.setNoSpace().before(keyword);
         cfg.setNoSpace().after(keyword);
      }
      for (Keyword keyword : grmr.findKeywords("..")) {
         cfg.setNoSpace().before(keyword);
         cfg.setNoSpace().after(keyword);
      }
      for (Keyword keyword : grmr.findKeywords(",")) {
         cfg.setNoSpace().before(keyword);
         cfg.setSpace(" ").after(keyword);
      }
      for (Pair<Keyword, Keyword> pair : grmr.findKeywordPairs("(", ")")) {
         cfg.setNoSpace().after(pair.getFirst());
         cfg.setNoSpace().before(pair.getSecond());
      }
      for (Pair<Keyword, Keyword> pair : grmr.findKeywordPairs("[", "]")) {
         cfg.setNoSpace().after(pair.getFirst());
         cfg.setNoSpace().before(pair.getSecond());
      }

      // >>>>>>>>>>>>>>>>> Grammar
      // Script Version
      ScriptVersionElements version = grmr.getScriptVersionAccess();
      cfg.setLinewrap(0, 1, 2).before(version.getGroup());
      cfg.setLinewrap(1).after(version.getGroup());

      // templates
      OsTemplateLiteralElements template = grmr.getOsTemplateLiteralAccess();
      cfg.setNoSpace().after(template.getLeftCurlyBracketLeftCurlyBracketKeyword_1());
      cfg.setNoSpace().before(template.getRightCurlyBracketRightCurlyBracketKeyword_3());

      // variables
      OsVariableDeclarationElements variable = grmr.getOsVariableDeclarationAccess();
      cfg.setLinewrap(0, 1, 2).before(variable.getVarKeyword_1());
      cfg.setLinewrap(1, 1, 2).after(variable.getSemicolonKeyword_4());

      // start from ....;
      OsQueryExpressionElements query = grmr.getOsQueryExpressionAccess();
      cfg.setLinewrap(1).before(query.getNameStartKeyword_1_0());

      OsQueryStatementElements stmt = grmr.getOsQueryStatementAccess();
      cfg.setLinewrap(2).after(stmt.getSemicolonKeyword_1());

      // find
      OsFindClauseElements find = grmr.getOsFindClauseAccess();
      cfg.setLinewrap(1).before(find.getGroup());
      cfg.setLinewrap(1).after(find.getGroup());

      // follow
      OsFollowClauseElements follow = grmr.getOsFollowClauseAccess();
      cfg.setLinewrap(1).before(follow.getGroup());
      cfg.setLinewrap(1).after(follow.getGroup());

      // collect
      OsCollectClauseElements collect = grmr.getOsCollectClauseAccess();
      cfg.setLinewrap(1).before(collect.getNameCollectKeyword_0_0());
      cfg.setLinewrap(1).after(collect.getGroup());

      OsCollectObjectExpressionElements collectObj = grmr.getOsCollectObjectExpressionAccess();
      cfg.setLinewrap().after(collectObj.getLeftCurlyBracketKeyword_3());
      cfg.setIndentationIncrement().before(collectObj.getGroup_4_1());
      cfg.setIndentationDecrement().after(collectObj.getGroup_4_1());
      cfg.setLinewrap().before(collectObj.getRightCurlyBracketKeyword_5());

      OsCollectAllFieldsExpressionElements allFields = grmr.getOsCollectAllFieldsExpressionAccess();
      cfg.setNoLinewrap().before(allFields.getNameAsteriskKeyword_1_0());
      cfg.setNoLinewrap().after(allFields.getNameAsteriskKeyword_1_0());
   }

   @Override
   public ITokenStream createFormatterStream(String indent, ITokenStream out, boolean preserveWhitespaces) {
      return new MyTokenStream(out, indent, getConfig(), createMatcher(), getHiddenTokenHelper(), preserveWhitespaces);
   }

   private class MyTokenStream extends FormattingConfigBasedStream {

      public MyTokenStream(ITokenStream out, String indentation, FormattingConfig cfg, IElementMatcher<ElementPattern> matcher, IHiddenTokenHelper hiddenTokenHelper, boolean preserveSpaces) {
         super(out, indentation, cfg, matcher, hiddenTokenHelper, preserveSpaces);
      }

      private EObject previous = null;

      @Override
      public void writeSemantic(EObject grammarElement, String value) throws IOException {
         previous = grammarElement;
         super.writeSemantic(grammarElement, value);
      }

      @Override
      public void writeHidden(EObject grammarElement, String value) throws IOException {
         String newValue = value;

         if (grammarElement instanceof TerminalRule) {
            if ("SL_COMMENT".equals(((TerminalRule) grammarElement).getName())) {
               if (previous instanceof Keyword) {
                  if ("{".toString().equals(((Keyword) previous).getValue())) {
                     newValue = getIndentString() + value;
                  }
               }
            }
            if ("ML_COMMENT".equals(((TerminalRule) grammarElement).getName())) {
               StringBuilder sb = new StringBuilder();
               sb.append(getLineSeparator());
               sb.append(getLineSeparator());
               for (int i = 0; i < indentationLevel; i++) {
                  sb.append(getIndentString());
               }

               if (previous instanceof Keyword) {
                  if ("{".toString().equals(((Keyword) previous).getValue())) {
                     sb.append(getIndentString());
                  }
               }
               sb.append(value).append(getLineSeparator());
               newValue = sb.toString();
            }
         }
         super.writeHidden(grammarElement, newValue);
      }

      @Override
      public void flush() throws IOException {
         super.flush();
      }
   };

}
