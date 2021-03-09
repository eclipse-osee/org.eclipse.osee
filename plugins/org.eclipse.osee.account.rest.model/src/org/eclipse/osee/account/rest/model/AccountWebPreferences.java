/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.account.rest.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Angel Avila
 */
public class AccountWebPreferences {

   Map<String, Link> linksMap = new HashMap<>();

   public AccountWebPreferences() {

   }

   public AccountWebPreferences(Map<String, String> teamToPreferences) {
      for (String team : teamToPreferences.keySet()) {
         initPreferences(teamToPreferences.get(team), team);
      }
   }

   private void initPreferences(String string, String team) {
      try {
         ObjectMapper OM = new ObjectMapper();

         JsonNode jObject = OM.readTree(string);
         JsonNode jsonNode = jObject.get("links");
         if (jsonNode != null) {
            JsonNode linkJsonObject = OM.readTree(jsonNode.toString());
            Iterator<String> keys = linkJsonObject.fieldNames();

            while (keys.hasNext()) {

               String next = keys.next();
               JsonNode linkJObject = OM.readTree(linkJsonObject.get(next).toString());
               Link link = new Link();
               if (linkJObject.has("name")) {
                  link.setName(linkJObject.get("name").asText());

               }

               if (linkJObject.has("url")) {
                  link.setUrl(linkJObject.get("url").asText());
               }
               if (linkJObject.has("tags")) {
                  List<String> array = linkJObject.findValuesAsText("tags");
                  for (int x = 0; x < array.size(); x++) {
                     link.getTags().add(array.get(x));
                  }
               }
               link.setTeam(team);
               link.setId(linkJObject.get("id").asText());
               linksMap.put(next, link);
            }
         }

      } catch (Exception ex) {
         System.out.println("exc: " + ex);
      }
   }

   public AccountWebPreferences(String jsonString, String team) {
      initPreferences(jsonString, team);
   }

   public Map<String, Link> getLinks() {
      return linksMap;
   }

   public void setLinks(Map<String, Link> links) {
      this.linksMap = links;
   }

}
