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
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "applicabilityId", "setAttributes", "addAttributes", "deleteAttributes"})
public class ModifyArtifact {

   @JsonProperty("id")
   private String id;
   @JsonProperty("applicabilityId")
   private String applicabilityId;
   @JsonProperty("setAttributes")
   private List<SetAttribute> setAttributes;
   @JsonProperty("addAttributes")
   private List<AddAttribute> addAttributes;
   @JsonProperty("deleteAttributes")
   private List<DeleteAttribute> deleteAttributes;
   @JsonIgnore
   private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

   @JsonProperty("id")
   public String getId() {
      return id;
   }

   @JsonProperty("id")
   public void setId(String id) {
      this.id = id;
   }

   @JsonProperty("applicabilityId")
   public String getApplicabilityId() {
      return applicabilityId;
   }

   @JsonProperty("applicabilityId")
   public void setApplicabilityId(String applicabilityId) {
      this.applicabilityId = applicabilityId;
   }

   @JsonProperty("setAttributes")
   public List<SetAttribute> getSetAttributes() {
      return setAttributes;
   }

   @JsonProperty("setAttributes")
   public void setSetAttributes(List<SetAttribute> setAttributes) {
      this.setAttributes = setAttributes;
   }

   @JsonProperty("addAttributes")
   public List<AddAttribute> getAddAttributes() {
      return addAttributes;
   }

   @JsonProperty("addAttributes")
   public void setAddAttributes(List<AddAttribute> addAttributes) {
      this.addAttributes = addAttributes;
   }

   @JsonProperty("deleteAttributes")
   public List<DeleteAttribute> getDeleteAttributes() {
      return deleteAttributes;
   }

   @JsonProperty("deleteAttributes")
   public void setDeleteAttributes(List<DeleteAttribute> deleteAttributes) {
      this.deleteAttributes = deleteAttributes;
   }

   @JsonAnyGetter
   public Map<String, Object> getAdditionalProperties() {
      return this.additionalProperties;
   }

   @JsonAnySetter
   public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
   }

}
