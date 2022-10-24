/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.api;

import java.util.List;
import java.util.Objects;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Data structure used to request the generation of the Word ML for a MS Word preview of an {@link Artifact} object or
 * objects.
 *
 * @author Loren K. Ashley
 */

public class MsWordPreviewRequestData implements ToMessage {

   /**
    * The {@link ArtifactId}s of the {@link Artifact}s to generate a preview for.
    */

   private List<ArtifactId> artifactIds;

   /**
    * The {@link BranchId} of the {@link Branch} containing the {@link Artifact}s to be previewed.
    */

   private BranchId branchId;

   /**
    * The request data for the Publishing Template.
    */

   private PublishingTemplateRequest publishingTemplateRequest;

   /**
    * Creates a new empty {@link MsWordPreviewRequestData} for JSON deserialization.
    */

   public MsWordPreviewRequestData() {
      this.publishingTemplateRequest = null;
      this.branchId = null;
      this.artifactIds = null;
   }

   /**
    * Creates a new {@link MsWordPreviewRequestData} with data for serialization (client) or for making a Publishing
    * Operations service call (server).
    *
    * @param publishingTemplateRequest the request data for the Publishing Template.
    * @param branchId the identifier of the branch containing the artifacts to be previewed.
    * @param artifactIds a list of the identifiers of the artifacts to be previewed.
    * @throws NullPointerException when any of the parameters <code>publishingTemplateRequest</code>,
    * <code>branchId</code>, or <code>artifactId</code> is <code>null</code>.
    */

   public MsWordPreviewRequestData(PublishingTemplateRequest publishingTemplateRequest, BranchId branchId, List<ArtifactId> artifactIds) {

      this.publishingTemplateRequest = Objects.requireNonNull(publishingTemplateRequest,
         "MsWordPreviewTemplateRequest::new, parameter \"publishingTemplateRequest\" cannot be null.");
      this.branchId =
         Objects.requireNonNull(branchId, "MsWordPreviewTemplateRequest::new, parameter \"branchId\" cannot be null.");
      this.artifactIds =
         Objects.requireNonNull(artifactIds, "MsWordPreviewTemplate::new, parameter \"artifactIds\" cannot be null.");
   }

   /**
    * Gets a list of the {@link ArtifactId}s of the artifacts to be previewed.
    *
    * @return a {@link List} of {@link ArtifactId}s.
    * @throws IllegalStateException when an attempt is made to get the {@link #artifactIds} for an
    * {@link MsWordPreviewRequestData} when the member has not been set.
    */

   public List<ArtifactId> getArtifactIds() {
      if (Objects.isNull(this.artifactIds)) {
         throw new IllegalStateException();
      }
      return this.artifactIds;
   }

   /**
    * Gets the {@link BranchId} of the branch containing the artifacts to be previewed.
    *
    * @return a {@link BranchId}.
    * @throws IllegalStateException when an attempt is made to get the {@link #branchId} for an
    * {@link MsWordPreviewRequestData} when the member has not been set.
    */

   public BranchId getBranchId() {
      if (Objects.isNull(this.branchId)) {
         throw new IllegalStateException();
      }
      return this.branchId;
   }

   /**
    * Gets the {@link PublishingTemplateRequest} data for the publishing template to be used for the preview.
    *
    * @return the {@link PublishingTemplateRequest} data.
    * @throws IllegalStateException when an attempt is made to get the {@link #publishingTemplateRequest} for an
    * {@link MsWordPreviewRequestData} when the member has not been set.
    */

   public PublishingTemplateRequest getPublishingTemplateRequest() {
      if (Objects.isNull(this.publishingTemplateRequest)) {
         throw new IllegalStateException();
      }
      return this.publishingTemplateRequest;
   }

   /**
    * Predicate to test the validity of the {@link MsWordTemplateRequestData} object.
    *
    * @return <code>true</code> when all members are non-<code>null</code>, {@link #publishingTemplateRequest} is valid
    * according to {@link PublishingTemplateRequest#isValid}, and {@link #artifactIds} is not empty; otherwise,
    * <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.publishingTemplateRequest ) && this.publishingTemplateRequest.isValid()
         && Objects.nonNull( this.branchId )
         && Objects.nonNull( this.artifactIds ) && !this.artifactIds.isEmpty();
      //@formatter:on
   }

   /**
    * Sets the list of {@link ArtifactId}s of the artifacts to be previewed. Used for deserialization.
    *
    * @param artifactIds a {@link List} of {@link ArtifactId} objects.
    * @throws NullPointerException when the parameter <code>artifactIds</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link #artifactIds} for a
    * {@link MSWordPreviewRequestData} that has already been set.
    */

   public void setArtifactIds(List<ArtifactId> artifactIds) {
      if (Objects.nonNull(this.artifactIds)) {
         throw new IllegalStateException();
      }
      this.artifactIds =
         Objects.requireNonNull(artifactIds, "MsWordPreviewTemplate::new, parameter \"artifactIds\" cannot be null.");
   }

   /**
    * Sets the {@link BranchId} for the artifacts to be previewed. Used for deserialization.
    *
    * @param branchId the {@link BranchId} of the branch the artifacts to be previewed are on.
    * @throws NullPointerException when the parameter <code>branchId</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link #branchId} for a
    * {@link MSWordPreviewRequestData} that has already been set.
    */

   public void setBranchId(BranchId branchId) {
      if (Objects.nonNull(this.branchId)) {
         throw new IllegalStateException();
      }
      this.branchId =
         Objects.requireNonNull(branchId, "MsWordPreviewTemplateRequest::new, parameter \"branchId\" cannot be null.");
   }

   /**
    * Sets the {@link PublishingTemplateRequest} data for the preview request. Used for deserialization.
    *
    * @param publishingTemplateRequest the {@link PublishingTemplateRequest} data for the preview.
    * @throws NullPointerException when the parameter <code>publishingTemplateRequest</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link #publishingTemplateRequest} for a
    * {@link MSWordPreviewRequestData} that has already been set.
    */

   public void setPublishingTemplateRequest(PublishingTemplateRequest publishingTemplateRequest) {
      if (Objects.nonNull(this.publishingTemplateRequest)) {
         throw new IllegalStateException();
      }

      this.publishingTemplateRequest = Objects.requireNonNull(publishingTemplateRequest,
         "MsWordPreviewTemplateRequest::new, parameter \"publishingTemplateRequest\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( this.getClass().getSimpleName() )
         .indentInc()
         .segment( "Artifacts", this.artifactIds )
         .segment( "Branch",    this.branchId    )
         .toMessage( this.publishingTemplateRequest )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */
