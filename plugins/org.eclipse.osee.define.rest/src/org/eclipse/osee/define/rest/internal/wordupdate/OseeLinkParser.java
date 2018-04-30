/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal.wordupdate;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class OseeLinkParser {

   private static final Matcher LEGACY_MATCHER = Pattern.compile("/(.*?)/guid/(.*?)/(.*)").matcher("");
   private static final Matcher LINK_CONTEXT_MATCHER = Pattern.compile("/(.*?)\\?(.*)").matcher("");
   private static final Matcher PARAMETER_MATCHER = Pattern.compile("([^&]*?)=([^&]*)").matcher("");

   private final Map<String, String> parameterMap;

   public OseeLinkParser() {
      this.parameterMap = new HashMap<>();
   }

   public void parse(String link) {
      parameterMap.clear();
      link = link.replaceAll("&amp;", "&");
      boolean wasHandled = parseOldSchoolStyleLinks(link);
      if (!wasHandled) {
         parseNewStyleRequests(link);
      }
   }

   public int getId() {
      int branchUuid = -1;
      if (parameterMap != null) {
         String branchUuidStr = parameterMap.get("branchUuid");
         if (Strings.isValid(branchUuidStr)) {
            try {
               branchUuid = Integer.parseInt(branchUuidStr);
            } catch (Exception ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
         }
      }
      return branchUuid;
   }

   public String getGuid() {
      String guidToReturn = null;
      if (parameterMap != null) {
         String guid = parameterMap.get("guid");
         if (Strings.isValid(guid)) {
            if (!GUID.isValid(guid)) {
               try {
                  guid = URLDecoder.decode(guid, "UTF-8");
                  if (GUID.isValid(guid)) {
                     guidToReturn = guid;
                  }
               } catch (Exception ex) {
                  OseeCoreException.wrapAndThrow(ex);
               }
            } else {
               guidToReturn = guid;
            }
         }
      }
      return guidToReturn;
   }

   /**
    * Process new style requests are of the following format: http://127.0.0.1:<port>/
    * <ProcessType>?key1=value1&key2=value2...&key3=value3
    */
   private void parseNewStyleRequests(String link) {
      String noHostStr = link.replaceFirst("^http:\\/\\/(.*?)\\/", "/");
      Matcher matcher = LINK_CONTEXT_MATCHER;
      matcher.reset(noHostStr);
      if (matcher.matches()) {
         Matcher dataMatcher = PARAMETER_MATCHER;
         dataMatcher.reset(matcher.group(2));
         while (dataMatcher.find()) {
            parameterMap.put(dataMatcher.group(1), dataMatcher.group(2));
         }
      }
   }

   /**
    * Process old format: http://127.0.0.1:<port>/get/guid/<guid>/<ats,Define> old format should be removed once all
    * legacy references are change to new format
    */
   private boolean parseOldSchoolStyleLinks(String link) {
      boolean wasHandled = false;
      Matcher oldMatcher = LEGACY_MATCHER;
      oldMatcher.reset(link);
      if (oldMatcher.find()) {
         wasHandled = true;
         String guid = oldMatcher.group(2);
         if (oldMatcher.groupCount() > 2) {
            String processType = oldMatcher.group(3);
            if (processType.equals("ats")) {
               parameterMap.put("guid", guid);
            } else if (processType.equals("Define") || processType.equals("")) {
               parameterMap.put("guid", guid);
            } else if (GUID.isValid(guid)) {
               parameterMap.put("guid", guid);
            }
         }
      }
      return wasHandled;
   }
}
