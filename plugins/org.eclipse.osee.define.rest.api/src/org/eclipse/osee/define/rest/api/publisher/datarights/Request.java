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

import java.util.Arrays;
import java.util.Objects;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates data structure required to make a request for a data rights analysis from the Data Rights Manager.
 *
 * @author Md I. Khan
 */

public class Request implements ToMessage {

   /**
    * The title, head, and footer statements in the response will be in the format specified by the format-indicator.
    */

   private FormatIndicator format;

   /**
    * PublishingTemplateRequest object which contains the parameters to request a Publishing Template
    */

   private PublishingTemplateRequest publishingTemplateRequest;

   /**
    * An ordered list of artifact proxies.
    */

   private ArtifactProxy[] artifactProxyList;

   /**
    * Creates a new empty {@link Request} object for JSON deserialization
    */

   public Request() {
      this.format = null;
      this.publishingTemplateRequest = null;
      this.artifactProxyList = null;
   }

   /**
    * Creates a new {@link Request} object with data for JSON serialization
    *
    * @param format format indicator as an object of {@link FormatIndicator}
    * @param publishingTemplateRequest a request for a publishing template as an object of
    * {@link PublishingTemplateRequest}
    * @param artifactProxyList artifact proxies as an array of {@link ArtifactProxy}
    * @throws NullPointerException when any of the parameter are null
    */

   public Request(FormatIndicator format, PublishingTemplateRequest publishingTemplateRequest, ArtifactProxy[] artifactProxyList) {
      this.format = Objects.requireNonNull(format, "Request::new, parameter \"format\" cannot be null.");
      this.publishingTemplateRequest = Objects.requireNonNull(publishingTemplateRequest,
         "Request::new, parameter \"publishingTemplateRequest\" cannot be null.");
      this.artifactProxyList =
         Objects.requireNonNull(artifactProxyList, "Request::new, parameter \"artifactProxyList\" cannot be null.");
   }

   /**
    * Gets the format indicator
    *
    * @return format
    * @throws IllegalStateException when {@link #format} has not been set
    */

   public FormatIndicator getFormat() {
      if (Objects.isNull(this.format)) {
         throw new IllegalStateException("Request::getFormat, member \"format\" has not been set.");
      }
      return this.format;
   }

   /**
    * Gets the publishing template request
    *
    * @return publishingTemplateRequest
    * @throws IllegalStateException when {@link #publishingTemplateRequest} has not been set
    */

   public PublishingTemplateRequest getPublishingTemplateRequest() {
      if (Objects.isNull(this.publishingTemplateRequest)) {
         throw new IllegalStateException(
            "Request::getPublishingTemplateRequest, member \"publishingTemplateRequest\" has not been set.");
      }
      return this.publishingTemplateRequest;
   }

   /**
    * Gets an array of artifact proxy
    *
    * @return artifactProxyList
    * @throws IllegalStateException when {@link #artifactProxyList} has not been set
    */

   public ArtifactProxy[] getArtifactProxyList() {
      if (Objects.isNull(this.artifactProxyList)) {
         throw new IllegalStateException(
            "Request::getArtifactProxyList, member \"artifactProxyList\" has not been set.");
      }
      return this.artifactProxyList;
   }

   /**
    * Predicates to test the validity of {@link Request} object
    *
    * @return <code>true</code> when all members are non-<code>null</code>; otherwise <code>false</code>
    */

   public boolean isValid() {

      //@formatter:off
      return
         Objects.nonNull( this.format ) && this.format.isValid()
         &&  Objects.nonNull( this.publishingTemplateRequest ) && this.publishingTemplateRequest.isValid()
         &&  Objects.nonNull( this.artifactProxyList ) && Arrays.stream(this.artifactProxyList).allMatch( ArtifactProxy::isValid );
      //@formatter:on
   }

   /**
    * Sets the format indicator
    *
    * @param format format indicator as an object of {@link FormatIndicator}
    * @throws IllegalStateException when the member {@link #format} has already been set
    * @throws NullPointerException when parameter <code>label</code> is <code>null</code>
    */

   public void setFormat(FormatIndicator format) {
      if (Objects.nonNull(this.format)) {
         throw new IllegalStateException("Request::setFormat, member \"format\" has already been set.");
      }
      this.format = Objects.requireNonNull(format, "Request::setFormat, parameter \"format\" cannot be null.");
   }

   /**
    * Sets the publishing template request
    *
    * @param publishingTemplateRequest request for a publishing template as an object of
    * {@link PublishingTemplateRequest}
    * @throws IllegalStateException when member {@link #publishingTemplateRequest} has already been set
    * @throws NullPointerException when parameter <code>publishingTemplateRequest</code> is <code>null</code>
    */

   public void setPublishingTemplateRequest(PublishingTemplateRequest publishingTemplateRequest) {
      if (Objects.nonNull(this.publishingTemplateRequest)) {
         throw new IllegalStateException(
            "Request::setPublishingTemplateRequest, member \"publishingTemplateRequest\" has already been set.");
      }
      this.publishingTemplateRequest = Objects.requireNonNull(publishingTemplateRequest,
         "Request::setPublishingTemplateRequest, parameter \"publishingTemplateRequest\" cannot be null.");
   }

   /**
    * Sets the array of artifact proxy
    *
    * @param artifactProxyList artifact proxies as an array of {@link ArtifactProxy}
    * @throws IllegalStateException when member {@link #artifactProxyList} has already been set
    * @throws NullPointerException when parameter <code>artifactProxyList</code> is <code>null</code>
    */

   public void setArtifactProxyList(ArtifactProxy[] artifactProxyList) {
      if (Objects.nonNull(this.artifactProxyList)) {
         throw new IllegalStateException(
            "Request::setArtifactProxyList, member \"artifactProxyList\" has already been set.");
      }
      this.artifactProxyList = Objects.requireNonNull(artifactProxyList,
         "Request::setArtifactProxyList, parameter \"artifactProxyList\" cannot be null.");
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
         .title( "Data Rights - Request" )
         .indentInc()
         .segment( "Format Indicator",   this.format   )
         .segment( "Publishing Template Request", this.publishingTemplateRequest )
         .segment( "Artifact Proxy List", this.artifactProxyList )
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