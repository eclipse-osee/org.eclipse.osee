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
package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * Payload for the Access Topic Event
 *
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AccessTopicEventPayload implements ISerializeableTopicPayload {

   @JsonSerialize(using = ToStringSerializer.class)
   long branchUuid;
   List<String> artifactUuids = new ArrayList<>();

   public long getBranchUuid() {
      return branchUuid;
   }

   public void setBranchUuid(long branchUuid) {
      this.branchUuid = branchUuid;
   }

   public List<String> getArtifactUuids() {
      return artifactUuids;
   }

   public void setArtifactUuids(List<String> artifactUuids) {
      this.artifactUuids = artifactUuids;
   }

   public void addArtifact(ArtifactId artifact) {
      addArtifact(artifact.getUuid());
   }

   public void addArtifact(Long artifactUuid) {
      getArtifactUuids().add(String.valueOf(artifactUuid));
   }

   public void addArtifact(Integer artifactId) {
      getArtifactUuids().add(String.valueOf(Long.valueOf(artifactId)));
   }

   /**
    * @return true if this artifact matches event branch and contains artifact
    */
   public boolean matches(ArtifactToken artifact) {
      if (!artifactUuids.isEmpty()) {
         return artifact.getBranchId().equals(branchUuid) && artifactUuids.contains(String.valueOf(artifact.getUuid()));
      }
      return false;
   }

}
