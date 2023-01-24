/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.define.api.publishing.datarights;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * A reduced version of the {@link DataRightAnchor} class for JSON serialization that references the artifact's
 * {@link DataRight} by name instead of containing the {@link DataRight} object and the associated footer Word ML. The
 * footer Word ML can be large and is also likely to be shared by many of the {@link DataRightAnchor} objects being
 * serialized for a publish.
 *
 * @author Loren K. Ashley
 */

public class DataRightAnchorSkinny implements ToMessage {

   /**
    * Saves the identifier of the artifact the data right and flags are associated with.
    */

   private ArtifactId artifactId;

   /**
    * Saves a reference by name to the {@link DataRight} for the artifact.
    */

   private String classification;

   /**
    * Flag indicates the artifact is the last artifact in a sub-sequence of artifacts with the same footer.
    */

   private Boolean isContinuous;

   /**
    * Flag indicates the artifact is the first artifact in a sub-sequence of artifacts with the same data rights.
    */

   private Boolean newFooter;

   /**
    * Creates a new empty {@link DataRightAnchor} for JSON deserialization.
    */

   public DataRightAnchorSkinny() {
      this.artifactId = null;
      this.classification = null;
      this.newFooter = null;
      this.isContinuous = null;
   }

   /**
    * Creates a new {@link DataRightAnchorSkinny} with data for JSON serialization.
    *
    * @param dataRightAnchor the {@link DataRightAnchor} to create the skinny version from.
    * @throws NullPointerException when the parameters <code>dataRightAnchor</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>dataRightAnchor</code> contains <code>null</code> or
    * invalid members.
    */

   public DataRightAnchorSkinny(DataRightAnchor dataRightAnchor) {

      Objects.requireNonNull(dataRightAnchor,
         "DataRightAnchorSkinny::new, parameter \"dataRightAnchor\" cannot be null.");

      if (!dataRightAnchor.isValid()) {
         throw new IllegalArgumentException("DataRightAnchorSkinny::new, parameter \"dataRightAnchor\" is invalid.");
      }

      this.artifactId = dataRightAnchor.getArtifactId();
      this.classification = dataRightAnchor.getDataRight().getClassification();
      this.newFooter = dataRightAnchor.getNewFooter();
      this.isContinuous = dataRightAnchor.getIsContinuous();
   }

   /**
    * Gets the {@link ArtifactId} of the artifact the data rights and sequence flags are associated with.
    *
    * @return the identifier of the associated artifact.
    * @throws IllegalStateException when an attempt is made to get the {@link #artifactId} member for an
    * {@link DataRightAnchorSkinny} that has not been set.
    */

   public ArtifactId getArtifactId() {
      if (Objects.isNull(this.artifactId)) {
         throw new IllegalStateException(
            "DataRightAnchorSkinny::getArtifactId, the member \"artifactId\" has not been set.");
      }
      return this.artifactId;
   }

   /**
    * Gets the data right classification name for the data right associated with the artifact.
    *
    * @return the data right classification name.
    * @throws IllegalStateException when an attempt is made to get the {@link #classification} member for an
    * {@link DataRightAnchorSkinny} that has not been set.
    */

   public String getClassification() {
      if (Objects.isNull(this.classification)) {
         throw new IllegalStateException(
            "DataRightAnchor::getClassification, the member \"classification\" has not been set.");
      }
      return this.classification;
   }

   /**
    * Gets the sequence flag {@link #isContinuous} for the associated artifact.
    *
    * @return the {@link #isContinuous} sequence flag.
    * @throws IllegalStateException when an attempt is made to get the {@link #isContinuous} member for a
    * {@link DataRightAnchorSkinny} that has not been set.
    */

   public Boolean getIsContinuous() {
      if (Objects.isNull(this.isContinuous)) {
         throw new IllegalStateException(
            "DataRightAnchorSkinny::getIsContinuous, the member \"isContinuous\" has not been set.");
      }
      return isContinuous;
   }

   /**
    * Gets the sequence flag {@link #newFooter} for the associated artifact.
    *
    * @return the {@link #newFooter} sequence flag.
    * @throws IllegalStateException when an attempt is made to get the {@link #newFooter} member for a
    * {@link DataRightAnchorSkinny} that has not been set.
    */

   public Boolean getNewFooter() {
      if (Objects.isNull(this.newFooter)) {
         throw new IllegalStateException(
            "DataRightAnchorSkinny::getNewFooter, the member \"newFooter\" has not been set.");
      }
      return this.newFooter;
   }

   /**
    * Predicate to test the validity of the {@link DataRightAnchorSkinny} object.
    *
    * @return <code>true</code>, when all members are non-<code>null</code>; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.artifactId )
         && Objects.nonNull( this.classification )
         && Objects.nonNull( this.newFooter )
         && Objects.nonNull( this.isContinuous );
      //@formatter:on
   }

   /**
    * Sets the {@link ArtifactId} of the artifact the data rights and sequence flags are associated with.
    *
    * @param artifactId the associated artifact's identifier.
    * @throws IllegalStateException when an attempt is made to set the {@link #artifactId} for an
    * {@link DataRightAnchorSkinny} that has already been set.
    * @throws NullPointerException when the parameter <code>artifactId</code> is <code>null</code>.
    */

   public void setArtifactId(ArtifactId artifactId) {
      if (Objects.nonNull(this.artifactId)) {
         throw new IllegalStateException(
            "DataRightAnchorSkinny::setArtifactId, the member \"artifactId\" has already been set.");
      }
      this.artifactId = Objects.requireNonNull(artifactId,
         "DataRightAnchorSkinny::setArtifactId, parameter \"artifactId\" cannot be null.");
   }

   /**
    * Sets the data right classification name for the data right associated with the artifact.
    *
    * @param classification the data right classification name.
    * @throws IllegalStateException when an attempt is made to set the {@link #classification} for an
    * {@link DataRightAnchor} that has already been set.
    * @throws NullPointerException when the parameter <code>classification</code> is <code>null</code>.
    */

   public void setClassification(String classification) {
      if (Objects.nonNull(this.classification)) {
         throw new IllegalStateException(
            "DataRightAnchorSkinny::setDataRight, the member \"classification\" has already been set.");
      }
      this.classification = Objects.requireNonNull(classification,
         "DataRightAnchorSkinny::setClassification, parameter \"classification\" cannot be null.");
   }

   /**
    * Sets the sequence flag {@link #isContinuous}.
    *
    * @param isContinuous the {@link #isContinuous} sequence flag.
    * @throws IllegalStateException when an attempt is made to set the {@link #isContinuous} flag for a
    * {@link DataRightAnchorSkinny} that has already been set.
    * @throws NullPointerException when the parameter <code>isContinuous</code> is <code>null</code>.
    */

   public void setIsContinuous(Boolean isContinuous) {
      if (Objects.nonNull(this.isContinuous)) {
         throw new IllegalStateException(
            "DataRightAnchor::setIsContinuous, the member \"isContinuous\" has already been set.");
      }
      this.isContinuous = Objects.requireNonNull(isContinuous,
         "DataRightAnchor::setIsContinuous, parameter \"isContinuous\" cannot be null.");
   }

   /**
    * Sets the sequence flag {@link #newFooter}.
    *
    * @param newFooter the {@link #newFooter} sequence flag.
    * @throws IllegalStateException when an attempt is made to set the {@link #newFooter} flag for a
    * {@link DataRightAnchorSkinny} that has already been set.
    * @throws NullPointerException when the parameter <code>newFooter</code> is <code>null</code>.
    */

   public void setNewFooter(Boolean newFooter) {
      if (Objects.nonNull(this.newFooter)) {
         throw new IllegalStateException(
            "DataRightAnchor::setNewFooter, the member \"newFooter\" has already been set.");
      }
      this.newFooter =
         Objects.requireNonNull(newFooter, "DataRightAnchor::setNewFooter, parameter \"newFooter\" cannot be null.");
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
         .title( "Data Right Anchor" )
         .indentInc()
         .segment( "artifactId",     this.artifactId     )
         .segment( "newFooter",      this.newFooter      )
         .segment( "isContinuous",   this.isContinuous   )
         .segment( "classification", this.classification )
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
