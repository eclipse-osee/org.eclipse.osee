/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.orcs.rest.model.transaction;

/**
 * @author David W. Miller
 * @author autogenerated by jsonschema2pojo
 */
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"typeName", "aArtId", "bArtId", "rationale", "typeId"})
public class AddRelation {

   @JsonProperty("typeName")
   private String typeName;
   @JsonProperty("aArtId")
   private String aArtId;
   @JsonProperty("bArtId")
   private String bArtId;
   @JsonProperty("rationale")
   private String rationale;
   @JsonProperty("typeId")
   private String typeId;
   @JsonProperty("id")
   private String id;

   @JsonIgnore
   private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

   @JsonProperty("typeName")
   public String getTypeName() {
      return typeName;
   }

   @JsonProperty("typeName")
   public void setTypeName(String typeName) {
      this.typeName = typeName;
   }

   @JsonProperty("aArtId")
   public String getaArtId() {
      return aArtId;
   }

   @JsonProperty("aArtId")
   public void setaArtId(String aArtId) {
      this.aArtId = aArtId;
   }

   @JsonProperty("bArtId")
   public String getbArtId() {
      return bArtId;
   }

   @JsonProperty("bArtId")
   public void setbArtId(String bArtId) {
      this.bArtId = bArtId;
   }

   @JsonProperty("rationale")
   public String getRationale() {
      return rationale;
   }

   @JsonProperty("rationale")
   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   @JsonProperty("typeId")
   public String getTypeId() {
      return typeId;
   }

   @JsonProperty("typeId")
   public void setTypeId(String typeId) {
      this.typeId = typeId;
   }

   @JsonAnyGetter
   public Map<String, Object> getAdditionalProperties() {
      return this.additionalProperties;
   }

   @JsonAnySetter
   public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
   }

   @JsonProperty("id")
   public String getId() {
      return id;
   }

   @JsonProperty("id")
   public void setId(String id) {
      this.id = id;
   }
}
