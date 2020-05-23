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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
         JSONObject jObject = new JSONObject(string);
         JSONObject linkJsonObject = jObject.getJSONObject("links");
         @SuppressWarnings("unchecked")
         Iterator<String> keys = linkJsonObject.keys();
         while (keys.hasNext()) {
            String next = keys.next();
            JSONObject linkJObject = linkJsonObject.getJSONObject(next);
            Link link = new Link();
            if (linkJObject.has("name")) {
               link.setName(linkJObject.getString("name"));
            }
            if (linkJObject.has("url")) {
               link.setUrl(linkJObject.getString("url"));
            }
            if (linkJObject.has("tags")) {
               JSONArray array = linkJObject.getJSONArray("tags");
               for (int x = 0; x < array.length(); x++) {
                  link.getTags().add(array.getString(x));
               }
            }
            link.setTeam(team);
            link.setId(linkJObject.getString("id"));
            linksMap.put(next, link);
         }

      } catch (JSONException ex) {
         //
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
