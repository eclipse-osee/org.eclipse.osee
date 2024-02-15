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
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
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
    * A {@link Supplier} implementation that returns a saved string.
    */

   public static class StringSupplier implements Supplier<String> {

      /**
       * The string to be returned by the {@link Supplier}.
       */

      private final String string;

      /**
       * Creates the {@link Supplier} implementation and saves the string.
       *
       * @param string the {@link String} to be provided by the {@link Supplier}.
       */

      public StringSupplier(String string) {
         this.string = string;
      }

      /**
       * Implements the {@link Supplier#get} method.
       *
       * @return the saved string.
       */

      @Override
      public String get() {
         return this.string;
      }
   }

   /**
    * A {@link Supplier} implementation that reads a file from the OSEE INF directory and returns the file contents as a
    * {@link String}.
    */

   public static class FileSupplierOseeInf implements Supplier<String> {

      /**
       * Saves the path to the file to be read.
       */

      private final String pathString;

      /**
       * Creates the {@link Supplier} implementation and saves the relative path to the file from the OSEE INF
       * directory.
       *
       * @param pathString relative path to the file from the OSEE INF directory.
       */

      public FileSupplierOseeInf(String pathString) {
         this.pathString = pathString;
      }

      /**
       * Implements the {@link Supplier#get} method.
       *
       * @return the contents of the file specified to the constructor as a {@link String}.
       * @throws AssertionError when the file cannot be read.
       */

      @Override
      public String get() {

         final var filePathString = Paths.get(this.pathString).toString();

         try {

            final var contents = OseeInf.getResourceContents(filePathString, SetupPublishing.class);

            return contents;

         } catch (Exception e) {

            //@formatter:off
            var message =
               new Message()
                      .title( "PublishingTemplate.FileSupplierOseeInf::get, Failed to load file." )
                      .indentInc()
                      .segment( "File Path", filePathString )
                      .reasonFollows( e );

            throw new AssertionError( message.toString(), e );
            //@formatter:on
         }

      }
   }

   /**
    * A {@link Supplier} implementation that reads a file from a &quot;support&quot; directory of a JAR file and returns
    * the contents as a {@link String}.
    */

   public static class FileSupplierSupport implements Supplier<String> {

      /**
       * Save the relative path from the support directory to the file.
       */

      private final String pathString;

      /**
       * Save a {@link Class} reference to any class in the JAR file that contains the file to be read.
       */

      private final Class<?> locationClass;

      /**
       * Creates the {@link Supplier} implementation and saves the location of the file to be read.
       *
       * @param pathString the relative path from the &quot;support&quot; directory to the file.
       * @param locationClass any class in the the JAR file containing the file to be read.
       */

      public FileSupplierSupport(String pathString, Class<?> locationClass) {
         this.pathString = pathString;
         this.locationClass = locationClass;
      }

      /**
       * Implements the {@link Supplier#get} methods.
       *
       * @returns the contents of the file specified to the constructor as a {@link String}.
       * @throws AssertionError when the file cannot be read.
       */

      @Override
      public String get() {

         final var filePathString = Paths.get("support", this.pathString).toString();

         try {

            final var contents = Lib.fileToString(locationClass, filePathString);

            return contents;

         } catch (Exception e) {

            //@formatter:off
            var message =
               new Message()
                      .title( "PublishingTemplate.FileSupplierSupport::get, Failed to load file." )
                      .indentInc()
                      .segment( "File Path",      filePathString          )
                      .segment( "Location Class", locationClass.getName() )
                      .reasonFollows( e );

            throw new AssertionError( message.toString(), e );
            //@formatter:on
         }

      }
   }

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
            Class<?>                           locationClass,
            boolean                            inOseeInf
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
    * When specified, a supplier used to obtain the publish options JSON string.
    */

   private final @Nullable Supplier<String> publishOptionsSupplier;

   /**
    * When specified, a supplier used to obtain the publishing template content..
    */

   private final @Nullable Supplier<String> templateContentSupplier;

   /**
    * When specified, will be used to create attribute values for the Publishing Template's Publishing Template Content
    * By Format Map Entry attribute. Each attribute value is a JSON record of the form:
    *
    * <pre>
    *    {
    *      "key":   "&lt;format&gt;",
    *      "value": "&lt;template-content&gt;"
    *    }
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>key:</dt>
    * <dd>Is the format name specified by the {@link FormatIndicator}.</dd>
    * <dt>template-content:</code>
    * <dd>Is the JSON escaped contents of the file specified by the {@link Path}.</dd>
    * </dl>
    */

   private final List<PublishingTemplateContentMapEntry> publishingTemplateContentMapEntries;

   /**
    * Creates a new {@link PublishingTemplate} with the specified parameters.
    *
    * @param parentArtifactToken the hierarchical parent of the publishing template artifact.
    * @param name the name of the publishing template artifact.
    * @param publishOptionsSupplier a supplier used to obtain the publish options JSON string. The parameter may be
    * <code>null</code>.
    * @param templateContentSupplier a supplier used to obtain the publishing template content. The parameter may be
    * <code>null</code>. The supplied content will be added to the publishing template attribute
    * {@link CoreAttributeTypes#WholeWordContent}.
    * @param publishingTemplateContentMapEntries a list of {@link PublishingTemplateContentMapEntry} objects. The list
    * entries will be added to the publishing template attribute
    * {@link CoreAttributeTypes#PublishingTemplateContentByFormatMapEntry}.
    * @param matchCriteria a list of {@link PublishingTemplateMatchCriterion} for the publishing template. The parameter
    * maybe <code>null</code>.
    * @throws NullPointerException when either of the parameters <code>parentArtifactToken</code> or <code>name</code>
    * are <code>null</code>.
    */

   //@formatter:off
   public
      PublishingTemplate
         (
                      ArtifactToken                           parentArtifactToken,
                      String                                  name,
            @Nullable Supplier<String>                        publishOptionsSupplier,
            @Nullable Supplier<String>                        templateContentSupplier,
                      List<PublishingTemplateContentMapEntry> publishingTemplateContentMapEntries,
                      List<PublishingTemplateMatchCriterion>  matchCriteria
         ) {
   //@formatter:on

      this.parentArtifactToken = Objects.requireNonNull(parentArtifactToken,
         "SetupPublishing.PublishingTemplate::new, paramter \"parentArtifactToken\" cannot be null.");

      this.name =
         Objects.requireNonNull(name, "SetupPublishing.PublishingTemplate::new, parameter \"name\" cannnot be null.");

      this.publishOptionsSupplier = publishOptionsSupplier;

      this.templateContentSupplier = templateContentSupplier;

      this.publishingTemplateContentMapEntries = Objects.requireNonNull(publishingTemplateContentMapEntries);

      this.matchCriteria = matchCriteria;
   }

   /**
    * Gets the publishing template identifier as a {@link String}.
    *
    * @return the publishing template identifier as a string.
    */

   public String getIdentifier() {

      return this.identifier;
   }

   /**
    * Loads a publishing template configuration file from under either the OSEE-INF folder or the folder containing the
    * class file for a specified class.
    *
    * @param inOseeInf when <code>true</code>, the files will be loaded from the OSEE-INF folder under the base path.
    * When <code>false</code>, the files will be loaded from the "support" sub-directory of the folder containing the
    * class file for the specified class.
    * @param filePath a relative file path from the specified directory to the file to be loaded.
    * @param locationClass this class is used to generate the base path to the directory containing the files for the
    * project containing the specified class.
    * @return A {@link String} with the contents of the loaded file.
    */

   private String readFile(boolean inOseeInf, Path filePath, Class<?> locationClass) {

      try {
         //@formatter:off
         return
            inOseeInf
               ? OseeInf
                    .getResourceContents
                       (
                          filePath.toString(),
                          SetupPublishing.class
                       )
               : Lib
                    .fileToString
                       (
                          locationClass,
                          Paths
                             .get( "support" )
                             .resolve( filePath )
                             .toString()
                       );

      } catch( Exception e ) {

         var message =
            new Message()
                   .title( "PublishingTemplate::readFile, Failed to load file." )
                   .indentInc()
                   .segment( "In Osee Inf",    Boolean.valueOf( inOseeInf ) )
                   .segment( "File Path",      filePath                     );

         if( !inOseeInf ) {
            message.segment( "Location Class", locationClass.getName() );
         }

         message.reasonFollows( e );

         throw new AssertionError( message.toString(), e );
      }
      //@formatter:on
   }

   /**
    * Reads a file using the method {@link #readFile} replacing occurrences of "_TEMPLATE_NAME_" with the publishing
    * template name.
    *
    * @param inOseeInf when <code>true</code>, the files will be loaded from the OSEE-INF folder under the base path.
    * When <code>false</code>, the files will be loaded from the "support" sub-directory of the folder containing the
    * class file for the specified class.
    * @param locationClass this class is used to generate the base path to the directory containing the files for the
    * project containing the specified class.
    * @param filePath a relative file path from the specified directory to the file to be loaded.
    * @return A {@link String} with the contents of the loaded file.
    */

   private String readTemplateFile(boolean inOseeInf, Path filePath, Class<?> locationClass) {
      //@formatter:off
      return
         this
            .readFile( inOseeInf, filePath, locationClass )
            .replace( "_TEMPLATE_NAME_", this.name );
      //@formatter:on

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

   private PublishingTemplate load(Class<?> locationClass, boolean inOseeInf,
      PublishingTemplateSetter publishingTemplateSetter) {

      Objects.requireNonNull(locationClass);
      Objects.requireNonNull(publishingTemplateSetter);

      var templateContents = Conditions.getWhenNonNull(this.templateContentSupplier);

      var publishOptionsContents = Conditions.getWhenNonNull(this.publishOptionsSupplier);

      //@formatter:off
      var publishingTemplateContentMapEntries =
         this.publishingTemplateContentMapEntries
            .stream()
            .map
               (
                  ( publishingTemplateContentMapEntry ) -> Map.entry
                                                              (
                                                                 publishingTemplateContentMapEntry.getFormatIndicator().getFormatName(),
                                                                 this.readTemplateFile
                                                                    (
                                                                       inOseeInf,
                                                                       publishingTemplateContentMapEntry.getTemplateContentPath(),
                                                                       locationClass
                                                                    )
                                                              )
               )
            .collect( Collectors.toList() );

      var matchCriteria =
         this.matchCriteria
         .stream()
         .map( PublishingTemplateMatchCriterion::getTemplateMatchCriteria )
         .collect( Collectors.toCollection( ArrayList::new ) );

      this.identifier =
         publishingTemplateSetter
            .set
               (
                  this.parentArtifactToken,
                  this.name,
                  templateContents,
                  publishOptionsContents,
                  publishingTemplateContentMapEntries,
                  matchCriteria
               );

         //@formatter:on

      return this;
   }

}

/* EOF */