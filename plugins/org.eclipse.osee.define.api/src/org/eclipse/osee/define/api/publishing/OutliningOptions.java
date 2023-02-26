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

package org.eclipse.osee.define.api.publishing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

public class OutliningOptions implements ToMessage {

   @JsonProperty("ArtifactName")
   private String artifactName;

   @JsonProperty("HeadingAttributeType")
   private String headingAttributeType;

   @JsonProperty("IncludeEmptyHeaders")
   private Boolean includeEmptyHeaders;

   @JsonProperty("OutlineNumber")
   private String outlineNumber;

   @JsonProperty("OutlineOnlyHeaderFolders")
   private Boolean outlineOnlyHeaderFolders;

   @JsonProperty("Outlining")
   private Boolean outlining;

   @JsonProperty("OverrideOutlineNumber")
   private Boolean overrideOutlineNumber;

   @JsonProperty("RecurseChildren")
   private Boolean recurseChildren;

   @JsonProperty("TemplateFooter")
   private Boolean templateFooter;

   public OutliningOptions() {
      this.artifactName = null;
      this.headingAttributeType = null;
      this.includeEmptyHeaders = null;
      this.outlineNumber = null;
      this.outlineOnlyHeaderFolders = null;
      this.outlining = null;
      this.overrideOutlineNumber = null;
      this.recurseChildren = null;
      this.templateFooter = null;
   }

   public OutliningOptions(String artifactType, String headingAttributeType, Boolean includeEmptyHeaders, String outlineNumber, Boolean outlineOnlyHeaderFolders, Boolean outlining, Boolean overrideOutlineNumber, Boolean recurseChildren, Boolean templateFooter) {
      this.artifactName =
         Objects.requireNonNull(artifactType, "OutliningOptions::new, parameter \"artifactName\" cannot be null.");
      this.headingAttributeType = Objects.requireNonNull(headingAttributeType,
         "OutliningOptions::new, parameter \"headingAttributeType\" cannot be null.");
      this.includeEmptyHeaders = Objects.requireNonNull(includeEmptyHeaders,
         "OutliningOptions::new, parameter \"includeEmptyHeaders\" cannot be null.");
      this.outlineNumber =
         Objects.requireNonNull(outlineNumber, "OutliningOptions::new, parameter \"outlineNumber\" cannot be null.");
      this.outlineOnlyHeaderFolders = Objects.requireNonNull(outlineOnlyHeaderFolders,
         "OutliningOptions::new, parameter \"outlineOnlyHeaderFolders\" cannot be null.");
      this.outlining =
         Objects.requireNonNull(outlining, "OutliningOptions::new, parameter \"outlining\" cannot be null.");
      this.overrideOutlineNumber = Objects.requireNonNull(overrideOutlineNumber,
         "OutliningOptions::new, parameter \"overrideOutlineNumber\" cannot be null.");
      this.recurseChildren = Objects.requireNonNull(recurseChildren,
         "OutliningOptions::new, parameter \"recurseChildren\" cannot be null.");
      this.templateFooter =
         Objects.requireNonNull(templateFooter, "OutliningOptions::new, parameter \"templateFooter\" cannot be null.");
   }

   public void defaults() {
      if (Objects.isNull(this.artifactName)) {
         this.artifactName = "";
      }
      if (Objects.isNull(this.headingAttributeType)) {
         this.headingAttributeType = "";
      }
      if (Objects.isNull(this.includeEmptyHeaders)) {
         this.includeEmptyHeaders = false;
      }
      if (Objects.isNull(this.outlineNumber)) {
         this.outlineNumber = "";
      }
      if (Objects.isNull(this.outlineOnlyHeaderFolders)) {
         this.outlineOnlyHeaderFolders = false;
      }
      if (Objects.isNull(this.outlining)) {
         this.outlining = false;
      }
      if (Objects.isNull(this.overrideOutlineNumber)) {
         this.overrideOutlineNumber = false;
      }
      if (Objects.isNull(this.recurseChildren)) {
         this.recurseChildren = false;
      }
      if (Objects.isNull(this.templateFooter)) {
         this.templateFooter = false;
      }
   }

   public String getArtifactName() {
      if (Objects.isNull(this.artifactName)) {
         throw new IllegalStateException(
            "OutliningOptions::getArtifactName, the member \"artifactName\" has not been set.");
      }
      return this.artifactName;
   }

   public String getHeadingAttributeType() {
      if (Objects.isNull(this.headingAttributeType)) {
         throw new IllegalStateException(
            "OutliningOptions::getHeadingAttributeType, the member \"headingAttributeType\" has not been set.");
      }
      return this.headingAttributeType;
   }

   public String getOutlineNumber() {
      if (Objects.isNull(this.outlineNumber)) {
         throw new IllegalStateException(
            "OutliningOptions::getOutlineNumber, the member \"outlineNumber\" has not been set.");
      }
      return this.outlineNumber;
   }

   public boolean isIncludeEmptyHeaders() {
      if (Objects.isNull(this.includeEmptyHeaders)) {
         throw new IllegalStateException(
            "OutliningOptions::isIncludeEmptyHeaders, the member \"includeEmptyHeaders\" has not been set.");
      }
      return this.includeEmptyHeaders;
   }

   public boolean isOutlineOnlyHeaderFolders() {
      if (Objects.isNull(this.outlineOnlyHeaderFolders)) {
         throw new IllegalStateException(
            "OutliningOptions::isOutlineOnlyHeaderFolders, the member \"outlineOnlyHeaderFolders\" has not been set.");
      }
      return this.outlineOnlyHeaderFolders;
   }

   public boolean isOutlining() {
      if (Objects.isNull(this.outlining)) {
         throw new IllegalStateException("OutliningOptions::isOutlining, the member \"outlining\" has not been set.");
      }
      return this.outlining;
   }

   public boolean isOverrideOutlineNumber() {
      if (Objects.isNull(this.overrideOutlineNumber)) {
         throw new IllegalStateException(
            "OutliningOptions::isOverrideOutlineNumber, the member \"overrideOutlineNumber\" has not been set.");
      }
      return this.overrideOutlineNumber;
   }

   public boolean isRecurseChildren() {
      if (Objects.isNull(this.recurseChildren)) {
         throw new IllegalStateException(
            "OutliningOptions::isRecurseChildren, the member \"recurseChildren\" has not been set.");
      }
      return this.recurseChildren;
   }

   public boolean isTemplateFooter() {
      if (Objects.isNull(this.templateFooter)) {
         throw new IllegalStateException(
            "OutliningOptions::isTemplateFooter, the member \"templateFooter\" has not been set.");
      }
      return this.templateFooter;
   }

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.artifactName )
         && Objects.nonNull( this.headingAttributeType )
         && Objects.nonNull( this.includeEmptyHeaders )
         && Objects.nonNull( this.outlineNumber )
         && Objects.nonNull( this.outlineOnlyHeaderFolders )
         && Objects.nonNull( this.outlining )
         && Objects.nonNull( this.overrideOutlineNumber )
         && Objects.nonNull( this.recurseChildren )
         && Objects.nonNull( this.templateFooter );
      //@formatter:on
   }

   public void setArtifactName(String artifactName) {
      if (Objects.nonNull(this.artifactName)) {
         throw new IllegalStateException(
            "OutliningOptions::setArtifactName, member \"artifactName\" has already been set.");
      }
      this.artifactName =
         Objects.requireNonNull(artifactName, "OutliningOptions::new, parameter \"artifactName\" cannot be null.");
   }

   public void setHeadingAttributeType(String headingAttributeType) {
      if (Objects.nonNull(this.headingAttributeType)) {
         throw new IllegalStateException(
            "OutliningOptions::setHeadingAttributeType, member \"headingAttributeType\" has already been set.");
      }
      this.headingAttributeType = Objects.requireNonNull(headingAttributeType,
         "OutliningOptions::new, parameter \"headingAttributeType\" cannot be null.");
   }

   public void setIncludeEmptyHeaders(Boolean includeEmptyHeaders) {
      if (Objects.nonNull(this.includeEmptyHeaders)) {
         throw new IllegalStateException(
            "OutliningOptions::setIncludeEmptyHeaders, member \"includeEmptyHeaders\" has already been set.");
      }
      this.includeEmptyHeaders = Objects.requireNonNull(includeEmptyHeaders,
         "OutliningOptions::new, parameter \"includeEmptyHeaders\" cannot be null.");
   }

   public void setOutlineNumber(String outlineNumber) {
      if (Objects.nonNull(this.outlineNumber)) {
         throw new IllegalStateException(
            "OutliningOptions::setOutlineNumber, member \"outlineNumber\" has already been set.");
      }
      this.outlineNumber =
         Objects.requireNonNull(outlineNumber, "OutliningOptions::new, parameter \"outlineNumber\" cannot be null.");
   }

   public void setOutlineOnlyHeaderFolders(Boolean outlineOnlyHeaderFolders) {
      if (Objects.nonNull(this.outlineOnlyHeaderFolders)) {
         throw new IllegalStateException(
            "OutliningOptions::setOutlineOnlyHeaderFolders, member \"outlineOnlyHeaderFolders\" has already been set.");
      }
      this.outlineOnlyHeaderFolders = Objects.requireNonNull(outlineOnlyHeaderFolders,
         "OutliningOptions::new, parameter \"outlineOnlyHeaderFolders\" cannot be null.");
   }

   public void setOutlining(Boolean outlining) {
      if (Objects.nonNull(this.outlining)) {
         throw new IllegalStateException("OutliningOptions::setOutlining, member \"outlining\" has already been set.");
      }
      this.outlining =
         Objects.requireNonNull(outlining, "OutliningOptions::new, parameter \"outlining\" cannot be null.");
   }

   public void setOverrideOutlineNumber(Boolean overrideOutlineNumber) {
      if (Objects.nonNull(this.overrideOutlineNumber)) {
         throw new IllegalStateException(
            "OutliningOptions::setOverrideOutlineNumber, member \"overrideOutlineNumber\" has already been set.");
      }
      this.overrideOutlineNumber = Objects.requireNonNull(overrideOutlineNumber,
         "OutliningOptions::new, parameter \"overrideOutlineNumber\" cannot be null.");
   }

   public void setRecurseChildren(Boolean recurseChildren) {
      if (Objects.nonNull(this.recurseChildren)) {
         throw new IllegalStateException(
            "OutliningOptions::setRecurseChildren, member \"recurseChildren\" has already been set.");
      }
      this.recurseChildren = Objects.requireNonNull(recurseChildren,
         "OutliningOptions::new, parameter \"recurseChildren\" cannot be null.");
   }

   public void setTemplateFooter(Boolean templateFooter) {
      if (Objects.nonNull(this.templateFooter)) {
         throw new IllegalStateException(
            "OutliningOptions::setTemplateFooter, member \"templateFooter\" has already been set.");
      }
      this.templateFooter =
         Objects.requireNonNull(templateFooter, "OutliningOptions::new, parameter \"templateFooter\" cannot be null.");
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
         .title( "OutliningOptions" )
         .indentInc()
         .segment( "Artifact Name",               this.artifactName             )
         .segment( "Heading Attribute Type",      this.headingAttributeType     )
         .segment( "Include Empty Headers",       this.includeEmptyHeaders      )
         .segment( "Outline Number",              this.outlineNumber            )
         .segment( "Outline Only Header Folders", this.outlineOnlyHeaderFolders )
         .segment( "Outlining",                   this.outlining                )
         .segment( "Override Outline Number",     this.overrideOutlineNumber    )
         .segment( "Recurse Children",            this.recurseChildren          )
         .segment( "Template Footers",            this.templateFooter           )
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
