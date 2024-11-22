/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.util;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class RecentlyVisistedItem {

   String workflowName;
   @JsonSerialize(using = ToStringSerializer.class)
   Long workflowId;
   @JsonSerialize(using = ToStringSerializer.class)
   Long artifactTypeId;

   public RecentlyVisistedItem() {
      // for jax-rs
   }

   private RecentlyVisistedItem(String workflowName, Long workflowId, Long artifactTypeId) {
      this.workflowName = workflowName;
      this.workflowId = workflowId;
      this.artifactTypeId = artifactTypeId;
   }

   public static RecentlyVisistedItem valueOf(ArtifactToken artTok, ArtifactTypeToken typeToken) {
      Conditions.assertNotSentinel(artTok, "Invalid Id");
      Conditions.assertNotNull(artTok.getName(), "Invalid Id");
      Conditions.assertNotSentinel(typeToken, "Invalid Type Id");
      RecentlyVisistedItem item = new RecentlyVisistedItem(artTok.getName(), artTok.getId(), typeToken.getId());
      return item;
   }

   public String getWorkflowName() {
      return workflowName;
   }

   public void setWorkflowName(String workflowName) {
      this.workflowName = workflowName;
   }

   public Long getWorkflowId() {
      return workflowId;
   }

   public void setWorkflowId(Long workflowId) {
      this.workflowId = workflowId;
   }

   public Long getArtifactTypeId() {
      return artifactTypeId;
   }

   public void setArtifactTypeId(Long artifactTypeId) {
      this.artifactTypeId = artifactTypeId;
   }

   @Override
   public String toString() {
      return "RecentlyVisistedItem [workflowName=" + workflowName + ", workflowId=" + workflowId + ", artifactTypeId=" + artifactTypeId + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(workflowId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      RecentlyVisistedItem other = (RecentlyVisistedItem) obj;
      return Objects.equals(workflowId, other.workflowId);
   }

}