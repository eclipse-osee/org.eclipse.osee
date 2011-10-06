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
package org.eclipse.osee.display.view.web.internal.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.vaadin.widgets.Navigator;

/*
 * Might need to utilize thread-safe techniques like this: https://vaadin.com/book/-/page/advanced.global.html
 */
/**
 * @author Shawn F. Cook
 */
public class OseeRoadMapAndNavigation {

   public final static String ARTIFACT = "artifact";
   public final static String PROGRAM = "program";
   public final static String BUILD = "build";
   public final static String NAMEONLY = "nameonly";
   public final static String SEARCHPHRASE = "searchphrase";

   private final static OseeRoadMapAndNavigation singleton = new OseeRoadMapAndNavigation();
   private final static Navigator navigator = new Navigator();

   //   public static void navigateToSearchResults(SearchCriteria searchCriteria) {
   //      Map<String, String> parameters = new HashMap<String, String>();
   //      parameters.put(PROGRAM, searchCriteria.getProgram().getProgramId());
   //      parameters.put(BUILD, searchCriteria.getBuild().getBuildId());
   //      parameters.put(NAMEONLY, searchCriteria.isNameOnly() ? "true" : "false");
   //      parameters.put(SEARCHPHRASE, searchCriteria.getSearchPhrase());
   //      String requestString = parameterMapToRequestString(parameters);
   //      navigator.navigateTo(String.format("%s%s", navigator.getUri(OseeSearchResultsView.class), requestString));
   //   }

   public static OseeRoadMapAndNavigation getInstance() {
      return singleton;
   }

   public static Navigator getNavigator() {
      return navigator;
   }

   public static Map<String, String> requestStringToParameterMap(String requestedDataId) {
      Map<String, String> parameters = new HashMap<String, String>();

      //TODO: Need better error detection for malformed parameter strings here.

      if (requestedDataId != null) {
         String[] tokens = requestedDataId.split("/");
         if (tokens.length > 1) {
            String parameterString = tokens[1];
            String[] paramArray = parameterString.split("&");
            for (int i = 0; i < paramArray.length; i++) {
               String param = paramArray[i];
               String[] keyValuePair = param.split("=");
               if (keyValuePair.length == 2) {
                  parameters.put(keyValuePair[0], keyValuePair[1]);
               }
            }
         }
      }

      return parameters;
   }

   /*
    * Returns: parameter string starting with '/' and ending without a delim character. example: "/program/blk3_guid"
    */
   public static String parameterMapToRequestString(Map<String, String> parameters) {
      String requestedDataId = "/";

      Set<Entry<String, String>> keyValuePairs = parameters.entrySet();
      for (Iterator<Entry<String, String>> iter = keyValuePairs.iterator(); iter.hasNext();) {
         Entry<String, String> pair = iter.next();
         if (pair.getKey() != null && !pair.getKey().isEmpty() && pair.getValue() != null && !pair.getValue().isEmpty()) {
            requestedDataId = String.format("%s%s/%s", requestedDataId, pair.getKey(), pair.getValue());
            if (iter.hasNext()) {
               requestedDataId = String.format("%s/", requestedDataId);
            }
         }
      }

      return requestedDataId;
   }
}
