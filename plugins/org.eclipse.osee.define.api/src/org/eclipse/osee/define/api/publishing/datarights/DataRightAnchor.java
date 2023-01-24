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

import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Class to encapsulate the data right and sequence flags for an artifact.
 *
 * @author Angel Avila
 * @author Loren K. Ashley
 */

public class DataRightAnchor implements ToMessage {

   /**
    * Saves the identifier of the artifact the data right and flags are associated with.
    */

   private ArtifactId artifactId;

   /**
    * Saves the {@link DataRight} for the artifact.
    */

   private DataRight dataRight;

   /**
    * Flag indicates the artifact is the last artifact in a sub-sequence of artifacts with the same footer.
    */

   private Boolean isContinuous = false;

   /**
    * Flag indicates the artifact is the first artifact in a sub-sequence of artifacts with the same data rights.
    */

   private Boolean newFooter;

   /**
    * Creates a new empty {@link DataRightAnchor} for JSON deserialization.
    */

   public DataRightAnchor() {
      this.artifactId = null;
      this.dataRight = null;
      this.newFooter = null;
      this.isContinuous = null;
   }

   /**
    * Creates a new {@link DataRightAnchor} with data for JSON serialization. The member {@link #isContinuous} is left
    * <code>null</code> and must be set with {@link #setIsContinuous} before serialization.
    *
    * @param artifactId the identifier of the associated artifact.
    * @param dataRight the data rights for the artifact.
    * @param isSetDataRightFooter sequence flag.
    * @throws NullPointerException when any of the parameters <code>artifactId</code>, <code>dataRight</code>, or
    * <code>isSetDataRightFooter</code> are <code>null</code>.
    */

   public DataRightAnchor(ArtifactId artifactId, DataRight dataRight, Boolean isSetDataRightFooter) {
      this.artifactId =
         Objects.requireNonNull(artifactId, "DataRightAnchor::new, parameter \"artifactId\" cannot be null.");
      this.dataRight =
         Objects.requireNonNull(dataRight, "DataRightAnchor::new, parameter \"dataRight\" cannot be null.");
      this.newFooter =
         Objects.requireNonNull(isSetDataRightFooter, "DataRightAnchor::new, parameter \"newFooter\" cannot be null.");
      this.isContinuous = null;
   }

   /**
    * This constructor is used to recreate a {@link DataRightAnchor} from an {@link DataRightAnchorSkinny} after JSON
    * deserialization. The {@link DataRightAnchorSkinny} does not contain the footer Word ML. Only the data right
    * classification and not the {@link DataRight} object with the footer Word ML is saved in the
    * {@link DataRightAnchorSkinny} object. The footer Word ML for the {@link DataRight} object in the recreated
    * {@link DataRightAnchor} is obtained from the map parameter <code>dataRights</code> which is a map of footer Word
    * ML by data right classification name.
    *
    * @param dataRightAnchorSkinny data to recreate the {@link DataRightAnchor}.
    * @param dataRights map of footer Word ML by data right classification.
    * @throws NullPointerException when:
    * <ul>
    * <li>any of the parameters <code>dataRightAnchorSkinny</code> or <code>dataRights</code> is <code>null</code>,
    * or</li>
    * <li>the {@link DataRight} object obtained from the <code>dataRights</code> map is <code>null</code>.</li>
    * </ul>
    * @throws IllegalArgumentException when:
    * <ul>
    * <li>the parameter <code>dataRightAnchorSkinny</code> fails its {@link DataRightAnchorSkinny#isValid} check,</li>
    * <li>the {@link DataRight} object obtained from the <code>dataRights</code> map fails its {@link DataRight#isValid}
    * check.</li>
    * </ul>
    */

   public DataRightAnchor(DataRightAnchorSkinny dataRightAnchorSkinny, Map<String, DataRight> dataRights) {

      Objects.requireNonNull(dataRightAnchorSkinny,
         "DataRightAnchor::new, parameter \"dataRightAnchorSkinny\" cannot be null.");

      if (!dataRightAnchorSkinny.isValid()) {
         throw new IllegalArgumentException("DataRightAnchor::new, parameter \"dataRightAnchorSkinny\" is not valid.");
      }

      Objects.requireNonNull(dataRights, "DataRightAnchor::new, parameter \"dataRights\" cannot be null.");

      this.artifactId = dataRightAnchorSkinny.getArtifactId();
      this.newFooter = dataRightAnchorSkinny.getNewFooter();
      this.isContinuous = dataRightAnchorSkinny.getIsContinuous();
      this.dataRight = dataRights.get(dataRightAnchorSkinny.getClassification());

      Objects.requireNonNull(this.dataRight,
         "DataRightAnchor::new, the \"DataRight\" obtained from the parameter \"dataRights\" cannot be null.");

      if (!this.dataRight.isValid()) {
         throw new IllegalArgumentException(
            "DataRightAnchor::new, the parameter \"dataRights\" contains an invalid \"DataRight\" object.");
      }
   }

   /**
    * Gets the {@link ArtifactId} of the artifact the data rights and sequence flags are associated with.
    *
    * @return the identifier of the associated artifact.
    * @throws IllegalStateException when an attempt is made to get the {@link #artifactId} member for an
    * {@link DataRightAnchor} that has not been set.
    */

   public ArtifactId getArtifactId() {
      if (Objects.isNull(this.artifactId)) {
         throw new IllegalStateException("DataRightAnchor::getArtifactId, the member \"artifactId\" has not been set.");
      }
      return this.artifactId;
   }

   /**
    * Gets the {@link DataRight} for the associated artifact.
    *
    * @return the data right for the associated artifact.
    * @throws IllegalStateException when an attempt is made to get the {@link #dataRight} member for an
    * {@link DataRightAnchor} that has not been set.
    */

   public DataRight getDataRight() {
      if (Objects.isNull(this.dataRight)) {
         throw new IllegalStateException("DataRightAnchor::getDataRight, the member \"dataRight\" has not been set.");
      }
      return this.dataRight;
   }

   /**
    * Gets the sequence flag {@link #isContinuous} for the associated artifact.
    *
    * @return the {@link #isContinuous} sequence flag.
    * @throws IllegalStateException when an attempt is made to get the {@link #isContinuous} member for a
    * {@link DataRightAnchor} that has not been set.
    */

   public Boolean getIsContinuous() {
      if (Objects.isNull(this.isContinuous)) {
         throw new IllegalStateException(
            "DataRightAnchor::getIsContinuous, the member \"isContinuous\" has not been set.");
      }
      return isContinuous;
   }

   /**
    * Gets the sequence flag {@link #newFooter} for the associated artifact.
    *
    * @return the {@link #newFooter} sequence flag.
    * @throws IllegalStateException when an attempt is made to get the {@link #newFooter} member for a
    * {@link DataRightAnchor} that has not been set.
    */

   public Boolean getNewFooter() {
      if (Objects.isNull(this.newFooter)) {
         throw new IllegalStateException("DataRightAnchor::getNewFooter, the member \"newFooter\" has not been set.");
      }
      return this.newFooter;
   }

   /**
    * Predicate to test the validity of the {@link DataRightAnchor} object.
    *
    * @return <code>true</code>, when all members are non-<code>null</code> and the member {@link #dataRight} is also
    * valid; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.artifactId )
         && Objects.nonNull( this.dataRight ) && this.dataRight.isValid()
         && Objects.nonNull( this.newFooter )
         && Objects.nonNull( this.isContinuous );
      //@formatter:on
   }

   /**
    * Sets the {@link ArtifactId} of the artifact the data rights and sequence flags are associated with.
    *
    * @param artifactId the associated artifact's identifier.
    * @throws IllegalStateException when an attempt is made to set the {@link #artifactId} for an
    * {@link DataRightAnchor} that has already been set.
    * @throws NullPointerException when the parameter <code>artifactId</code> is <code>null</code>.
    */

   public void setArtifactId(ArtifactId artifactId) {
      if (Objects.nonNull(this.artifactId)) {
         throw new IllegalStateException(
            "DataRightAnchor::setArtifactId, the member \"artifactId\" has already been set.");
      }
      this.artifactId =
         Objects.requireNonNull(artifactId, "DataRightAnchor::setArtifactId, parameter \"artifactId\" cannot be null.");
   }

   /**
    * Sets the {@link DataRight} associated with the artifact.
    *
    * @param dataRight the {@link DataRight}.
    * @throws IllegalStateException when an attempt is made to set the {@link #dataRight} for an {@link DataRightAnchor}
    * that has already been set.
    * @throws NullPointerException when the parameter <code>dataRight</code> is <code>null</code>.
    */

   public void setDataRight(DataRight dataRight) {
      if (Objects.nonNull(this.dataRight)) {
         throw new IllegalStateException(
            "DataRightAnchor::setDataRight, the member \"dataRight\" has already been set.");
      }
      this.dataRight =
         Objects.requireNonNull(dataRight, "DataRightAnchor::setDataRight, parameter \"dataRight\" cannot be null.");
   }

   /**
    * Sets the sequence flag {@link #isContinuous}.
    *
    * @param isContinuous the {@link #isContinuous} sequence flag.
    * @throws IllegalStateException when an attempt is made to set the {@link #isContinuous} flag for a
    * {@link DataRightAnchor} that has already been set.
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
    * {@link DataRightAnchor} that has already been set.
    * @throws NullPointerException when the parameter <code>newFooter</code> is <code>null</code>.
    */

   public void setNewFooter(Boolean newFooter) {
      if (Objects.nonNull(this.newFooter)) {
         throw new IllegalStateException(
            "DataRightAnchor::setIsSetDataRightFooter, the member \"newFooter\" has already been set.");
      }
      this.newFooter = Objects.requireNonNull(newFooter,
         "DataRightAnchor::setIsSetDataRightFooter, parameter \"newFooter\" cannot be null.");
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
         .segment( "artifactId",   this.artifactId   )
         .segment( "newFooter",    this.newFooter    )
         .segment( "isContinuous", this.isContinuous )
         .toMessage( this.dataRight )
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
