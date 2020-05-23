/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.template.engine;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Megumi Telles
 */
public final class WordDocumentPropertiesRule extends AppendableRule<String> {

   private static final String LINES_PARAGRAPHS_CHARACTERS_WITH_SPACES__VERSION =
      "<o:Lines>1</o:Lines><o:Paragraphs>1</o:Paragraphs><o:CharactersWithSpaces>0</o:CharactersWithSpaces><o:Version>1</o:Version>";
   private static final String C_COMPANY = "</o:Company>";
   private static final String O_COMPANY = "<o:Company>";
   private static final String PAGES_WORDS_CHARACTERS =
      "<o:Pages>1</o:Pages><o:Words>0</o:Words><o:Characters>0</o:Characters>";
   private static final String C_LAST_SAVED = "</o:LastSaved>";
   private static final String O_LAST_SAVED = "<o:LastSaved>";
   private static final String C_CREATED = "</o:Created>";
   private static final String O_CREATED = "<o:Created>";
   private static final String C_LAST_PRINTED = "</o:LastPrinted>";
   private static final String O_LAST_PRINTED = "<o:LastPrinted>";
   private static final String REVISION_TIME = "<o:Revision>1</o:Revision><o:TotalTime>1332</o:TotalTime>";
   private static final String C_LAST_AUTHOR = "</o:LastAuthor>";
   private static final String O_LAST_AUTHOR = "<o:LastAuthor>";
   private static final String C_AUTHOR = "</o:Author>";
   private static final String O_AUTHOR = "<o:Author>";
   private static final String C_TITLE = "</o:Title>";
   private static final String O_TITLE = "<o:Title>";
   private static final String DATE = "yyyy-MM-dd-HH:mm:ss";

   private final CharSequence title;
   private final CharSequence author;
   private final CharSequence company;

   public WordDocumentPropertiesRule(String ruleName, CharSequence title, CharSequence author, CharSequence company) {
      super(ruleName);
      this.title = title;
      this.author = author;
      this.company = company;
   }

   @Override
   public void applyTo(Appendable appendable) throws IOException {
      String date = new SimpleDateFormat(DATE).format(Calendar.getInstance().getTime());

      appendable.append(O_TITLE);
      appendable.append(title);
      appendable.append(C_TITLE);
      appendable.append(O_AUTHOR);
      appendable.append(author);
      appendable.append(C_AUTHOR);
      appendable.append(O_LAST_AUTHOR);
      appendable.append(author);
      appendable.append(C_LAST_AUTHOR);
      appendable.append(REVISION_TIME);
      appendable.append(O_LAST_PRINTED);
      appendable.append(date);
      appendable.append(C_LAST_PRINTED);
      appendable.append(O_CREATED);
      appendable.append(date);
      appendable.append(C_CREATED);
      appendable.append(O_LAST_SAVED);
      appendable.append(date);
      appendable.append(C_LAST_SAVED);
      appendable.append(PAGES_WORDS_CHARACTERS);
      appendable.append(O_COMPANY);
      appendable.append("company");
      appendable.append(C_COMPANY);
      appendable.append(LINES_PARAGRAPHS_CHARACTERS_WITH_SPACES__VERSION);

   }

   @Override
   public String toString() {
      return String.format("title = [ %s ], author = [ %s ], company = [ %s ]", title, author, company);

   }
}