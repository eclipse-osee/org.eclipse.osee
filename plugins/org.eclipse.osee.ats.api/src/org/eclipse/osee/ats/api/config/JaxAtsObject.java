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
package org.eclipse.osee.ats.api.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

}
