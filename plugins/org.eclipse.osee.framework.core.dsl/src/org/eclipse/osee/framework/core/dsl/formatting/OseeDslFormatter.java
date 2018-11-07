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
package org.eclipse.osee.framework.core.dsl.formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.eclipse.osee.framework.core.dsl.services.OseeDslGrammarAccess;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

/**
 * This class contains custom formatting description. see :
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting on how and when to use it Also see
 * {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 * 
 * @author Roberto E. Escobar
 */
public class OseeDslFormatter extends AbstractDeclarativeFormatter implements IIndentationInformation {

   private final List<String> KEYWORDS = Arrays.asList(new String[] {
      "attribute",
      "sideAName",
      "sideAArtifactType",
      "sideBName",
      "sideBArtifactType",
      "defaultOrderType",
      "entryId",
      "multiplicity",
      "dataProvider",
      "min",
      "max",
      "taggerId",
      "enumType",
      "defaultValue",
      "entry",
      "guid",
      "uuid",
      "add",
      "remove",
      "inheritsAll",
      "description",
      "min",
      "max",
      "dataProvider",
      "defaultValue",
      "fileExtension",
      "mediaType",
      "taggerId",
      "accessContext",
      "ALLOW",
      "DENY",
      "AND",
      "OR"});

   private boolean isKeywordEntry(String current) {
      return KEYWORDS.contains(current);
   }

   @Override
   protected void configureFormatting(FormattingConfig c) {
      OseeDslGrammarAccess access = (OseeDslGrammarAccess) getGrammarAccess();

      c.setAutoLinewrap(120);
      //      c.setIndentationSpace("   ");

      Iterable<Keyword> keywords = GrammarUtil.containedKeywords(access.getGrammar());
      Stack<Keyword> openBraceStack = new Stack<>();

      for (Keyword currentKeyword : keywords) {
         String current = currentKeyword.getValue();
         if ("{".equals(current)) {
            openBraceStack.add(currentKeyword);
            c.setLinewrap().after(currentKeyword);
         } else if ("}".equals(current)) {
            c.setLinewrap().before(currentKeyword);
            c.setLinewrap(2).after(currentKeyword);
            if (!openBraceStack.isEmpty()) {
               c.setIndentation(openBraceStack.pop(), currentKeyword);
            }
         } else if (";".equals(current)) {
            c.setSpace("").before(currentKeyword);
            c.setLinewrap(1).after(currentKeyword);
         } else if (isKeywordEntry(current)) {
            c.setLinewrap().before(currentKeyword);
         }
      }
      c.setLinewrap(0, 1, 2).after(access.getImportRule());
      c.setLinewrap(0, 1, 2).before(access.getSL_COMMENTRule());
      c.setLinewrap(0, 1, 2).before(access.getML_COMMENTRule());
      c.setLinewrap(0, 1, 1).after(access.getML_COMMENTRule());
   }

   @Override
   public String getIndentString() {
      return "   ";
   }
}
