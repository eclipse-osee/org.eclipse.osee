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

package org.eclipse.osee.define.operations.publishing.templatemanager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.osee.define.api.publishing.templatemanager.InvalidRendererOptionsException;
import org.eclipse.osee.define.api.publishing.templatemanager.InvalidWordMlTemplateException;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplate;
import org.eclipse.osee.define.api.publishing.templatemanager.RendererOptions;
import org.eclipse.osee.define.api.publishing.templatemanager.TemplateContent;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.xml.sax.SAXParseException;

/**
 * An implementation of the {@link PublishingTemplateInternal} interface for OSEE Artifact Publishing Templates.
 *
 * @author Loren K. Ashley
 */

class ArtifactPublishingTemplate implements PublishingTemplateInternal {

   /**
    * Saves the {@link ArtifactReadable} containing the Publishing Template.
    */

   private final ArtifactReadable artifactReadable;

   /**
    * A {@link String} representation of the OSEE Artifact Identifier.
    */

   private final String identifier;

   /**
    * The OSEE Artifact name.
    */

   private final String name;

   /**
    * Saves the parsed JSON Renderer Options from the OSEE Artifact.
    */

   private final RendererOptions rendererOptions;

   /**
    * Saves the parsed Word ML XML from the OSEE Artifact.
    */

   private final TemplateContent templateContent;

   /**
    * Saves an unmodifiable list of the Publishing Template Artifact's TemplateMatchCritera attribute values.
    */

   private final List<String> templateMatchCriteria;

   /**
    * Saves the Whole Word Content extracted from the OSEE Artifact that is related as Supporting Info.
    */

   private String style;

   /**
    * Parses an {@link ArtifactReadable} into an {@link ArtifactPublishingTemplate}.
    *
    * @param artifactReadable the OSEE Artifact containing the Publishing Template.
    * @throws NullPointerException when the <code>artifactReadable</code> is <code>null</code>.
    * @throws InvalidRendererOptionsException when the Renderer Options in the OSEE Artifact do not parse.
    * @throws INvalidWordMlTemplateException when the Word ML XML does not parse.
    */

   ArtifactPublishingTemplate(ArtifactReadable artifactReadable) {

      this.artifactReadable = Objects.requireNonNull(artifactReadable);

      this.identifier = "AT-" + this.artifactReadable.getIdString();

      this.name = this.artifactReadable.getName();

      try {
         this.rendererOptions = RendererOptions.create(
            this.artifactReadable.getAttributeValuesAsString(CoreAttributeTypes.RendererOptions));
      } catch (InvalidRendererOptionsException e) {
         e.setPublishingTemplateInformation(this.identifier, this.name);
         throw e;
      }

      var templateXml = this.artifactReadable.getAttributeValuesAsString(CoreAttributeTypes.WholeWordContent);

      this.templateContent = new TemplateContent(templateXml);

      var documentOptional = this.templateContent.getTemplateXml();

      if (documentOptional.isEmpty()) {

         //@formatter:off
         var message =
            this.templateContent.getTemplateXmlParseError()
               .map
                  (
                     ( exception ) ->
                        ( exception instanceof SAXParseException )
                           ? new Message()
                                    .title( "Template Word ML XML parsing failed." )
                                    .indentInc()
                                    .title( exception.getMessage() )
                                    .indentInc()
                                    .segment( "LineNumber",    ((SAXParseException) exception).getLineNumber()   )
                                    .segment( "Column Number", ((SAXParseException) exception).getColumnNumber() )
                                    .toString()
                            : exception.getMessage()
                  )
               .orElse( "Template Word ML XML parsing failed." );
         //@formatter:on

         throw new InvalidWordMlTemplateException(this.identifier, this.name, message, templateXml);
      }

      this.templateMatchCriteria = this.buildTemplateMatchCriteriaList();

      try {
         var templateRelatedArtifacts =
            this.artifactReadable.getRelated(CoreRelationTypes.SupportingInfo_SupportingInfo).getList();

         this.style =
            (templateRelatedArtifacts.size() == 1) ? templateRelatedArtifacts.get(0).getAttributeValuesAsString(
               CoreAttributeTypes.WholeWordContent) : "";
      } catch (Exception e) {
         this.style = "";
      }

   }

   /**
    * Extracts the Publishing Template Artifact's {@link CoreAttributeTypes#TemplateMatchCriteria} attribute values and
    * builds an unmodifiable {@link List}.
    *
    * @return on success a {@link List} of the Publishing Template's match criteria; otherwise, an empty {@link List}.
    */

   private List<String> buildTemplateMatchCriteriaList() {
      try {
         var matchCriteriaObjectList =
            this.artifactReadable.getAttributeValues(CoreAttributeTypes.TemplateMatchCriteria);
         var matchCriteriaList =
            matchCriteriaObjectList.stream().map(Object::toString).collect(Collectors.toUnmodifiableList());
         return matchCriteriaList;
      } catch (Exception e) {
         return List.of();
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public PublishingTemplate getBean() {
      //@formatter:off
      return
         new PublishingTemplate
                (
                   this.identifier,
                   this.name,
                   this.rendererOptions,
                   this.style,
                   this.templateContent
                );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return this.identifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return this.name;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public RendererOptions getRendererOptions() {
      return this.rendererOptions;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getSafeName() {
      return this.artifactReadable.getSafeName();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getStyle() {
      return this.style;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public TemplateContent getTemplateContent() {
      return this.templateContent;
   }

   /**
    * {@inheritDoc}
    * <p>
    * The match criteria strings for Artifact Publishing Templates are extracted from the OSEE Artifact's
    * TemplateMatchCriteria attribute values.
    */

   @Override
   public List<String> getTemplateMatchCriteria() {
      return this.templateMatchCriteria;
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
         .title( "ArtifactPublishingTemplate" )
         .indentInc()
         .segment( "Class",                   this.getClass().getSimpleName() )
         .segment( "Artifact Readable",       this.artifactReadable           )
         .segment( "Identifier",              this.identifier                 )
         .segment( "Name",                    this.name                       )
         .segment( "Style",                   this.style                      )
         .segment( "Template Match Criteria", this.templateMatchCriteria      )
         .toMessage( this.rendererOptions )
         .toMessage( this.templateContent )
         ;
      //@formatter:off

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