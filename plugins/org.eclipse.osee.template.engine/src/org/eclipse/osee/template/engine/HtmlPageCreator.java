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

package org.eclipse.osee.template.engine;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;

/**
 * Generates xhtml pages using both static and dynamic substitutions. Designed to be thread safe; however, this means
 * the substitutions are shared so only reuse with multiple threads that can share the substitutions<br />
 * http://www.w3.org/2007/07/xhtml-basic-ref.html <br />
 * http://validator.w3.org/check <br />
 * 
 * @author Ryan D. Brooks
 */
public final class HtmlPageCreator {
   private static final Pattern xmlProcessingInstructionStartOrEnd = Pattern.compile("(\\?>)|(<\\?\\s*)");
   private static final Pattern xmlProcessingInstructionStart = Pattern.compile("\\s*<\\?");
   private static final Pattern emptyOrWhitespaceOnly = Pattern.compile("\\s*");
   private static final Pattern newlineStart = Pattern.compile("\\A\\r?\\n?");
   private static final Pattern newlineEnd = Pattern.compile("\\r?\\n?\\z");
   private static final Pattern processingInstructionPath = Pattern.compile("path=\"([^ ]+)");
   private static final Pattern processingInstructionId = Pattern.compile("id=\"([^\"]+)");
   private static final int NumOfCharsInTypicalSmallPage = 7000;

   private final IResourceRegistry registry;
   private final ConcurrentHashMap<String, AppendableRule> substitutions =
      new ConcurrentHashMap<String, AppendableRule>();

   public HtmlPageCreator(IResourceRegistry registry) {
      this.registry = registry;
   }

   public void addSubstitution(AppendableRule rule) {
      substitutions.put(rule.getName(), rule);
   }

   public void addKeyValuePair(String ruleName, CharSequence substitution) {
      addSubstitution(new StringRule(ruleName, substitution));
   }

   public void addKeyValuePairs(String... keyValues) {
      for (int i = 0; i < keyValues.length; i += 2) {
         addKeyValuePair(keyValues[i], keyValues[i + 1]);
      }
   }

   public void addKeyValuePairs(Iterable<String> keyValuePairs) {
      Iterator<String> iterator = keyValuePairs.iterator();
      while (iterator.hasNext()) {
         addKeyValuePair(iterator.next(), iterator.next());
      }
   }

   public String getValue(String ruleName) {
      return substitutions.get(ruleName).toString();
   }

   public void readKeyValuePairs(ResourceToken valuesResource) throws Exception {
      InputStream keyValueStream = registry.getResource(valuesResource.getGuid());
      readKeyValuePairs(keyValueStream);
   }

   public void readKeyValuePairs(InputStream keyValueStream) throws Exception {
      Scanner scanner = new Scanner(keyValueStream, "UTF-8");
      scanner.useDelimiter(xmlProcessingInstructionStartOrEnd);

      try {
         while (scanner.hasNext()) {
            String token = scanner.next();
            if (emptyOrWhitespaceOnly.matcher(token).matches()) {
               continue;
            }

            String id;
            CharSequence substitution;

            if (token.startsWith("include")) {
               id = getProcessingInstructionId(token);
               substitution = new StringBuilder(NumOfCharsInTypicalSmallPage);
               appendInclude((StringBuilder) substitution, token);
            } else {
               id = token;
               if (scanner.hasNext()) {
                  substitution = trimToken(scanner.next());
               } else {
                  substitution = "";
               }
            }

            addKeyValuePair(id, substitution);
         }
      } finally {
         scanner.close();
      }
   }

   public String realizePage(ResourceToken templateResource) throws Exception {
      return realizePage(registry.getResource(templateResource.getGuid())).toString();
   }

   private StringBuilder realizePage(InputStream template) throws Exception {
      StringBuilder page = new StringBuilder(NumOfCharsInTypicalSmallPage);
      Scanner scanner = new Scanner(template, "UTF-8");
      try {
         scanner.useDelimiter(xmlProcessingInstructionStartOrEnd);

         boolean isProcessingInstruction = scanner.findInLine(xmlProcessingInstructionStart) != null;
         while (scanner.hasNext()) {
            processToken(page, scanner.next(), isProcessingInstruction);
            isProcessingInstruction = !isProcessingInstruction;
         }
      } finally {
         scanner.close();
      }
      return page;
   }

   private Long toUniversalId(String token) throws Exception {
      Matcher pathMatcher = processingInstructionPath.matcher(token);
      if (pathMatcher.find()) {
         return Long.parseLong(pathMatcher.group(1), 16);
      }
      throw new OseeArgumentException("include path from token[%s] is in of the expected format", token);
   }

   private String getProcessingInstructionId(String token) throws Exception {
      Matcher idMatcher = processingInstructionId.matcher(token);
      if (idMatcher.find()) {
         return idMatcher.group(1);
      }
      throw new OseeArgumentException("include id from token [%s] is in of the expected format", token);
   }

   private void processToken(StringBuilder page, String token, boolean isProcessingInstruction) throws Exception {
      if (isProcessingInstruction) {
         if (token.startsWith("include")) {
            appendInclude(page, token);
         } else if (token.startsWith("xml ")) {
            page.append("<?");
            page.append(token);
            page.append("?>");
         } else {
            AppendableRule rule = substitutions.get(token);
            if (rule == null) {
               throw new OseeArgumentException("no substitution was found for token %s", token);
            }
            rule.applyTo(page);
         }
      } else {
         page.append(token);
      }
   }

   private String trimToken(String token) {
      Matcher newLineStartMatcher = newlineStart.matcher(token);
      int beginIndex = newLineStartMatcher.find() ? newLineStartMatcher.end() : 0;

      Matcher newLineEndMatcher = newlineEnd.matcher(token);
      int endIndex = newLineEndMatcher.find() ? newLineEndMatcher.start() : token.length();

      return beginIndex > endIndex ? "" : token.substring(beginIndex, endIndex);
   }

   private void appendInclude(Appendable page, String tokenStr) throws Exception {
      Long universalId = toUniversalId(tokenStr);
      ResourceToken token = registry.getResourceToken(universalId);
      String name = token.getName();
      boolean css = name.endsWith(".css");

      page.append(css ? "/* " : "<!-- ");
      page.append(name);
      page.append(css ? " */\n" : " -->\n");
      page.append(realizePage(token));
   }

   @Override
   public String toString() {
      return substitutions.toString();
   }
}