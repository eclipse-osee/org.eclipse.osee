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

package org.eclipse.osee.framework.core.publishing;

import java.util.Objects;

/**
 * {@link RuntimeException} which is thrown when a Publishing Template has an invalid JSON Publish Options string.
 *
 * @author Loren K. Ashley
 */

public class InvalidPublishOptionsException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * The invalid JSON {@link PublishOptions} string.
    */

   private final String jsonPublishOptionsString;

   /**
    * The identifier of the publishing template with the invalid Publish Options.
    */

   private String publishingTemplateIdentifier;

   /**
    * The name of the publishing template with the invalid Publish Options.
    */

   private String publishingTemplateName;

   /**
    * Creates a new {@link RuntimeException} with a message describing the invalid JSON Publish Options string.
    *
    * @param jsonPublishOptionsString the JSON string that caused the exception.
    */

   public InvalidPublishOptionsException(String jsonPublishOptionsString) {

      super();
      this.jsonPublishOptionsString = jsonPublishOptionsString;
      this.publishingTemplateIdentifier = null;
      this.publishingTemplateName = null;
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the invalid JSON Publish Options string.
    *
    * @param jsonPublishOptionsString the JSON string that caused the exception.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public InvalidPublishOptionsException(String jsonPublishOptionsString, Throwable cause) {

      super();
      this.initCause(cause);
      this.jsonPublishOptionsString = jsonPublishOptionsString;
      this.publishingTemplateIdentifier = null;
      this.publishingTemplateName = null;
   }

   /**
    * {@inheritDoc}
    * <p>
    * Provides a detail message with the Publishing Template Identifier and Name; and the invalid JSON Publish Options
    * string.
    */

   @Override
   public String getMessage() {
      return InvalidPublishOptionsException.buildMessage(this.jsonPublishOptionsString,
         this.publishingTemplateIdentifier, this.publishingTemplateName);
   }

   /**
    * Sets the Publishing Template's identifier and name that contains the invalid JSON Publish Options string.
    *
    * @param identifier the Publishing Template's identifier.
    * @param name the Publishing Template's name.
    */

   public void setPublishingTemplateInformation(String identifier, String name) {
      this.publishingTemplateIdentifier = identifier;
      this.publishingTemplateName = name;
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param jsonPublishOptionsString the JSON string that caused the exception.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(String jsonPublishOptionsString, String publishingTemplateIdentifier,
      String publishingTemplateName) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
                .append( "\n" )
                .append( "Invalid JSON Publish Options string." ).append( "\n" )
                .append( "   Publishing Template Identifier: " ).append( Objects.nonNull( publishingTemplateIdentifier ) ? publishingTemplateIdentifier : "(null)" ).append( "\n" )
                .append( "   Publishing Template Name:       " ).append( Objects.nonNull( publishingTemplateName       ) ? publishingTemplateName       : "(null)" ).append( "\n" )
                .append( "   JSON Follows:" ).append( "\n" )
                .append( "\n" )
                .append( jsonPublishOptionsString ).append( "\n" )
                .append( "\n" )
                .toString();
      //@formatter:on
   }

}

/* EOF */
