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

import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * {@link RuntimeException} which is thrown when a Publishing Template has an invalid XML for the Word ML template.
 *
 * @author Loren K. Ashley
 */

public class InvalidWordMlTemplateException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the XML parse error.
    *
    * @param templateIdentifier the identifier of the Artifact containing the invalid Word ML.
    * @param templateName the name of the Artifact containing the invalid Word ML.
    * @param parseError a message describing the XML parsing error.
    * @param badXml the XML that failed to parse.
    */

   public InvalidWordMlTemplateException(String templateIdentifier, String templateName, String parseError, String badXml) {
      super(InvalidWordMlTemplateException.buildMessage(templateIdentifier, templateName, parseError, badXml));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the XML parse error.
    *
    * @param templateIdentifier the identifier of the Artifact containing the invalid Word ML.
    * @param templateName the name of the Artifact containing the invalid Word ML.
    * @param parseError a message describing the XML parsing error.
    * @param badXml the XML that failed to parse.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public InvalidWordMlTemplateException(String templateIdentifier, String templateName, String parseError, String badXml, Throwable cause) {
      super(InvalidWordMlTemplateException.buildMessage(templateIdentifier, templateName, parseError, badXml));

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param templateIdentifier the identifier of the Artifact containing the invalid Word ML.
    * @param templateName the name of the Artifact containing the invalid Word ML.
    * @param parseError a message describing the XML parsing error.
    * @param badXml the XML that failed to parse.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(String templateIdentifier, String templateName, String parseError, String badXml) {

      //@formatter:off
      return
         new Message()
                .blank()
                .title( "Invalid Word ML XML Template." )
                .indentInc()
                .segment( "Template Identifier", templateIdentifier )
                .segment( "Template Name",       templateName       )
                .title( "Parse Error Follows" )
                .blank()
                .block( parseError )
                .blank()
                .title( "XML Follows" )
                .blank()
                .block( badXml )
                .blank()
                .toString();
      //@formatter:on
   }
}

/* EOF */
