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

package org.eclipse.osee.define.rest.api.publisher.templatemanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates the parameters to request a Publishing Template from the Template Manager. Publishing Templates may be
 * requested with the Publishing Template artifact name, Template Match Criteria, or with the Publishing Template
 * artifact identifier. The client side {@link IRenderer} implementation used to perform a publish is determined through
 * an applicability and priority selection process. The selected {@link IRenderer} implementation will use the Template
 * Match Criteria to select a Publishing Template for the publish. The Template Match Criteria selection process allows
 * {@link IRenderer} implementations to select a Publishing Template based upon the type of artifact being published,
 * the type of publishing presentation, an {@link IRenderer} implementation specific criteria, and server properties.
 * This is the preferred Publishing Template request method. When a function requests a Publishing Template by
 * identifier it should be for a "well known" artifact with a code defined {@link ArtifactToken}.
 * <p>
 * <h2>Selection by Publishing Template Artifact Name</h2>
 * <p>
 * When a Publishing Template request is being by options and the optional <code>option</code> parameter is specified,
 * the first Publishing Template found with an artifact name that matches the value of <code>option</code> will be
 * selected. When the artifact name of more than one Publishing Template matches the value of <code>option</code>, the
 * selected Publishing Template will be indeterminate. When no Publishing Templates have a matching name, the selection
 * process will continue using Template Match Criteria.
 * <p>
 * <h2>Selection by Template Match Criteria</h2>
 * <p>
 * Template Match Criteria have the follow parts:
 *
 * <pre>
 * &lt;rendererId&gt; [ " " &lt;publishingArtifactTypeName&gt; ] " " &lt;presentationType&gt; [ " " &lt;option&gt; ] [ " NO TAGS" ]
 * </pre>
 *
 * Where:
 * <dl>
 * <dt><code>rendererId</code>:</dt>
 * <dd>The required first token is a unique identifier for the {@link IRenderer} implementation that is requesting the
 * publish or a unique pseudo-renderer identifier for a non-renderer function requesting a publish.</dd>
 * <dt><code>publishingArtifactTypeName</code>:</dt>
 * <dd>By convention the optional second token is the name of an OSEE Artifact Type.</dd>
 * <dt><code>presentationType</code></dt>
 * <dd>By convention the required third token is an enumeration member name of the {@link PresentationType}
 * enumeration.</dd>
 * <dt><code>option</code>:</dt>
 * <dd>The optional forth token, when not the Artifact name of a Publishing Template, is an additional token used for
 * publishing template selection.</dd>
 * <dt>NO TAGS:</dt>
 * <dd>When the server parameter &quot;osee.publish.no.tags&quot; is &quot;true&quot; in any case it indicates that only
 * the publishing templates with a Template Match Criteria attribute ending with the string &quot; NO TAGS&quot; will be
 * selected; otherwise, the &quot; NO TAGS&quot; string must not be present for a match.</dd>
 * </dl>
 * <p>
 * The Template Manager will create up to four Template Match Criteria strings based upon the Template Match Criteria
 * specified in the {@link PublishingTemplateRequest}. The Template Match Criteria strings created and their selection
 * priority are as follows:
 * <table border="1">
 * <tr>
 * <th>Priority</th>
 * <th>Created When the Template Match Criteria are Specified</th>
 * <th>Template Match Criteria string</th>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>
 * <ul>
 * <li><code>publishingArtifactTypeName</code></li>
 * <li><code>option</code></li>
 * </ul>
 * </td>
 * <td>
 *
 * <pre>
 * &lt;rendererId&gt; " " &lt;publishingArtifactTypeName&gt; " " &lt;presentationType&gt; " " &lt;option&gt; [ " NO TAGS" ]
 * </pre>
 *
 * </td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>
 * <ul>
 * <li><code>publishingArtifactTypeName</code></li>
 * </ul>
 * </td>
 * <td>
 *
 * <pre>
 * &lt;rendererId&gt; " " &lt;publishingArtifactTypeName&gt; " " &lt;presentationType&gt; [ " NO TAGS" ]
 * </pre>
 *
 * </td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>
 * <ul>
 * <li><code>option</code></li>
 * </ul>
 * </td>
 * <td>
 *
 * <pre>
 * &lt;rendererId&gt; " " &lt;presentationType&gt; " " &lt;option&gt; [ " NO TAGS" ]
 * </pre>
 *
 * </td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>(always)</td>
 * <td>
 *
 * <pre>
 * &lt;rendererId&gt; " " &lt;presentationType&gt; [ " NO TAGS" ]
 * </pre>
 *
 * </td>
 * </tr>
 * </table>
 * Notes:
 * <ul>
 * <li>Higher priority values have higher priority.
 * <li>The optional " NO TAGS" is included at the end of the Template Match Criteria strings when the server property
 * "osee.publish.no.tags" is set to "true".</li>
 * </ul>
 * Starting with the highest priority Template Match Criteria string that was created, all of the Publishing Templates
 * are searched for one with a {@link CoreAttributeTypes#TemplateMatchCriteria} attribute that contains an exact match
 * to the Template Match Criteria string. The first found Publishing Template with a matching Template Match Criteria is
 * the selected Publishing Template. If no template is found, the process is repeated with the next highest priority
 * Template Match Criteria string that was created. If no Publishing Template is found with a match for all of the
 * Template Match Criteria strings created, the Publishing Template selection by Template Match Criteria fails.
 * <p>
 * When more than one Publishing Template has the same Template Match Criteria strings in the
 * {@link CoreAttributeTypes#TemplateMatchCriteria} attribute and that Template Match Criteria string is being searched
 * for, the Publishing Template from the set of Publishing Templates with a matching Template Match Criteria string that
 * is selected will be indeterminate.
 * <p>
 * <h2>Selection by Publishing Template Artifact Identifier</h2>
 * <p>
 * When a Publishing Template request is being made by identifier, the "AT-" prefix is removed from the identifier
 * string and the remaining digits are converted to an {@link ArtifactId}. If a Publishing Template with a matching
 * {@link ArtifactId} is found is the selected Publishing Template.
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

   private @Nullable Boolean byOptions;

   /**
    * An additional {@link String} that maybe provided for Publishing Template selection. This member maybe
    * <code>null</code> even when the request is by options.
    */

   private @Nullable String option;

   /**
    * A string representation of the {@link PresentationType} enumeration that describes how the publishing results will
    * be presented to the user.
    */

   private @Nullable String presentationType;

   /**
    * The OSEE Artifact Type Name for the primary Artifact being published.
    */

   private @Nullable String publishArtifactTypeName;

   /**
    * A unique identifier for the {@link IRenderer} implementation that is requesting the Publishing Template.
    */

   private @Nullable String rendererId;

   /**
    * A unique identifier for a Publishing Template.
    */

   private @Nullable String templateId;

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
    * Template. When the publishing request is made by a non-renderer this parameters needs to be unique pseudo-renderer
    * identifier.
    * @param publishArtifactTypeName the OSEE Artifact Type Name for the artifacts being published. This parameter may
    * be <code>null</code>, empty, or blank.
    * @param presentationType a string representation of the {@link PresentationType} enumeration that describes how the
    * publishing results will be presented to the user.
    * @param option the name of the template to select or an additional {@link String} that may be provided for
    * Publishing Template selection. This parameter may be <code>null</code>, empty, or blank.
    * @throws IllegalArgumentException when <code>rendererId</code> or <code>presentationType</code> are
    * <code>null</code> or blank.
    */

   @JsonIgnore
   public PublishingTemplateRequest(@NonNull String rendererId, @Nullable String publishArtifactTypeName, @NonNull String presentationType, @Nullable String option) {

      //@formatter:off
      this.byOptions               = true;
      this.rendererId              = Strings.isValidAndNonBlank( rendererId              ) ? rendererId              : null;
      this.publishArtifactTypeName = Strings.isValidAndNonBlank( publishArtifactTypeName ) ? publishArtifactTypeName : null;
      this.presentationType        = Strings.isValidAndNonBlank( presentationType        ) ? presentationType        : null;
      this.option                  = Strings.isValidAndNonBlank( option                  ) ? option                  : null;
      this.templateId              = null;
      //@formatter:on
      if (Objects.isNull(this.rendererId) || Objects.isNull(this.presentationType)) {
         throw new IllegalArgumentException();
      }
   }

   /**
    * Creates a new {@link PublishingTemplateRequest} by template identifier for serialization (client) or for making a
    * Publishing Template Manager Service call (server).
    *
    * @param templateId a unique identifier for a Publishing Template.
    * @throws NullPointerException when <code>templateId</code> is <code>null</code>.
    */

   @JsonIgnore
   public PublishingTemplateRequest(@NonNull String templateId) {

      //@formatter:off
      this.byOptions               = false;
      this.option                  = null;
      this.presentationType        = null;
      this.publishArtifactTypeName = null;
      this.rendererId              = null;
      this.templateId              = Objects.requireNonNull(templateId);
      //@formatter:on
   }

   /**
    * Gets the by options {@link PublishingTemplateRequset} option {@link #option}.
    *
    * @return the {@link PublishingTemplateRequset} option {@link #option}. This method may return <code>null</code>.
    * @throws IllegalStateException when the member {@link #byOptions} has not been set.
    */

   public @Nullable String getOption() {
      if (Objects.isNull(this.byOptions)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getOption, the member \"byOptions\" has not been set.");
      }
      return this.option;
   }

   /**
    * Gets the by options {@link PublishingTemplateRequset} option {@link #presentationType}.
    *
    * @return the {@link PublishingTemplateRequset} option {@link #presentationType}.
    * @throws IllegalStateException when the member {@link #byOptions} has not been set; or the member
    * {@link #byOptions} is <code>true</code> and the member {@link #presentationType} has not been set.
    */

   public @Nullable String getPresentationType() {
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

   public @Nullable String getPublishArtifactTypeName() {
      if (Objects.isNull(this.byOptions)) {
         throw new IllegalStateException(
            "PublishingTemplateRequest::getTemplateId, the member \"byOptions\" has not been set.");
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

   public @Nullable String getRendererId() {
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

   public @Nullable String getTemplateId() {
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

   @JsonIgnore
   public boolean isValid() {

      if (Objects.isNull(this.byOptions)) {
         return false;
      }

      if (this.byOptions) {
         //@formatter:off
         return
               Objects.nonNull( this.presentationType ) && !this.presentationType.isBlank()
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

   @JsonIgnore
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
               .indentInc()
               .segment( "Renderer Identifier",        this.rendererId              )
               .segment( "Publish Artifact Type Name", this.publishArtifactTypeName )
               .segment( "Presentation Type",          this.presentationType        )
               .segment( "Option",                     this.option                  )
               .segment( "Template Identifier",        this.templateId )
               .indentDec()
               .indentDec();
         //@formatter:on
      }

      if (this.byOptions) {
         //@formatter:off
         return
            outMessage
               .title( "Publishing Template Requset is by options." )
               .indentInc()
               .segment( "Renderer Identifier",        this.rendererId              )
               .segment( "Publish Artifact Type Name", this.publishArtifactTypeName )
               .segment( "Presentation Type",          this.presentationType        )
               .segment( "Option",                     this.option                  )
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

   @JsonIgnore
   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }
}

/* EOF */
