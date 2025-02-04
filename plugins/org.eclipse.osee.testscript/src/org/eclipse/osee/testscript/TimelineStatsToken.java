/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;
import org.eclipse.osee.orcs.rest.model.transaction.ModifyArtifact;
import org.eclipse.osee.orcs.rest.model.transaction.SetAttribute;

public class TimelineStatsToken {

   public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

   private final ArtifactId id;
   private Date updatedAt;
   private ArtifactId setId;
   private String team;
   private List<TimelineDayToken> days;

   private ArtifactReadable art;

   public TimelineStatsToken(ArtifactReadable art) {
      this.art = art;
      if (art.isInvalid()) {
         this.id = ArtifactId.SENTINEL;
         this.updatedAt = Date.from(Instant.EPOCH);
         this.setId = ArtifactId.SENTINEL;
         this.team = "";
         this.days = new ArrayList<>();
         return;
      }
      this.id = art.getArtifactId();
      this.updatedAt = art.getSoleAttributeValue(CoreAttributeTypes.UpdatedAt, new Date());
      this.setId = ArtifactId.valueOf(art.getSoleAttributeValue(CoreAttributeTypes.SetId, "-1"));
      this.days = new ArrayList<>();
      ObjectMapper mapper = new ObjectMapper();
      String json = art.getSoleAttributeValue(CoreAttributeTypes.TimelineData, "[]");
      try {
         this.days =
            mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, TimelineDayToken.class));
      } catch (JsonProcessingException ex) {
         System.out.println(ex);
      }
   }

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return art;
   }

   public void setArtifactReadable(ArtifactReadable art) {
      this.art = art;
   }

   public ArtifactId getId() {
      return id;
   }

   public void setUpdatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
   }

   public Date getUpdatedAt() {
      return this.updatedAt;
   }

   public ArtifactId getSetId() {
      return setId;
   }

   public void setSetId(ArtifactId setId) {
      this.setId = setId;
   }

   public String getTeam() {
      return team;
   }

   public void setTeam(String team) {
      this.team = team;
   }

   public void setDays(List<TimelineDayToken> days) {
      this.days = days;
   }

   public List<TimelineDayToken> getDays() {
      return this.days;
   }

   private String getDaysJson() {
      ObjectMapper mapper = new ObjectMapper();
      try {
         return mapper.writeValueAsString(this.getDays());
      } catch (JsonProcessingException ex) {
         return "[]";
      }
   }

   private Map<AttributeTypeToken, String> getTxValues() {
      // @formatter:off
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.UpdatedAt, CoreAttributeTypes.UpdatedAt.storageStringFromValue(this.getUpdatedAt()));
      values.put(CoreAttributeTypes.SetId, this.getSetId().getIdString());
      values.put(CoreAttributeTypes.TimelineData, this.getDaysJson());
      // @formatter:on
      return values;
   }

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      Map<AttributeTypeToken, String> values = getTxValues();
      CreateArtifact art = new CreateArtifact();
      art.setName("Timeline Stats - " + getSetId().getIdString());
      art.setTypeId(CoreArtifactTypes.ScriptTimeline.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.ScriptTimeline.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);
      art.setApplicabilityId(applicId.getIdString());
      art.setkey(key);
      return art;
   }

   public ModifyArtifact modifyArtifact() {
      Map<AttributeTypeToken, String> values = getTxValues();
      ModifyArtifact art = new ModifyArtifact();
      art.setId(this.getArtifactReadable().getArtifactId().getIdString());

      List<SetAttribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.ScriptTimeline.getValidAttributeTypes()) {
         String value = values.get(type);
         SetAttribute attr = new SetAttribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setSetAttributes(attrs);
      return art;
   }

}
