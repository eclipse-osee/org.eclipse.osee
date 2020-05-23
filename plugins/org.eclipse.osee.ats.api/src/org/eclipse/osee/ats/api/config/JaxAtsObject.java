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

package org.eclipse.osee.ats.api.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsObject extends NamedIdBase {

   /**
    * Do not remove this guid until IDE client no longer needs it for creating config objects. This class is used to
    * serialize all the ATS config objects so the clients don't have to load them.
    */
   protected String guid;
   protected boolean active;
   private String description;
   @JsonIgnore
   protected AtsApi atsApi;
   @JsonIgnore
   private ArtifactToken artifact;
   private List<WorkType> workTypes = new ArrayList<>();
   private List<String> tags = new ArrayList<>();

   public JaxAtsObject() {
      this(Id.SENTINEL, "");
   }

   public JaxAtsObject(Long id, String name) {
      super(id, name);
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public boolean matches(JaxAtsObject... identities) {
      for (JaxAtsObject identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public AtsApi getAtsApi() {
      return atsApi;
   }

   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @JsonIgnore
   public ArtifactToken getStoreObject() {
      if (artifact == null && atsApi != null) {
         artifact = atsApi.getQueryService().getArtifact(getId());
      }
      return artifact;
   }

   public void setStoreObject(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   public Collection<WorkType> getWorkTypes() {
      return workTypes;
   }

   public void setWorkTypes(List<WorkType> workTypes) {
      this.workTypes = workTypes;
   }

   public boolean isWorkType(WorkType workType) {
      return getWorkTypes().contains(workType);
   }

   public Collection<String> getTags() {
      return tags;
   }

   public void setTags(List<String> tags) {
      this.tags = tags;
   }

   public boolean hasTag(String tag) {
      return getTags().contains(tag);
   }

}
