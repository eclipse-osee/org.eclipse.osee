/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxEnumAttribute {

   private String name;
   private String uuid;
   private String dataProvider;
   private int min;
   private int max;
   private String taggerId;
   private String enumTypeName;
   private String enumTypeUuid;
   private String mediaType;
   private String defaultValue;
   private String description;

   private final List<JaxEnumEntry> entries = new ArrayList<>();

   public JaxEnumAttribute() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @JsonIgnore
   public Long getUuidLong() {
      return Long.valueOf(uuid);
   }

   public String getDataProvider() {
      return dataProvider;
   }

   public void setDataProvider(String dataProvider) {
      this.dataProvider = dataProvider;
   }

   public int getMin() {
      return min;
   }

   public void setMin(int min) {
      this.min = min;
   }

   public int getMax() {
      return max;
   }

   public void setMax(int max) {
      this.max = max;
   }

   public String getTaggerId() {
      return taggerId;
   }

   public void setTaggerId(String taggerId) {
      this.taggerId = taggerId;
   }

   public String getEnumTypeName() {
      return enumTypeName;
   }

   public void setEnumTypeName(String enumTypeName) {
      this.enumTypeName = enumTypeName;
   }

   @JsonIgnore
   public Long getEnumTypeUuidLong() {
      return Long.valueOf(enumTypeUuid);
   }

   public String getMediaType() {
      return mediaType;
   }

   public void setMediaType(String mediaType) {
      this.mediaType = mediaType;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<JaxEnumEntry> getEntries() {
      return entries;
   }

   public String getUuid() {
      return uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public String getEnumTypeUuid() {
      return enumTypeUuid;
   }

   public void setEnumTypeUuid(String enumTypeUuid) {
      this.enumTypeUuid = enumTypeUuid;
   }

}
