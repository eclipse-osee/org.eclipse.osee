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

package org.eclipse.osee.define.api.publishing.templatemanager;

import java.util.Objects;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates the parameters to request a Publishing Template from the
 * {@link TemplateManagerEndpoint#getPublishingTemplate} endpoint.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateRequest implements ToMessage {

   /**
    * Flag indicates the request is being made my options or by template identifier. When <code>true</code> the
    * following members are valid:
    * <ul>
    * <li>{@link #option},</li>
    * <li>{@link #presetnationType},</li>
    * <li>{@link #publishArtifactTypeName}, and</li>
    * <li>{@link #rendererId}.</li>
    * </ul>
    * When <code>false</code> the following members are valid:
    * <ul>
    * <li>{@link templateId}.</li>
    * </ul>
    */

   private Boolean byOptions;

   /**
    * An additional {@link String} that maybe provided for Publishing Template selection. This member maybe
    * <code>null</code> even when the request is by options.
    */

   private String option;

   /**
    * A string representation of the {@link PresentationType} enumeration that describes how the publishing results will
    * be presented to the user.
    */

   private String presentationType;

   /**
    * The OSEE Artifact Type Name for the primary Artifact being published.
    */

   private String publishArtifactTypeName;

   /**
    * A unique identifier for the {@link IRenderer} implementation that is requesting the Publishing Template.
    */

   private String rendererId;

   /**
    * A unique identifier for a Publishing Template.
    */

   private String templateId;

   /**
    * Creates a new empty {@link PublishingTemplateRequest} for JSON deserialization.
    */

   public PublishingTemplateRequest() {
      this.byOptions = null;
      this.option = null;
      this.presentationType = null;
      this.publishArtifactTypeName = null;
      this.rendererId = null;
   }

   /**
    * Creates a new {@link PublishingTemplateRequest} by options for serialization (client) or for making a Publishing
    * Template Manager Service call (server).
    *
    * @param rendererId a unique identifier for the {@link IRenderer} implementation that is requesting the Publishing
    * Template.
    * @param publishArtifactTypeName the OSEE Artifact Type Name for the primary Artifact being published.
    * @param presentationType a string representation of the {@link PresentationType} enumeration that describes how the
    * publishing results will be presented to the user.
    * @param option an additional {@link String} that maybe provided for Publishing Template selection. This parameter
    * maybe <code>null</code>.
    */

   public PublishingTemplateRequest(String rendererId, String publishArtifactTypeName, String presentationType, String option) {

      this.byOptions = true;
      this.templateId = null;

      //@formatter:off
      this.rendererId =
         Objects.requireNonNull(rendererId, "PublishingTemplateRequest::new, parameter \"rendererId\" cannot be null.");
      this.publishArtifactTypeName =
         Objects.requireNonNull(publishArtifactTypeName, "PublishingTemplateRequest::new, parameter \"publishArtifactTypeName\" cannot be null.");
      this.presentationType =
         Objects.requireNonNull(presentationType, "PublishingTemplateRequest::new, parameter \"presentationType\" cannot be null.");
      this.option = option;
      //@formatter:on
   }

   /**
    * Creates a new {@link PublishingTemplateRequest} by template identifier for serialization (client) or for making a
    * Publishing Template Manager Service call (server).
    *
    * @param templateId a unique identifier for a Publishing Template.
    */

   public PublishingTemplateRequest(String templateId) {

      this.byOptions = false;
      this.option = null;
      this.presentationType = null;
      this.publishArtifactTypeName = null;
      this.rendererId = null;

      this.templateId =
         Objects.requireNonNull(templateId, "PublishingTemplateRequest::new, parameter \"templateId\" cannot be null.");
   }

   /**
    * Gets the by options {@link PublishingTemplateRequset} option {@link #option}.
    *
    * @return the {@link PublishingTemplateRequset} option {@link #option}. This method may return <code>null</code>.
    */

   public String getOption() {
      return this.option;
   }

   /**
    * Gets the by options {@link PublishingTemplateRequset} option {@link #presentationType}.
    *
    * @return the {@link PublishingTemplateRequset} option {@link #presentationType}.
    * @throws IllegalStateException when the member {@link #byOption} has not been set; or the member {@link #byOption}
    * is <code>true</code> and the member {@link #presentationType} has not been set.
    */

   public String getPresentationType() {
      if (Objects.isNull(this.byOptions)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getTemplateId, the member \"byOptions\" has not been set.");
      }
      if (Objects.isNull(this.presentationType) && this.byOptions) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getPresentationType, the member \"presentationType\" has not been set.");
      }
      return this.presentationType;
   }

   /**
    * Gets the by options {@link PublishingTemplateRequset} option {@link #publishArtifactTypeName}.
    *
    * @return the {@link PublishingTemplateRequset} option {@link #publishArtifactTypeName}.
    * @throws IllegalStateException when the member {@link #byOption} has not been set; or the member {@link #byOption}
    * is <code>true</code> and the member {@link #publishArtifactTypeName} has not been set.
    */

   public String getPublishArtifactTypeName() {
      if (Objects.isNull(this.byOptions)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getTemplateId, the member \"byOptions\" has not been set.");
      }
      if (Objects.isNull(this.publishArtifactTypeName) && this.byOptions) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getPublishArtifactTypeName, the member \"publishArtifactTypeName\" has not been set.");
      }
      return this.publishArtifactTypeName;
   }

   /**
    * Gets the by options {@link PublishingTemplateRequset} option {@link #rendererId}.
    *
    * @return the {@link PublishingTemplateRequset} option {@link #rendererId}.
    * @throws IllegalStateException when the member {@link #byOption} has not been set; or the member {@link #byOption}
    * is <code>true</code>, and the member {@link #rendererId} has not been set.
    */

   public String getRendererId() {
      if (Objects.isNull(this.byOptions)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getTemplateId, the member \"byOptions\" has not been set.");
      }
      if (Objects.isNull(this.rendererId) && this.byOptions) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getRendererId, the member \"rendererId\" has not been set.");
      }
      return this.rendererId;
   }

   /**
    * Gets the by identifier {@link PublishingTemplateRequset} {@link #templateId}.
    *
    * @return the {@link PublishingTemplateRequset} {@link #templateId}.
    * @throws IllegalStateException when the member {@link #byOption} has not been set; or the member {@link #byOption}
    * is <code>false</code> and the member {@link #templateId} has not been set.
    */

   public String getTemplateId() {
      if (Objects.isNull(this.byOptions)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getTemplateId, the member \"byOptions\" has not been set.");
      }
      if (Objects.isNull(this.templateId) && !this.byOptions) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getTemplateId, the member \"templateId\" has not been set.");
      }
      return this.templateId;
   }

   /**
    * Predicate to determine if the request is being made by options or by template identifier.
    *
    * @return <code>true</code> when the request is being made by options; otherwise, <code>false</code>.
    * @throws IllegalStateException when the member {@link #byOptions} has not yet been set.
    */

   public boolean isByOptions() {
      if (Objects.isNull(this.byOptions)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::isByOptions, the member \"byOptions\" has not been set.");
      }
      return this.byOptions;
   }

   /**
    * Predicate to test the validity of the {@link PublishingTemplateRequest} object. The validity is determined as
    * follows:
    * <dl>
    * <dt>Member {@link #byOptions} has not been set:</dt>
    * <dd>the request is invalid.</dd>
    * <dt>Member {@link #byOptions} is <code>true</code>:</dt>
    * <dd>The following members must be set and valid:
    * <dl>
    * <dt>{@link #presentationType}</dt>
    * <dd>This member must be set to a non-empty and non-whitespace string.</dd>
    * <dt>{@link #publishArtifactTypeName}</dt>
    * <dd>This member must be set.</dd>
    * <dt>{@link #rendererId}</dt>
    * <dd>This member must be set.</dd>
    * </dl>
    * </dd>
    * <dt>Member {@link #byOptions} is <code>false</code>:</dt>
    * <dd>The following members must be set and valid:
    * <dl>
    * <dt>{@link #templateId}</dt>
    * <dd>This member must be set to a valid base-10 integer greater than zero.</dd>
    * </dl>
    * </dd>
    * </dl>
    *
    * @return <code>true</code>, when the {@link PublishingTempateRequset} is valid; otherwise, <code>false</code>.
    */

   public boolean isValid() {

      if (Objects.isNull(this.byOptions)) {
         return false;
      }

      if (this.byOptions) {
         //@formatter:off
         return
               Objects.nonNull( this.presentationType ) && !this.presentationType.isBlank()
            && Objects.nonNull( this.publishArtifactTypeName )
            && Objects.nonNull( this.rendererId ) && !this.rendererId.isBlank()
            && Objects.isNull( this.templateId );
         //@formatter:on
      }

      //@formatter:off
      return
            Objects.isNull( this.option )
         && Objects.isNull( this.presentationType )
         && Objects.isNull( this.publishArtifactTypeName )
         && Objects.isNull( this.rendererId )
         && Objects.nonNull( this.templateId );
      //@formatter:on
   }

   /**
    * Sets the {@link #byOptions} flag to indicate the {@link PublishingTemplateRequest} will be by options
    * (<code>true</code>) or by identifier (<code>false</code>).
    *
    * @param byOptions <code>true</code> for by options and <code>false</code> for by identifier.
    * @throws NullPointerException when the parameter <code>byOptions</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #byOptions} has already been set.
    */

   public void setByOption(Boolean byOptions) {
      if (Objects.nonNull(this.byOptions)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::setByOptions, member \"byOptions\" has already been set.");
      }
      this.byOptions = Objects.requireNonNull(byOptions,
         "PublishingTemplateRequset::setByOptions, parameter \"byOptions\" cannot be null.");
   }

   /**
    * Sets the additional {@link String} {@link #option} for Publishing Template selection.
    *
    * @param option the additional {@link String} for Publishing Template selection.
    * @throws IllegalStateException when the member {@link #option} has already been set.
    */

   public void setOption(String option) {
      if (Objects.nonNull(this.option)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::setOption, member \"option\" has already been set.");
      }
      this.option = option;
   }

   /**
    * Sets the {@link String} representation of the {@link PresentationType} option for the Publishing Template
    * selection.
    *
    * @param presentationType {@link String} representation of the {@link PresentationType} Publishing Template
    * selection option.
    * @throws IllegalStateException when the member {@link #presentationType} has already been set.
    */

   public void setPresentationType(String presentationType) {
      if (Objects.nonNull(this.presentationType)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::setPresentationType, member \"presentationType\" has already been set.");
      }
      this.presentationType = presentationType;
   }

   /**
    * Sets the OSEE Artifact Type name of the primary Artifact being published.
    *
    * @param publishArtifactTypeName OSEE Artifact Type name of the primary Artifact being published.
    * @throws IllegalStateException when the member {@link #publishArtifactTypeName} has already been set.
    */

   public void setPublishArtifactTypeName(String publishArtifactTypeName) {
      if (Objects.nonNull(this.publishArtifactTypeName)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::setPublishArtifactTypeName, member \"publishArtifactTypeName\" has already been set.");
      }
      this.publishArtifactTypeName = publishArtifactTypeName;
   }

   /**
    * Sets the unique identifier of the {@link IRenderer} implementation making the Publishing Template request.
    *
    * @param rendererId the unique identifier of the {@link IRenderer} implementation making the Publishing Template
    * request.
    * @throws IllegalStateException when the member {@link #rendererId} has already been set.
    */

   public void setRendererId(String rendererId) {
      if (Objects.nonNull(this.rendererId)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::setRendererId, member \"rendererId\" has already been set.");
      }
      this.rendererId = rendererId;
   }

   /**
    * Sets the unique identifier of the Publishing Template being requested.
    *
    * @param templateId the unique identifier Publishing Template being requested.
    * @throws IllegalStateException when the member {@link #templateId} has already been set.
    */

   public void setTemplateId(String templateId) {
      if (Objects.nonNull(this.templateId)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::setTemplateId, member \"templateId\" has already been set.");
      }
      this.templateId = templateId;
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
         .title( "Publishing Template Request:" )
         .indentInc()
         ;
      //@formatter:on

      if (Objects.isNull(this.byOptions)) {
         //@formatter:off
         return
            outMessage
               .title( "IllegalState, member \"byOptions\" is null." )
               .indentDec();
         //@formatter:on
      }

      if (this.byOptions) {
         //@formatter:off
         return
            outMessage
               .title( "Publishing Template Requset is by options." )
               .indentInc()
               .segment( "Option",                     this.option                  )
               .segment( "Presentation Type",          this.presentationType        )
               .segment( "Publish Artifact Type Name", this.publishArtifactTypeName )
               .segment( "Renderer Identifier",        this.rendererId              )
               .indentDec()
               .indentDec()
               ;
         //@formatter:on
      }

      //@formatter:off
      return
         outMessage
            .title( "Publishing Template Request is by template identifier." )
            .indentInc()
            .segment( "Template Identifier", this.templateId )
            .indentDec()
            .indentDec()
            ;
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
