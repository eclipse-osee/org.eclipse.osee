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

package org.eclipse.osee.orcs.core.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.orcs.core.internal.SetupPublishing;

/**
 * Defines the parameters for the creation of a Publishing Template.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplate {

   /**
    * A static factory method to load the content files and create a Publishing Template Artifact.
    *
    * @param publishingTemplatesSupplier a {@link Supplier} that provides the {@link PublishingTemplate} objects that
    * specify the Publishing Template Artifacts to be created.
    * @param publishingTemplateSetter an implementation of the {@link PublishingTemplateSetter} interface that creates
    * the OSEE Artifact and sets it's Attributes.
    * @param locationClass this class is used to generate the base path to the directory containing the files for the
    * bundle containing the specified class.
    * @param inOseeInf when <code>true</code>, the files will be loaded from the OSEE-INF folder under the base path.
    * When <code>false</code>, the files will be loaded from the "support" sub-directory of the folder containing the
    * class file for the specified class.
    * @return a {@link List} of the {@link PublishingTemplate} objects that were provided by the
    * {@link PublishingTemplateSupplier}.
    */

   //@formatter:off
   public static List<PublishingTemplate>
      load
         (
            Supplier<List<PublishingTemplate>> publishingTemplatesSupplier,
            PublishingTemplateSetter           publishingTemplateSetter,
            Class<?> locationClass,
            boolean inOseeInf
         )
   {
      return
         publishingTemplatesSupplier
            .get()
            .stream()
            .map
               (
                  (publishingTemplate) -> publishingTemplate.load
                                             (
                                                locationClass,
                                                inOseeInf,
                                                publishingTemplateSetter
                                              )
               )
            .collect( Collectors.toList() );
   }
   //@formatter:on

   /**
    * Saves the Publishing Template Manager's identifier for the publishing template.
    *
    * @implNote In general usage the identifier should not be generated from the Publishing Template's Artifact
    * identifier an knowledge of how the Publishing Template Manager generates publishing template identifiers. It is
    * done here for testing to ensure that the created publishing template is the one read back and not a possibly
    * conflicting publishing template.
    */

   private String identifier;

   /**
    * A list, possibly empty, of the values to use for the publishing template's match criteria attribute values.
    */

   private final List<PublishingTemplateMatchCriterion> matchCriteria;

   /**
    * The name for the publishing template artifact.
    */

   private final String name;

   /**
    * The publishing template artifact will be created as a child of this artifact.
    */

   private final ArtifactToken parentArtifactToken;

   /**
    * When specified, the file contents will be used as the value of the publishing template's renderer options
    * attribute.
    */

   private final Path rendererOptionsFileName;

   /**
    * When specified, the file contents will be used as the value of the publishing template's Word ML.
    */

   private final Path templateContentFileName;

   /**
    * Creates a new {@link PublishingTemplate} with the specified parameters.
    *
    * @param parentArtifactToken the hierarchical parent of the publishing template artifact.
    * @param name the name of the publishing template artifact.
    * @param rendererOptionsFilename the publishing template renderer options filename. The parameter maybe
    * <code>null</code>.
    * @param templateContentFileName the filename of a file containing the Word ML publishing template content. The
    * parameter maybe <code>null</code>.
    * @param matchCriteria a list of {@link PublishingTemplateMatchCriterion} for the publishing template. The parameter
    * maybe <code>null</code>.
    * @throws NullPointerException when either of the parameters <code>parentArtifactToken</code> or <code>name</code>
    * are <code>null</code>.
    */

   public PublishingTemplate(ArtifactToken parentArtifactToken, String name, String rendererOptionsFileName, String templateContentFileName, List<PublishingTemplateMatchCriterion> matchCriteria) {

      this.parentArtifactToken = Objects.requireNonNull(parentArtifactToken,
         "SetupPublishing.PublishingTemplate::new, paramter \"parentArtifactToken\" cannot be null.");

      this.name =
         Objects.requireNonNull(name, "SetupPublishing.PublishingTemplate::new, parameter \"name\" cannnot be null.");

      this.rendererOptionsFileName =
         Objects.nonNull(rendererOptionsFileName) ? Paths.get(rendererOptionsFileName) : null;

      this.templateContentFileName =
         Objects.nonNull(templateContentFileName) ? Paths.get(templateContentFileName) : null;

      this.matchCriteria = matchCriteria;
   }

   /**
    * Creates a {@link PublishingTemplateRequest} that can be used to obtain the Publishing Template from the Publishing
    * Template Manager.
    *
    * @return a {@link PublishingTemplateRequest} object.
    */

   public PublishingTemplateRequest getPublishingTemplateRequest() {

      var publishingTemplateRequest = new PublishingTemplateRequest(this.identifier);

      return publishingTemplateRequest;
   }

   /**
    * Loads the publishing template content and renderer options files and uses the {@link PublishingTemplateSetter} to
    * create the Publishing Template Artifact. The {@link PublishingTemplateSetter} implementation is different for when
    * generating Publishing Template Artifacts as part of the ORCS database initialization than it is when generating
    * Artifact as part of the JUnit test suite.
    *
    * @param locationClass this class is used to generate the base path to the directory containing the files for the
    * bundle containing the specified class.
    * @param inOseeInf when <code>true</code>, the files will be loaded from the OSEE-INF folder under the base path.
    * When <code>false</code>, the files will be loaded from the "support" sub-directory of the folder containing the
    * class file for the specified class.
    * @param publishingTemplateSetter an implementation of the {@link PublishingTemplateSetter} interface to create the
    * Publishing Template Artifact.
    * @throws NullPointerException when the parameter <code>locationClass</code> or
    * <code>publishingTemplateSetter</code> is <code>null</code>.
    */

   public PublishingTemplate load(Class<?> locationClass, boolean inOseeInf,
      PublishingTemplateSetter publishingTemplateSetter) {

      Objects.requireNonNull(locationClass, "PublishingTemplate::load, parameter \"locationClass\" cannot be null.");

      Objects.requireNonNull(locationClass,
         "PublishingTemplate::load, parameter \"publishingTemplateSetter\" cannot be null.");

      String templateContents = null;
      String rendererContents = null;

      //@formatter:off
      if (Objects.nonNull(this.rendererOptionsFileName)) {

         try {
            rendererContents =
               inOseeInf
                  ? OseeInf.getResourceContents(this.rendererOptionsFileName.toString(), locationClass)
                  : Lib.fileToString(locationClass, Paths.get( "support" ).resolve( this.rendererOptionsFileName ).toString());

         } catch( Exception e ) {
            throw
               new AssertionError
                      (
                         new Message()
                                .title( "PublishingTemplate::createPublishingTemplate, Failed to load renderer file." )
                                .indentInc()
                                .segment( "Location Class", locationClass.getCanonicalName() )
                                .segment( "In Osee Inf",    Boolean.valueOf( inOseeInf )     )
                                .segment( "File Path",      this.rendererOptionsFileName     )
                                .reasonFollows( e )
                                .toString(),
                         e
                      );
         }
      }

      if (Objects.nonNull(this.templateContentFileName)) {

         try {
            templateContents =
               inOseeInf
                  ? OseeInf.getResourceContents(this.templateContentFileName.toString(), SetupPublishing.class)
                  : Lib.fileToString(locationClass, Paths.get( "support" ).resolve( this.templateContentFileName ).toString() );

         } catch( Exception e ) {
            throw
               new AssertionError
                      (
                         new Message()
                                .title( "PublishingTemplate::createPublishingTemplate, Failed to load template file." )
                                .indentInc()
                                .segment( "Location Class", locationClass.getCanonicalName() )
                                .segment( "In Osee Inf",    Boolean.valueOf( inOseeInf )     )
                                .segment( "File Path",      this.templateContentFileName     )
                                .reasonFollows( e )
                                .toString(),
                         e
                      );
         }

         templateContents = templateContents.replace( "_TEMPLATE_NAME_", this.name );

         this.identifier =
            publishingTemplateSetter.set
               (
                  this.parentArtifactToken,
                  this.name,
                  templateContents,
                  rendererContents,
                  this.matchCriteria
                     .stream()
                     .map(PublishingTemplateMatchCriterion::getTemplateMatchCriteria)
                     .collect( Collectors.toCollection( ArrayList::new ) )
               );

      }
      //@formatter:on

      return this;
   }

}

/* EOF */