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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

   public static JSONArray getDefaultJSonArray(Collection<ArtifactReadable> artifacts) throws JSONException {
      return getDefaultJSonArrayIterator(artifacts);
   }

   public static JSONArray getDefaultJSonArrayIterator(Iterable<ArtifactReadable> artifacts) throws JSONException {
      JSONArray jsonArray = new JSONArray();
      for (ArtifactReadable art : artifacts) {
         JSONObject jObj = new JSONObject();
         jObj.put("name", art.getName());
         jObj.put("uuid", art.getUuid());
         jsonArray.put(jObj);
      }
      return jsonArray;
   }

   public static JSONObject getDefaultJSon(ArtifactReadable art) throws JSONException {
      JSONObject jObj = new JSONObject();
      jObj.put("name", art.getName());
      jObj.put("uuid", art.getUuid());
      return jObj;
   }

   public static JSONObject getJsonObject(OrcsApi orcsApi, ArtifactReadable artifact) throws Exception, JSONException {
      JSONObject jsonObj = getDefaultJSon(artifact);
      addAttributes(orcsApi, jsonObj, artifact);
      return jsonObj;
   }

   public static JSONArray getJsonArray(OrcsApi orcsApi, ResultSet<ArtifactReadable> artifacts) throws Exception, JSONException {
      JSONArray jsonArray = new JSONArray();
      for (ArtifactReadable artifact : artifacts) {
         JSONObject jsonObj = getJsonObject(orcsApi, artifact);
         jsonArray.put(jsonObj);
      }
      return jsonArray;
   }

   public static JSONObject addAttributes(OrcsApi orcsApi, JSONObject jsonObj, ArtifactReadable art) throws Exception {
      addAttributesWithValues(orcsApi, jsonObj, art);
      return jsonObj;
   }

   private static JSONObject addAttributesWithValues(OrcsApi orcsApi, JSONObject jsonObj, ArtifactReadable artifact) throws Exception {
      addAttribute(orcsApi, jsonObj, artifact);
      return jsonObj;
   }

   private static void addAttribute(OrcsApi orcsApi, JSONObject jsonObj, ArtifactReadable art) throws Exception {
      for (AttributeTypeToken attrType : orcsApi.getOrcsTypes().getAttributeTypes().getAll()) {
         if (art.isAttributeTypeValid(attrType)) {
            ResultSet<? extends AttributeReadable<Object>> attributeVals = art.getAttributes();
            if (!attributeVals.isEmpty()) {
               if (art.isAttributeTypeValid(
                  attrType) && orcsApi.getOrcsTypes().getAttributeTypes().getMaxOccurrences(attrType) > 1) {
                  List<String> attributeValues = new ArrayList<>();
                  for (AttributeReadable<?> attrRead : attributeVals) {
                     String valueStr = String.valueOf(attrRead.getValue());
                     attributeValues.add(valueStr);
                  }
                  if (!attributeValues.isEmpty()) {
                     jsonObj.put(attrType.getName(), attributeValues);
                  }
               } else if (attributeVals.size() == 1) {
                  String valueStr = String.valueOf(attributeVals.iterator().next().getValue());
                  jsonObj.put(attrType.getName(), valueStr);
               }
            }
         }
      }
   }

   public static String getAccountId(HttpHeaders httpHeaders) {
      String clientId = httpHeaders.getHeaderString("osee.account.id");
      if (clientId == null) {
         clientId = "";
      }
      return clientId;
   }

   /**
    * @return User by artId which is also accountId
    */
   public static ArtifactReadable getUserByAccountId(String accountId, OrcsApi orcsApi) {
      ArtifactReadable userArt = null;
      if (Strings.isNumeric(accountId)) {
         userArt = getUserByAccountId(Long.valueOf(accountId), orcsApi);
      }
      return userArt;
   }

   public static ArtifactReadable getUserByAccountId(Long accountId, OrcsApi orcsApi) {
      ArtifactReadable user = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
         accountId.intValue()).getResults().getAtMostOneOrNull();
      return user;
   }

}
