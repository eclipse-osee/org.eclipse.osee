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
package org.eclipse.osee.orcs.utility;

import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class RestUtil {

   public static String jsonToPretty(String json) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      JsonParser jp = new JsonParser();
      JsonElement je = jp.parse(json);
      String pretty = gson.toJson(je);
      return pretty;
   }

   public static JSONObject toJson(String json) throws Exception {
      return new JSONObject(json);
   }

   public static String jsonToPretty(JSONObject json, boolean pretty) {
      return pretty ? jsonToPretty(json.toString()) : json.toString();
   }

   public static String jsonToPretty(JSONArray json, boolean pretty) {
      return pretty ? jsonToPretty(json.toString()) : json.toString();
   }

   public static JSONArray getDefaultJSonArray(ResultSet<ArtifactReadable> artifacts) throws JSONException {
      JSONArray jsonArray = new JSONArray();
      for (ArtifactReadable art : artifacts) {
         JSONObject jObj = new JSONObject();
         jObj.put("name", art.getName());
         jObj.put("uuid", art.getLocalId());
         jsonArray.put(jObj);
      }
      return jsonArray;
   }

   public static JSONObject getDefaultJSon(ArtifactReadable art) throws JSONException {
      JSONObject jObj = new JSONObject();
      jObj.put("name", art.getName());
      jObj.put("uuid", art.getLocalId());
      return jObj;
   }

}
