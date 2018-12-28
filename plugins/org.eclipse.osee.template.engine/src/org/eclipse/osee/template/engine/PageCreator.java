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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Generates xhtml pages using both static and dynamic substitutions. Designed to be thread safe; however, this means
 * the substitutions are shared so only reuse with multiple threads that can share the substitutions<br />
 * http://www.w3.org/2007/07/xhtml-basic-ref.html <br />
 * http://validator.w3.org/check <br />
 * 
 * @author Ryan D. Brooks
 */
public final class PageCreator {
   private static final Pattern xmlProcessingInstructionStartOrEnd = Pattern.compile("(\\?>)|(<\\?\\s*)");
   private static final Pattern xmlProcessingInstructionStart = Pattern.compile("\\s*<\\?");
   private static final Pattern emptyOrWhitespaceOnly = Pattern.compile("\\s*");
   private static final Pattern newlineStart = Pattern.compile("\\A\\r?\\n?");
   private static final Pattern newlineEnd = Pattern.compile("\\r?\\n?\\z");
   private static final Pattern processingInstructionPath = Pattern.compile("path=\"([^ ]+)");
   private static final Pattern processingInstructionId = Pattern.compile("id=\"([^\"]+)");
   private static final Pattern includeInstructionParseAttribute = Pattern.compile("parse=\"(.)+\"");
   private static final int NumOfCharsInTypicalSmallPage = 7000;

   private final IResourceRegistry registry;
   private final ConcurrentHashMap<String, AppendableRule<?>> substitutions =
      new ConcurrentHashMap<>();

   public PageCreator(IResourceRegistry registry) {
      this.registry = registry;
   }

   public Set<String> getAttributes() {
      return Collections.unmodifiableSet(substitutions.keySet());
   }

   public void addSubstitution(AppendableRule<?>... rules) {
      for (AppendableRule<?> rule : rules) {
         substitutions.put(rule.getName(), rule);
      }
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
      AppendableRule<?> appendableRule = substitutions.get(ruleName);
      if (appendableRule != null) {
         return appendableRule.toString();
      }
      return null;
   }

   public void readKeyValuePairs(ResourceToken keyValueResource) {
      readKeyValuePairs(keyValueResource.getInputStream());
   }

   public void readKeyValuePairs(InputStream keyValueStream) {
      Scanner scanner = new Scanner(keyValueStream, "UTF-8");
      scanner.useDelimiter(xmlProcessingInstructionStartOrEnd);
      try {
         String id = null;
         StringBuilder substitution = new StringBuilder(NumOfCharsInTypicalSmallPage);
         boolean isProcessingInstruction = false;

         while (scanner.hasNext()) {
            isProcessingInstruction = scanner.findInLine(xmlProcessingInstructionStart) != null;
            String token = scanner.next();
            if (emptyOrWhitespaceOnly.matcher(token).matches()) {
               continue;
            }
            if (token.startsWith("include")) {
               handleInclude(substitution, token);
               substitution = new StringBuilder(NumOfCharsInTypicalSmallPage);
            } else if (token.startsWith("rule ")) {
               handleRule(substitution, token);
            } else { // handle key
               if (isProcessingInstruction) {
                  // next instruction found, complete previous one
                  if (id != null) {
                     addKeyValuePair(id, substitution);
                     substitution = new StringBuilder(NumOfCharsInTypicalSmallPage);
                  }
                  id = token;
               } else {
                  substitution.append(trimToken(token));
               }
            }
         }
         // finish last token
         if (id != null) {
            addKeyValuePair(id, substitution);
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      } finally {
         scanner.close();
      }
   }

   private void handleRule(StringBuilder substitution, String token) {
      String ruleName = getRuleNamefromToken(token);
      if (!Strings.isValid(ruleName)) {
         throw new OseeArgumentException("no rule name specified in token %s", token);
      }
      AppendableRule<?> rule = substitutions.get(ruleName);
      if (rule == null) {
         throw new OseeArgumentException("no rule was found for token %s", token);
      }

      Map<String, String> attributes = new HashMap<>();
      // parse the arguments
      parseArgumentList(token, attributes);
      try {
         rule.applyTo(substitution, attributes);

      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
      String id = attributes.get("id");
      addKeyValuePair(id, substitution);
   }

   public String realizePage(ResourceToken templateResource) {
      return realizePage(templateResource, true);
   }

   public Appendable realizePage(ResourceToken templateResource, Appendable page) {
      return realizePage(templateResource, page, true);
   }

   private String realizePage(ResourceToken templateResource, boolean processInstructions) {
      StringBuilder page = new StringBuilder(NumOfCharsInTypicalSmallPage);
      return realizePage(templateResource, page, processInstructions).toString();
   }

   private Appendable realizePage(ResourceToken templateResource, Appendable page, boolean processInstructions) {
      return realizePage(templateResource.getInputStream(), page, processInstructions);
   }

   private Appendable realizePage(InputStream template, Appendable page, boolean processInstructions) {
      Scanner scanner = new Scanner(template, "UTF-8");
      try {
         scanner.useDelimiter(xmlProcessingInstructionStartOrEnd);

         boolean isProcessingInstruction =
            processInstructions ? scanner.findInLine(xmlProcessingInstructionStart) != null : false;
         while (scanner.hasNext()) {
            processToken(page, scanner.next(), isProcessingInstruction);
            isProcessingInstruction = processInstructions ? !isProcessingInstruction : false;
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      } finally {
         scanner.close();
      }
      return page;
   }

   private Long toUniversalId(String token) {
      Matcher pathMatcher = processingInstructionPath.matcher(token);
      if (pathMatcher.find()) {
         return Long.parseLong(pathMatcher.group(1), 16);
      }
      throw new OseeArgumentException("include path from token[%s] is not of the expected format", token);
   }

   private String getProcessingInstructionId(String token) {
      Matcher idMatcher = processingInstructionId.matcher(token);
      if (idMatcher.find()) {
         return idMatcher.group(1);
      }
      return null;
   }

   private void processToken(Appendable page, String token, boolean isProcessingInstruction) throws IOException {
      if (isProcessingInstruction) {
         if (token.startsWith("include")) {
            appendInclude(page, token);
         } else if (token.startsWith("xml ") || token.startsWith("mso-application ")) {
            page.append("<?");
            page.append(token);
            page.append("?>");
         } else if (token.startsWith("rule")) {
            // get rule name
            String ruleName = getRuleNamefromToken(token);
            if (!Strings.isValid(ruleName)) {
               throw new OseeArgumentException("no rule name specified in token %s", token);
            }
            AppendableRule<?> rule = substitutions.get(ruleName);
            if (rule == null) {
               throw new OseeArgumentException("no rule was found for token %s", token);
            }
            Map<String, String> attributes = new HashMap<>();
            // parse the arguments
            parseArgumentList(token, attributes);
            try {
               rule.applyTo(page, attributes);
            } catch (IOException ex) {
               throw new OseeCoreException(ex);
            }
         } else {
            AppendableRule<?> rule = substitutions.get(token);
            if (rule == null) {
               throw new OseeArgumentException("no substitution was found for token %s", token);
            }
            Map<String, String> attributes = new HashMap<>();
            // parse the arguments
            parseArgumentList(token, attributes);
            try {
               rule.applyTo(page, attributes);
            } catch (IOException ex) {
               throw new OseeCoreException(ex);
            }
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

   private boolean parseInclude(String token) {
      boolean toReturn = true;
      Matcher matcher = includeInstructionParseAttribute.matcher(token);
      if (matcher.find()) {
         toReturn = Boolean.parseBoolean(matcher.group(1));
      }
      return toReturn;
   }

   private void handleInclude(StringBuilder substitution, String token) throws IOException {
      appendInclude(substitution, token);
      String id = getProcessingInstructionId(token);
      if (id != null) {
         addKeyValuePair(id, substitution);
      }
   }

   private void appendInclude(Appendable page, String tokenStr) throws IOException {
      Long universalId = toUniversalId(tokenStr);
      boolean parseInclude = parseInclude(tokenStr);
      ResourceToken token = registry.getResourceToken(universalId);
      String name = token.getName();

      if (name.endsWith(".css")) {
         page.append("/* ");
         page.append(name);
         page.append(" */\n");
      } else if (name.endsWith(".html")) {
         page.append("<!-- ");
         page.append(name);
         page.append(" -->\n");
      }

      page.append(realizePage(token, parseInclude));
   }

   @Override
   public String toString() {
      return substitutions.toString();
   }

   private String getRuleNamefromToken(String token) {
      String toReturn = "";
      String search = "name=\"";
      int iName = token.indexOf(search);
      if (iName > -1) {
         iName += search.length();
         toReturn = token.substring(iName);
         int iQuote = toReturn.indexOf("\"");
         toReturn = toReturn.substring(0, iQuote);
      }
      return toReturn;
   }

   private void parseArgumentList(String token, Map<String, String> attributes) {
      /****************************************************
       * it is actually easier to parse this from the end of the string instead of the beginning
       */
      StringBuilder working = new StringBuilder(token.trim());
      int iSpace = working.lastIndexOf(" ");
      int iEqual = working.lastIndexOf("=\"");
      int iQuote = working.lastIndexOf("\"");
      String key = "";
      String value = null;
      while (iSpace != -1) {
         if (iQuote < iSpace) {
            // An attribute without a value (i.e. keyword)
            key = working.substring(iSpace + 1).trim();
         } else {
            if (iSpace > iEqual) {
               iSpace = working.substring(0, iEqual).lastIndexOf(" ");
            }
            key = working.substring(iSpace, iEqual).trim();
            value = working.substring(iEqual + 2, working.length() - 1);
         }
         if (!key.equalsIgnoreCase("name")) {
            attributes.put(key, value);
         }
         key = "";
         value = null;
         working.delete(iSpace, working.length());
         iSpace = working.lastIndexOf(" ");
         iEqual = working.lastIndexOf("=\"");
         iQuote = working.lastIndexOf("\"");

      }
   }
}