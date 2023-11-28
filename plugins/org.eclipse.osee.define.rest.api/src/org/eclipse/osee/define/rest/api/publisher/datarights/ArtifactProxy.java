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
package org.eclipse.osee.define.rest.api.publisher.datarights;

import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates the attributes of an OSEE artifact being published
 *
 * @author Md I. Khan
 */

public class ArtifactProxy implements ToMessage {

   /**
    * The OSEE Artifact Identifier.
    */

   private Long artifactId;

   /**
    * The unique integer identifier to look up the Required Indicator Set for the Artifact.
    */

   private Integer requiredId;

   /**
    * The unique integer identifier to look up the CUI Category Type Pair Set for the Artifact.
    */

   private Integer cuiCatergoryId;

   /**
    * The unique integer identifier to look up the CUI Limited Dissemination Control Indicator Set with optional
    * Trigraph Country Code Indicator Set Pair.
    */

   private Integer cuiDisseminationId;

   /**
    * Creates a new empty {@link ArtifactProxy} object for JSON deserialization
    */

   public ArtifactProxy() {
      this.artifactId = null;
      this.requiredId = null;
      this.cuiCatergoryId = null;
      this.cuiDisseminationId = null;
   }

   /**
    * Creates a new {@link ArtifactProxy} object with data for JSON serialization
    *
    * @param artifactId artifact identifier as an {@link Long}
    * @param requiredIndentifier required indicator as an {@link Integer}
    * @param cuiCatergoryId CUI category indicator as an {@link Integer}
    * @param cuiDisseminationId CUI limited dissemination indicator as an {@link Integer}
    * @throws NullPointerException when any of the parameters are null
    */

   public ArtifactProxy(Long artifactId, Integer requiredId, Integer cuiCatergoryId, Integer cuiDisseminationId) {
      this.artifactId =
         Objects.requireNonNull(artifactId, "ArtifactProxy::new, parameter \"artifactId\" cannot be null.");
      this.requiredId =
         Objects.requireNonNull(requiredId, "ArtifactProxy::new, parameter \"requiredId\" cannot be null.");
      this.cuiCatergoryId =
         Objects.requireNonNull(cuiCatergoryId, "ArtifactProxy::new, parameter \"cuiCategoryId\" cannot be null.");
      this.cuiDisseminationId = Objects.requireNonNull(cuiDisseminationId,
         "ArtifactProxy::new, parameter \"cuiDisseminationId\" cannot be null.");
   }

   /**
    * Gets the artifact identifier
    *
    * @return artifactId
    * @throws IllegalStateException when artifactId has not been set
    */

   public Long getArtifactId() {
      if (Objects.isNull(this.artifactId)) {
         throw new IllegalStateException("ArtifactProxy::getArtifactId, member \"artifactId\" has not been set.");
      }
      return this.artifactId;
   }

   /**
    * Gets the required indicator
    *
    * @return requiredId
    * @throws IllegalStateException when requiredId has not been set
    */

   public Integer getRequiredId() {
      if (Objects.isNull(this.requiredId)) {
         throw new IllegalStateException("ArtifactProxy::getRequiredId, member \"requiredId\" has not been set.");
      }
      return this.requiredId;
   }

   /**
    * Gets the CUI category indicator
    *
    * @return cuiCategoryId
    * @throws IllegalStateException when cuiCategory has not been set
    */

   public Integer getCuiCategoryId() {
      if (Objects.isNull(this.cuiCatergoryId)) {
         throw new IllegalStateException("ArtifactProxy::getCuiCategoryId, member \"cuiCategoryId\" has not been set.");
      }
      return this.cuiCatergoryId;
   }

   /**
    * Gets the CUI limited dissemination indicator
    *
    * @return cuiDisseminationId
    * @throws IllegalStateException when cuiDisseminationId has not been set
    */

   public Integer getCuiDisseminationId() {
      if (Objects.isNull(this.cuiDisseminationId)) {
         throw new IllegalStateException(
            "ArtifactProxy::getCuiDisseminationId, member \"cuiDisseminationId\" has not been set.");
      }
      return this.cuiDisseminationId;
   }

   /**
    * Predicate to test the validity of {@link ArtifactProxy} object
    *
    * @return <code>true</code>, when the member {@link #artifactId} is non-<code>null</code>; otherwise
    * <code>false</code>
    */

   public boolean isValid() {
      return Objects.nonNull(this.artifactId);
   }

   /**
    * Sets the artifact identifier
    *
    * @param artifactId artifact identifier as a {@link Long}
    * @throws IllegalStateException when the member {@link #artifactId} has already been set
    * @throws NullPointerException when parameter <code>artifactId</code> is <code>null</code>
    */

   public void setArtifactId(Long artifactId) {
      if (Objects.nonNull(this.artifactId)) {
         throw new IllegalStateException("ArtifactProxy::setArtifactId, member \"artifactId\" has already been set.");
      }
      this.artifactId =
         Objects.requireNonNull(artifactId, "ArtifactProxy::setArtifactId, parameter \"artifactId\" cannot be null.");
   }

   /**
    * Sets the required indicator
    *
    * @param requiredId required indicator as an {@link Integer}
    * @throws IllegalStateException when the member (@link #requiredId} has already been set
    * @throws NullPointerException when parameter <code>requiredId</code> is <code>null</code>
    */

   public void setRequiredId(Integer requiredId) {
      if (Objects.nonNull(this.requiredId)) {
         throw new IllegalStateException("ArtifactProxy::setRequiredId, member \"requiredId\" has already been set.");
      }
      this.requiredId =
         Objects.requireNonNull(requiredId, "ArtifactProxy::setRequiredId, parameter \"requiredId\" cannot be null.");
   }

   /**
    * Sets the CUI category indicator
    *
    * @param cuiCategoryId CUI category indicator as an {@link Integer}
    * @throws IllegalStateException when the member {@link #cuiCatergoryId} has already been set
    * @throws NullPointerException when parameter <code>cuiCategoryId</code> is <code>null</code>
    */

   public void setCuiCategoryId(Integer cuiCategoryId) {
      if (Objects.nonNull(this.cuiCatergoryId)) {
         throw new IllegalStateException(
            "ArtifactProxy::setCuiCategoryId, member \"cuiCategoryId\" has already been set.");
      }
      this.cuiCatergoryId = Objects.requireNonNull(cuiCategoryId,
         "ArtifactProxy::setCuiCategoryId, parameter \"cuiCategoryId\" cannot be null.");
   }

   /**
    * Sets the CUI limited dissemination indicator
    *
    * @param cuiDisseminationId CUI limited dissemination indicator as an {@link Integer}
    * @throws IllegalStateException when the member {@link #cuiDisseminationId} has already been set
    * @throws NullPointerException when parameter <code>cuiDisseminationId</code> is <code>null</code>
    */

   public void setCuiDisseminationId(Integer cuiDisseminationId) {
      if (Objects.nonNull(this.cuiDisseminationId)) {
         throw new IllegalStateException(
            "ArtifactProxy::setCuiDisseminationId, member \"cuiDisseminationId\" has already been set.");
      }
      this.cuiDisseminationId = Objects.requireNonNull(cuiDisseminationId,
         "ArtifactProxy::setCuiDisseminationId, parameter \"cuiDisseminationId\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Artifact Proxy List" )
         .indentInc()
         .segment( "Artifact Identifier",   this.artifactId   )
         .segment( "Required Indicator Set Identifier", this.requiredId )
         .segment( "CUI Category Type Pair Indicator Set Identifier", this.cuiCatergoryId )
         .segment( "CUI Limited Dissemination Control Indicator Set Trigraph Country Code Indicator Set Pair Identifier", this.cuiDisseminationId )
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
      return this.toMessage(0, (Message) null).toString();
   }

}
/* EOF */
