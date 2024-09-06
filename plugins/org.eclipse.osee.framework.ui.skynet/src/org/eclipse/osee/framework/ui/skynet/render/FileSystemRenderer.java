/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.FilenameFormat;
import org.eclipse.osee.framework.core.publishing.FilenameSpecification;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.RendererUtil;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 * @author Loren K. Ashley
 */

public abstract class FileSystemRenderer extends DefaultArtifactRenderer {

   private static final ArtifactFileMonitor monitor = new ArtifactFileMonitor();

   /**
    * {@link IRenderer} implementations extending {@link FileSystemRenderer} and using the method
    * {@link #getBaseApplicabilityRating} must set this member to the artifact types to test for according to the the
    * member {@link #artifactTypesAreRequired}. When {@link #artifactTypesAreRequired} is <code>true</code> the artifact
    * being rendered is required to be of one of the artifact types in {@link #applicabilityTestArtifactTypes}. When
    * {@link #artifactTypesAreRequired} is <code>false</code> the artifact being rendered must not be of one of the
    * artifact types in {@link #applicabilityTestArtifactTypes}.
    */

   protected ArtifactTypeToken[] applicabilityTestArtifactTypes;

   /**
    * {@link IRenderer} implementations extending {@link FileSystemRenderer} and using the method
    * {@link #getBaseApplicabilityRating} must set this member to the test sense for the artifact types in
    * {@link #applicabilityTestArtifactTypes} as follows:
    * <dl>
    * <dt><code>true</code></dt>
    * <dd>to require the artifact being rendered is of an artifact type in {@link #applicabilityTestArtifactTypes}.</dd>
    * <dt><code>false</code></dt>
    * <dd>to require the artifact being rendered is not of an artifact type in
    * {@link #applicabilityTestArtifactTypes}.</dd>
    * </dl>
    */

   protected boolean artifactTypesAreRequired;

   /**
    * The default file extension for the primary artifact type handled by the renderer.
    *
    * @implSpec {@link IRenderer} implementations that extend the {@link FileSystemRenderer} class that use the method
    * {@link #getBaseApplicabilityRating} must set a value for this member when constructed.
    */

   protected String defaultFileExtension;

   /**
    * Amount to adjust the renderer applicability rating when the artifact being rendered is not of the type specified
    * by {@link #rendererArtifactTypeToken}.
    *
    * @implSpec {@link IRenderer} implementations that extend the {@link FileSystemRenderer} class that use the method
    * {@link #getBaseApplicabilityRating} must set a value for this member when constructed.
    */

   protected int notRendererArtifactTypeDelta;

   /**
    * The value of the optional {@link RendererOption#OPEN_OPTION} that will boost the renderer's applicability rating
    * for {@link PresentationType#DIFF}.
    *
    * @implSpec {@link IRenderer} implementations that extend the {@link FileSystemRenderer} class that use the method
    * {@link #getBaseApplicabilityRating} must set a value for this member when constructed.
    */

   protected String openInRendererOptionValue;

   /**
    * The {@link PresentationType}s the renderer is not applicable for.
    *
    * @implSpec {@link IRenderer} implementations that extend the {@link FileSystemRenderer} class that use the method
    * {@link #getBaseApplicabilityRating} must set a value for this member when constructed.
    */

   protected PresentationType[] presentationTypeKnockOuts;

   /**
    * The attribute types that the renderer is not applicable for when the presentation type is
    * {@link PresentationType#PREVIEW}.
    *
    * @implSpec {@link IRenderer} implementations that extend the {@link FileSystemRenderer} class that use the method
    * {@link #getBaseApplicabilityRating} must set a value for this member when constructed.
    */

   protected AttributeTypeToken[] previewAttributeTypeKnockOuts;

   /**
    * This member is expected to be set by derived classes to indicate the publishing format the renderer supports.
    */

   protected FormatIndicator publishingFormat;

   /**
    * The main type of artifact the renderer is for.
    *
    * @implSpec {@link IRenderer} implementations that extend the {@link FileSystemRenderer} class that use the method
    * {@link #getBaseApplicabilityRating} must set a value for this member when constructed.
    */

   protected ArtifactTypeToken rendererArtifactTypeToken;

   /**
    * The expected main content attribute of the artifact being rendered.
    *
    * @implSpec {@link IRenderer} implementations that extend the {@link FileSystemRenderer} class that use the method
    * {@link #getBaseApplicabilityRating} must set a value for this member when constructed.
    */

   protected AttributeTypeToken rendererContentAttributeType;

   /**
    * If a renderer sub-thread's outer exception handler catches an exception, is is saved in this class global.
    */

   private Throwable rendererException;

   /**
    * If a renderer sub-thread fails to close the {@link PipedOutputStream} in the threads outer exception handler, the
    * pipe closing exception is saved in this class global.
    */

   private Throwable rendererPipeCloseException;

   /**
    * Saves a handle to the renderer sub-thread. A sub-thread is only used when the renderer has set the option
    * {@link RendererOption#CLIENT_RENDERER_CAN_STREAM} to <code>true</code>.
    */

   private Thread rendererThread;

   public FileSystemRenderer() {
      this(null);
   }

   public FileSystemRenderer(RendererMap rendererOptions) {
      super(rendererOptions);
      this.rendererThread = null;
      this.rendererException = null;
      this.rendererPipeCloseException = null;
   }

   /**
    * Checks for exceptions from a renderer sub-thread. The following checks are done:
    * <dl>
    * <dt>{@link #rendererException} and {@link #rendererPipeCloseException} are both non-<code>null</code>:</dt>
    * <dd>A new {@link OseeCoreException} wrapping the {@link #rendererException} and describing both exceptions is
    * thrown.</dd>
    * <dt>Just {@link #rendererException} is non-<code>null</code>:</dt>
    * <dd>If the {@link #rendererException} is a {@link RuntimeException} it is rethrown; otherwise, it is wrapped in an
    * {@link OseeCoreException} and thrown.</dd>
    * <dt>The renderer sub-thread is still alive:</dt>
    * <dd>An {@link OseeCoreException} is thrown describing the unexpectedly still living thread.</dd>
    * </dl>
    *
    * @throws OseeCoreException to wrap exceptions thrown from the sub-thread or when the sub-thread is unexpectedly
    * still alive.
    * @throws RuntimeException when the sub-thread threw an uncaught {@link RuntimeException}.
    */

   void checkRenderThreadExceptions() {

      if (Objects.nonNull(this.rendererException) && Objects.nonNull(this.rendererPipeCloseException)) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "FileSystemRender, Render sub-thread threw an exception and failed to close the pipe." )
                             .indentInc()
                             .segment( "Render", this.getName() )
                             .title( "Render Exception" )
                             .reasonFollows( this.rendererException )
                             .title( "Pipe Closing Execption" )
                             .reasonFollows( this.rendererPipeCloseException )
                             .toString(),
                             this.rendererException
                   );
         //@formatter:on
      }

      if (Objects.nonNull(this.rendererException)) {

         if (this.rendererException instanceof RuntimeException) {
            throw (RuntimeException) this.rendererException;
         }

         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "FileSystemRender, Render sub-thread threw an uncought checked exception." )
                             .indentInc()
                             .segment( "Render", this.getName() )
                             .title( "Render Exception" )
                             .reasonFollows( this.rendererException )
                             .toString()
                   );
         //@formatter:on
      }

      try {
         this.rendererThread.join(1000);
      } catch (Exception e) {
         //skip
      }

      if (this.rendererThread.isAlive()) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                         .title( "FileSystemRender, Render sub-thread is unexpectedly still alive." )
                         .indentInc()
                         .segment( "Render",    this.getName()              )
                         .segment( "Thread Id", this.rendererThread.getId() )
                         .toString()
                   );
         //@formatter:on
      }

      this.rendererException = null;
      this.rendererPipeCloseException = null;
      this.rendererThread = null;
   }

   public @NonNull IFile copyToNewFile(Artifact artifact, @NonNull PresentationType presentationType,
      @NonNull IFile file) {

      final var safePresentationType = Conditions.requireNonNull(presentationType, "presentationType");

      final var safeIFile = Conditions.requireNonNull(file, "file");

      /*
       * Make the artifact into a list
       */

      var artifacts = this.setRendererOptions(List.of(artifact));

      /*
       * Copy the file contents
       */

      try (var inputStream = safeIFile.getContents()) {
         //@formatter:off
         return
            this
               .renderToFileInternal       /* <presentation-folder> "/" <branch-name> "/" <artifact-name> "-" <artifact-id> [ "-" <transaction-id> ] */
                  (
                     artifacts,            /* List<Artifact>   artifacts        */
                     safePresentationType, /* PresentationType presentationType */
                     null,                 /* String           pathPrefix       */
                     inputStream           /* InputStream      inputStream      */
                  )
               .orElseThrow();             /* Optional will be empty when exporting a file. Exports should not get here. */
         //@formatter:on
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                  (
                     new Message()
                            .title( "FileSystemRenderer::copyToNewFile, Failed to copy file." )
                            .indentInc()
                            .segment( "Artifact",         artifact             )
                            .segment( "PresentationType", safePresentationType )
                            .segment( "File", safeIFile.getFullPath().toOSString()  )
                            .reasonFollowsWithTrace( e )
                            .toString(),
                     e
                  );
      }
   }

   /**
    * Gets the the program icon for the application associated with the type for the <code>artifact</code> or the
    * default icon for the artifact type.
    *
    * @param artifact the {@link Artifact} to get an application icon for.
    * @return When a program icon is located for the {@link Artifact} an {@link Optional} containing the icon
    * {@link ImageDescriptor}; otherwise, and empty {@link Optional}.
    */

   protected @NonNull Optional<ImageDescriptor> getArtifactBasedImageDescriptor(@Nullable Artifact artifact) {

      if (Objects.isNull(artifact)) {
         return Optional.empty();
      }

      var associatedExtension = this.getAssociatedExtension(artifact);

      if (Objects.isNull(associatedExtension)) {
         return Optional.empty();
      }

      //@formatter:off
      return
         ImageManager
            .safeGetProgramImageDescriptor( associatedExtension )
            .or( () -> ArtifactImageManager.getImageDescriptorNoDefault( artifact ) );
      //@formatter:on
   }

   @Override
   protected @NonNull MenuCmdDef getArtifactBasedMenuCommand(@NonNull MenuCmdDef menuCmdDef,
      @Nullable Artifact artifact) {

      Objects.requireNonNull(menuCmdDef);

      //@formatter:off
      return
         this
            .getArtifactBasedImageDescriptor( artifact )
            .map( menuCmdDef::newInstance )
            .orElse( menuCmdDef );
      //@formatter:on
   }

   /**
    * For the specified {@link Artifact}, gets the file system extension for the type of file that would likely be used
    * for the type of data stored in the {@link Artifact} main content {@link Attribute}.
    *
    * @implSpec When the {@link Artifact} is <code>null</code> or empty, the implementation must return the default file
    * system extension.
    * @param artifact the {@link Artifact} to analyze.
    * @return the file system extension.
    */

   public String getAssociatedExtension(Artifact artifact) {

      var defaultAssociatedExtension = this.getDefaultAssociatedExtension();

      try {
         //@formatter:off
         return
            Objects.nonNull( artifact )
               ? artifact.getSoleAttributeValue(CoreAttributeTypes.Extension, defaultAssociatedExtension )
               : defaultAssociatedExtension;
         //@formatter:on
      } catch (Exception e) {
         return defaultAssociatedExtension;
      }
   }

   /**
    * For the specified {@link List} of {@link Artifact} objects, gets the file system extension for the type of file
    * that would likely be used for the type of data stored in the {@link Artifact} main content {@link Attribute} of
    * the first {@link Artifact} on the {@link List}.
    *
    * @implSPec When the {@link List} is <code>null</code> or empty, the implementation must return the default file
    * system extension.
    * @param artifact the {@link Artifact} to analyze.
    * @return the file system extension.
    */

   public String getAssociatedExtension(List<Artifact> artifacts) {
      //@formatter:off
      return
         ( Objects.nonNull( artifacts ) && !artifacts.isEmpty() )
            ? this.getAssociatedExtension( artifacts.get( 0 ) )
            : this.getDefaultAssociatedExtension();
       //@formatter:on
   }

   public abstract Program getAssociatedProgram(Artifact artifact);

   /**
    * Generates a Renderer applicability rating for an artifact, presentation type, and set of renderer options.
    *
    * @implNote This method is intended to be called by {@link IRenderer} implementations that extend the
    * {@link FileSystemRenderer} class from their override of the method
    * {@link DefaultArtifactRenderer#getApplicabilityRating}. This method is not provided as an override of
    * {@link DefaultArtifactRenderer#getApplicabilityRating} so that implementations that do not override the method
    * {@link DefaultArtifactRenderer#getApplicabilityRating} will generate their applicability rating with the default
    * method.
    * @implSpec {@link IRenderer} implementations that use this method must provide values for the following members
    * during construction:
    * <ul>
    * <li>{@link #presentationTypeKnockOuts}</li>
    * <li>{@link #previewAttributeTypeKnockOuts}</li>
    * <li>{@link #artifactTypesAreRequired}</li>
    * <li>{@link #applicabilityTestArtifactTypes}</li>
    * <li>{@link #defaultFileExtension}</li>
    * <li>{@link #openInRendererOptionValue}</li>
    * <li>{@link #rendererArtifactTypeToken}</li>
    * <li>{@link #notRendererArtifactTypeDelta}</li>
    * <li>{@link #rendererContentAttributeType}</li>
    * </ul>
    * @param presentationType the type of presentation to be generated.
    * @param artifact the artifact the presentation is to be generated for.
    * @param rendererOptions the rendering options for the presentation.
    * @return an integer applicability rating for the renderer.
    */

   //@formatter:off
   protected int
      getBaseApplicabilityRating
         (
            PresentationType     presentationType,
            Artifact             artifact,
            RendererMap          rendererOptions
         ) {

      var rating =
         this.getBaseApplicabilityRatingInternal
            (
               presentationType,
               artifact,
               rendererOptions
            );

      if( !artifact.isOfType( rendererArtifactTypeToken ) ) {
         rating += notRendererArtifactTypeDelta;
      }

      return rating;

   }
   //@formatter:on

   /**
    * Generates a Renderer applicability rating for an artifact, presentation type, and set of renderer options.
    *
    * @param presentationType the type of presentation to be generated.
    * @param artifact the artifact the presentation is to be generated for.
    * @param rendererOptions the rendering options for the presentation.
    * @param rendererContentAttributeType the expected main content attribute of the artifact being rendered.
    * @return an integer applicability rating for the renderer.
    */

   //@formatter:off
   private int
      getBaseApplicabilityRatingInternal
         (
            PresentationType     presentationType,
            Artifact             artifact,
            RendererMap          rendererOptions
         ) {

     /*
      * Knock Outs, NO_MATCH (-1)
      */

     if(    presentationType.isOneOf( this.presentationTypeKnockOuts )
         || ( this.artifactTypesAreRequired ^ artifact.isOfType( this.applicabilityTestArtifactTypes ) )
         || (
                  rendererOptions.isRendererOptionSet( RendererOption.PUBLISHING_FORMAT )
               && !rendererOptions.getRendererOptionValue( RendererOption.PUBLISHING_FORMAT ).equals( this.publishingFormat )
            )
       ) {
        return IRenderer.NO_MATCH;
     }


     /*
      * SPECIALIZED_KEY_MATCH (70)
      */

     /*
      * SPECIALIZED_MATCH (60)
      */

     /*
      * PRESENTATION_TYPE_OPTION_MATCH (55)
      */

     if(
             presentationType.matches
                (
                   PresentationType.DIFF,
                   PresentationType.PREVIEW
                )
         && !artifact.isAttributeTypeValid
                (
                   rendererContentAttributeType
                )
       )
     {
        switch( presentationType ) {
           case DIFF:
              if(
                     rendererOptions.isRendererOptionSet( RendererOption.OPEN_OPTION )
                  && this.openInRendererOptionValue.equals( rendererOptions.getRendererOptionValue( RendererOption.OPEN_OPTION ) )
                ) {
                 return IRenderer.PRESENTATION_TYPE_OPTION_MATCH;
              }
              break;
           case PREVIEW:
              if(
                    artifact.areAllAttributeTypesInvalid( this.previewAttributeTypeKnockOuts )
                ) {
                     return IRenderer.PRESENTATION_TYPE_OPTION_MATCH;
                  }
              break;
           default:
        }
     }

     /*
      * PRESENTATION_SUB_TYPE_MATCH (50)
      */

     if(
            presentationType.matches
               (
                  PresentationType.DEFAULT_OPEN,
                  PresentationType.PREVIEW
               )
         && artifact.isAttributeTypeValid( this.rendererContentAttributeType )
         && ( artifact.getAttributeCount( this.rendererContentAttributeType ) > 0 )
       )  {
        return IRenderer.PRESENTATION_SUBTYPE_MATCH;
     }

     if(
            !presentationType.matches
                (
                   PresentationType.DEFAULT_OPEN,
                   PresentationType.PREVIEW
                )
         && artifact.isAttributeTypeValid( this.rendererContentAttributeType )
       ) {
        return IRenderer.PRESENTATION_SUBTYPE_MATCH;
     }

     if(
        presentationType.matches
           (
              PresentationType.DIFF
           )
      )
    {
       try {
          if(
               artifact
                  .getSoleAttributeValueAsString
                     (
                        CoreAttributeTypes.Extension,
                        Strings.EMPTY_STRING
                     )
                  .contains( this.defaultFileExtension )
            ) {
             return IRenderer.PRESENTATION_SUBTYPE_MATCH;
          }
       } catch( Exception e ) {
          //don't match
       }
    }

     /*
      * PRESENTATION_TYPE (40)
      */

     /*
      * SUBTYPE_TYPE_MATCH (30)
      */

     if(
             presentationType.matches
                (
                   PresentationType.DEFAULT_OPEN,
                   PresentationType.PREVIEW
                )
          && artifact.isAttributeTypeValid
                (
                   this.rendererContentAttributeType
                )
          && ( artifact.getAttributeCount( this.rendererContentAttributeType ) == 0 )
       ) {
        return IRenderer.SUBTYPE_TYPE_MATCH;
     }

     /*
      * GENERAL_MATCH (10)
      */

     /*
      * BASE_MATCH (5)
      */

     if(
             presentationType.matches
                (
                   PresentationType.DIFF
                )
         && !artifact.isAttributeTypeValid
                (
                   this.rendererContentAttributeType
                )
       ) {
        return IRenderer.BASE_MATCH;
     }

     /*
      * Anything Else, NO_MATCH (-1)
      */

     return IRenderer.NO_MATCH;
     //@formatter:on

   }

   /**
    * Gets the file system extension for the type of file that would likely be used for the type of data stored in the
    * {@link Artifact} main content {@link Attribute} for the most common type of {@link Artifact} processed by the
    * {@link IRenderer} implementation.
    *
    * @implSpec Implementations must always return a non-<code>null</code> {@link String}.
    * @implSpec Implementations must not throw exceptions.
    * @return the default file system extension.
    */

   public abstract String getDefaultAssociatedExtension();

   public abstract InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts);

   protected abstract IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch,
      PresentationType presentationType);

   /**
    * {@inheritDoc}
    * <p>
    *
    * @implNote When the {@link List} of {@link Artifact} is <code>null</code> or empty, processing still needs to
    * occur. The {@link WordTemplateRenderer} needs to produce an empty Word document for artifact comparisons where
    * there is a base artifact but not a compare to artifact.
    */

   @Override
   public void open(@Nullable List<Artifact> artifacts, @NonNull PresentationType presentationType) {

      var safePresentationType = Conditions.requireNonNull(presentationType, "presentationType");

      /*
       * This check is so that a double click will open the artifact in the configured default editor.
       */

      if (safePresentationType.matches(PresentationType.DEFAULT_OPEN)) {
         safePresentationType = PresentationType.PREVIEW;
      }

      try {

         /*
          * Get the render output and write the content file to the workspace. When previewing the returned optional
          * will contain the IFile handle to the rendered file in the workspace. When exporting the returned optional
          * will be empty.
          */

         var contentFileOptional = this.renderToFile(artifacts, safePresentationType, null);

         /*
          * If there were no artifacts or content file, do not display the empty content to the user.
          */

         if (Objects.isNull(artifacts) || artifacts.isEmpty() || contentFileOptional.isEmpty()) {
            return;
         }

         /*
          * Display renderer content to the user.
          */

         var program = this.getAssociatedProgram(artifacts.get(0));

         RenderingUtil.displayDocument(safePresentationType, program, contentFileOptional.get());

      } catch (Exception e) {

         var branchToken = artifacts.get(0).getBranchToken();

         //@formatter:off
         final var monitor =
            this.isRendererOptionSet( RendererOption.PROGRESS_MONITOR )
               ? (IProgressMonitor) this.getRendererOptionValue( RendererOption.PROGRESS_MONITOR )
               : null;

         var message =
            RenderingUtil.displayErrorDocument
               (
                  this,
                  presentationType,
                  monitor,
                  branchToken,
                  artifacts,
                  e.getMessage()
               );
         //@formatter:on

         throw new OseeCoreException(message, e);
      }
   }

   /**
    * Saves the renderer's output to the content file and setups file monitoring. The <code>artifacts</code> are
    * verified to all be from the same branch. The abstract method {@link #getRenderInputStream} is called to get the
    * {@link IRenderer} implementation's output. The workspace sub-directories are created as necessary. When the
    * {@link PresentationType} is {@link PresentationType#PREVIEW} the file monitoring is set for read only; otherwise,
    * the file is monitored for edit.
    * <p>
    * When the renderer option {@link RendererOption#CLIENT_RENDERER_CAN_STREAM} is set to <code>true</code>, the call
    * back to the abstract method {@link #getRenderInputStream} is run in a sub-thread connected with a
    * {@link PipedOutputStream} and {@link PipedInputStream} pair.
    * <p>
    * Filenames are generated as follows:
    * <dl>
    * <dt>pathPrefix is null && artifact list length > 1:</dt>
    * <dd>&lt;presentation-folder&gt; "/" &lt;branch-name&gt; "/" ( "artifacts", &lt;artifacts-length&gt; )</dd>
    * <dt>pathPrefix is non-null && artifact list length > 1:</dt>
    * <dd>&lt;presentation-folder&gt; "/" &lt;path-prefix&gt; "/" ( &lt;branch-name&gt;, "artifacts",
    * &lt;artifacts-length&gt; )</dd>
    * <dt>pathPrefix is null && artifact list length == 1 && artifact is non-null:</dt>
    * <dd>&lt;presentation-folder&gt; "/" &lt;branch-name&gt; "/" ( &lt;artifact-name&gt;, &lt;artifact-id&gt; [,
    * &lt;transaction-id&gt;] )</dd>
    * <dt>pathPrefix is non-null && pathPrefix != branchName && artifact list length == 1 && artifact is non-null:</dt>
    * <dd>&lt;presentation-folder&gt; "/" &lt;path-prefix&gt; "/" ( &lt;branch-name&gt;, &lt;artifact-name&gt;,
    * &lt;artifact-id&gt; [, &lt;transaction-id&gt;] )</dd>
    * <dt>pathPrefix is non-null && pathPrefix == branchName && artifact list length == 1 && artifact is non-null:</dt>
    * <dd>&lt;presentation-folder&gt; "/" &lt;path-prefix&gt; "/" ( &lt;artifact-name&gt;, &lt;artifact-id&gt; [,
    * &lt;transaction-id&gt;] )</dd>
    * <dt>pathPrefix is null && ( artifact list length == 0 || artifact list contains one null artifact):</dt>
    * <dd>&lt;presentation-folder&gt; "/" ( "artifacts", "0" )</dt>
    * <dt>pathPrefix is non-null && ( artifact list length == 0 || artifact list contains one null artifact):</dt>
    * <dd>&lt;presentation-folder&gt; "/" &lt;path-prefix&gt; "/" ( "artifacts", "0" )</dd>
    * </dl>
    *
    * @implNode This is a provided utility method to classes extending this class. It is generally called from overrides
    * of the {@link #open} method.
    * @param artifacts the artifacts that were rendered.
    * @param presentationType the type of presentation being made.
    * @return a {@link IFile} handle to the workspace file that was written with the rendering content or
    * <code>null</code> when the input parameters are <code>null</code> or empty.
    * @throws IllegalArgumentException when the provided artifacts are not from the same branch.
    */

   public Optional<IFile> renderToFile(List<Artifact> artifacts, PresentationType presentationType, String pathPrefix) {

      Objects.requireNonNull(presentationType,
         "FileSystemRender::renderToFile, parameter \"presentationType\" cannot be null.");

      /*
       * Get the branch token and ensure all artifacts are from the same branch. artifactsNoNull will be an empty list
       * if the input artifact list was null, empty, or only has null entries.
       */

      var artifactsNoNulls = this.setRendererOptions(artifacts);

      /*
       * Get the Renderer's output
       */

      if ((Boolean) this.getRendererOptionValue(RendererOption.CLIENT_RENDERER_CAN_STREAM)) {

         /*
          * It's a client side renderer that supports streaming (writing to a PipedOutputStream).
          */

         try (var pipedInputStream = new PipedInputStream();
            var pipedOutputStream = new PipedOutputStream(pipedInputStream);) {

            this.setRendererOption(RendererOption.OUTPUT_STREAM, pipedOutputStream);
            var renderer = this;

            /*
             * Create a sub-thread for the client streaming render. Running in a separate thread the renderer will write
             * its output to the output stream provided to it via the renderer options. The input stream is attached to
             * the output stream before the renderer is started.
             */

            this.rendererThread = new Thread("Renderer") {

               @Override
               public void run() {
                  try (var is = renderer.getRenderInputStream(presentationType, artifacts);) {

                  } catch (Exception rendererException) {

                     /*
                      * The renderer threw an exception. Shut down the piped streams.
                      */

                     try {

                        pipedOutputStream.close();

                     } catch (IOException pipeCloseException) {

                        renderer.rendererPipeCloseException = pipeCloseException;
                     }

                     /*
                      * Save the exception.
                      */

                     renderer.rendererException = rendererException;
                  }

               }
            };

            this.rendererException = null;
            this.rendererPipeCloseException = null;
            rendererThread.start();

            /*
             * Save renderer output to the content file and setup file monitoring
             */

            //@formatter:off
            return
               renderToFileInternal
                  (
                     artifactsNoNulls,  /* List<Artifact>   artifacts        */
                     presentationType,  /* PresentationType presentationType */
                     pathPrefix,        /* String           pathPrefix       */
                     pipedInputStream   /* InputStream      inputStream      */
                  );
            //@formatter:on
         } catch (Exception e) {
            //@formatter:off
            throw
               new OseeCoreException
                      (
                         new Message()
                                .title( "FileSystemRenderer::renderToFile, Failed to get renderer input stream." )
                                .indentInc()
                                .segment( "Renderer", this.getName() )
                                .reasonFollowsWithTrace( e )
                                .toString(),
                         e
                      );
            //@formatter:on
         }

      } else {

         /*
          * It's a server side render that provides the input stream from the REST API to read from. Or, it's a client
          * side render that wrote to a buffer and then provided an input stream that reads from the buffer.
          */

         try (var inputStream = this.getRenderInputStream(presentationType, artifacts); /* abstract */) {

            //@formatter:off

            return
               renderToFileInternal
                  (
                     artifactsNoNulls,     /* List<Artifact>   artifacts        */
                     presentationType,     /* PresentationType presentationType */
                     pathPrefix,           /* String           pathPrefix       */
                     inputStream           /* InputStream      inputStream      */
                  );
            //@formatter:on

         } catch (Exception e) {
            //@formatter:off
               throw
                  new OseeCoreException
                         (
                            new Message()
                                   .title( "FileSystemRenderer::renderToFile, Failed to renderer file for non-streaming renderer." )
                                   .indentInc()
                                   .segment( "Renderer", this.getName() )
                                   .reasonFollowsWithTrace( e )
                                   .toString(),
                            e
                         );
               //@formatter:on
         }
      }

   }

   /**
    * Writes the Renderer's output to a content file.
    * <p>
    * The output path is determined in the following order of precedence:
    * <dl>
    * <dt>The renderer option {@link RendererOption#OUTPUT_PATH} specifies a path,</dt>
    * <dd>it will be used. An output path specified by renderer options might not be a location in the user's
    * workspace.</dd>
    * <dt>The parameter <code>pathPrefix</code> is non-<code>null</code>,</dt>
    * <dd>the <code>pathPrefix</code> will be used as the location under the workspace presentation folder to write the
    * output file. The path specified by <code>pathPrefix</code> will be &quot;cleaned&quot; using
    * {@link RendererUtil#makePath}.</dd>
    * <dt>Otherwise,</dt>
    * <dd>The name of the branch containing the artifacts being rendered will be used as the location under the
    * workspace presentation folder to write the output file. The branch name will be &quot;cleaned&quot; using
    * {@link RendererUtil#makePath}.</dd>
    * </dl>
    * When the renderer options specify an output path with , it will be used.
    * <p>
    * The branch name will be used as the first filename segment when the {@link RendererOption#FILENAME_FORMAT} is
    * {@link FilenameFormat#PREVIEW} and the parameter <code>pathPrefix</code> is not equal to the value of the
    * {@link RendererOption#BRANCH_NAME}.
    * <p>
    *
    * @param artifacts the artifacts rendered. Used to determine the file extension, generate the filename, and for the
    * update monitor to save a modified content file back to the artifacts.
    * @param presentationType the type of presentation. Used for to located the workspace presentation folder, filename
    * generation, and to determine the type of file monitoring.
    * @param pathPrefix split with both UNIX and Windows path separators; and each segment is used as sub-folder under
    * the workspace presentation folder.
    * @param renderInputStream the output from the {@link IRenderer} implementation.
    * @return an {@link IFile} handle to the file the render's output was written to.
    * @throws OseeCoreException when unable to create the {@link IFile} handle or an error occurs writing the file.
    */

   private Optional<IFile> renderToFileInternal(List<Artifact> artifacts, PresentationType presentationType,
      String pathPrefix, InputStream renderInputStream) {

      /*
       * Get renderer options
       */

      final var branchId = (BranchId) this.getRendererOptionValue(RendererOption.BRANCH);
      final var branchName = (String) this.getRendererOptionValue(RendererOption.BRANCH_NAME);
      final var filenameFormat = (FilenameFormat) this.getRendererOptionValue(RendererOption.FILENAME_FORMAT);

      //@formatter:off
      final var pathRoot =
         this.isRendererOptionSet( RendererOption.OUTPUT_PATH )
            ? (Path) this.getRendererOptionValue( RendererOption.OUTPUT_PATH )
            : ( pathPrefix != null )
                   ? RendererUtil.makePath( pathPrefix )
                   : RendererUtil.makePath( branchName );
      //@formatter:on

      /*
       * Determine the filename extension from the artifacts
       */

      final var extension = this.getAssociatedExtension(artifacts);

      /*
       * Determine if the branch name should be the first filename segment
       */

      //@formatter:off
      final var branchNameFilenameSegment =
         filenameFormat.isPreview() && Objects.nonNull( pathPrefix ) && !pathPrefix.equals( branchName )
            ? branchName
            : null;
      //@formatter:on

      /*
       * Create the filename segments according to the filename format.
       */

      //@formatter:off
      final var filenameSegments =
         RenderingUtil.getFileNameSegmentsFromArtifacts
            (
               filenameFormat,
               presentationType,
               branchNameFilenameSegment,
               artifacts
            );

      final var filenameSpecification =
         new FilenameSpecification(Strings.EMPTY_STRING, filenameFormat, extension, filenameSegments);

      switch (filenameFormat) {

         case EXPORT: {

            RendererUtil
               .getRenderFile( pathRoot, filenameSpecification )
               .map
                  (
                     ( contentFile ) ->
                     {
                        try {
                           Lib.inputStreamToFile( renderInputStream, contentFile );
                        } catch( Exception e ) {

                           throw
                              new OseeCoreException
                                     (
                                        new Message()
                                               .title( "FileSystemRenderer::renderToFileInternal, Failed to write export file." )
                                               .indentInc()
                                               .segment( "Renderer",    this.getName() )
                                               .segment( "Branch Name", branchName     )
                                               .segment( "Branch Id",   branchId       )
                                               .segmentIndexed( "Artifacts", artifacts, Artifact::getIdString, 20 )
                                               .indentDec()
                                               .reasonFollows( e )
                                               .toString()
                                      );
                        }
                        return contentFile;
                     }
                  )
               .orElseThrow
                  (
                     () -> new OseeCoreException
                                  (
                                     new Message()
                                            .title( "FileSystemRenderer::renderToFileInternal, Failed to export file." )
                                            .indentInc()
                                            .segment( "Renderer",    this.getName() )
                                            .segment( "Branch Name", branchName     )
                                            .segment( "Branch Id",   branchId       )
                                            .segmentIndexed( "Artifacts", artifacts, Artifact::getIdString, 20 )
                                            .toString()
                                  )
                  );

            return Optional.empty();

         }

         case PREVIEW: {

            final var subFolderIPath = RendererUtil.makeRenderPath(pathRoot.toString());

            final var iFile =
               RenderingUtil.getRenderFile(this, presentationType, subFolderIPath, extension, filenameSegments).map(
                  (contentFile) -> {
                     try {
                        AIFile.writeToFile(contentFile, renderInputStream);
                     } catch (Exception e) {
                              throw
                                 new OseeCoreException
                                        (
                                           new Message()
                                                  .title( "FileSystemRenderer::renderToFileInternal, Failed to write preview file." )
                                                  .indentInc()
                                                  .segment( "Renderer",    this.getName() )
                                                  .segment( "Branch Name", branchName     )
                                                  .segment( "Branch Id",   branchId       )
                                                  .segmentIndexed( "Artifacts", artifacts, Artifact::getIdString, 20 )
                                                  .indentDec()
                                                  .reasonFollows( e )
                                                  .toString()
                                         );
                     }

                     if (presentationType.matches(PresentationType.PREVIEW)) {

                        monitor.markAsReadOnly(contentFile);

                        return contentFile;

                     }

                     if (presentationType.matches(PresentationType.SPECIALIZED_EDIT)) {

                        var location = contentFile.getLocation();

                        if (Objects.nonNull(location)) {

                           var file = location.toFile();

                           if (Objects.nonNull(file)) {

                              monitor.addFile(file,
                                 this.getUpdateOperation(file, artifacts, branchId, presentationType) /* abstract */
                              );
                           }
                        }
                     }

                     return contentFile;
                  }

               ).orElseThrow
                    (
                       () -> new OseeCoreException
                                    (
                                       new Message()
                                              .title( "FileSystemRenderer::renderToFileInternal, Failed render file for display.")
                                              .indentInc()
                                              .segment( "Renderer", this.getName() )
                                              .segment( "Presentation Type", presentationType.name() )
                                              .segment( "Branch Name", branchName )
                                              .segment( "Branch Id", branchId )
                                              .segmentIndexed("Artifacts", artifacts, Artifact::getIdString, 20)
                                              .toString()
                                    )
                    );

            return Optional.of(iFile);
         }

         default:
            throw Conditions.invalidCase(filenameFormat, "filenameFormat", IllegalArgumentException::new);
      }
      //@formatter:on

   }

   private List<Artifact> setRendererOptions(List<Artifact> artifacts) {

      /*
       * Get the branch token and ensure all artifacts are from the same branch. artifactsNoNull will be an empty list
       * if the input artifact list was null, empty, or only has null entries.
       */

      List<Artifact> artifactsNoNulls = new ArrayList<>(artifacts.size());

      var branchTokens = RenderingUtil.getBranchTokens(artifacts, artifactsNoNulls);

      if (branchTokens.size() > 1) {
         //@formatter:off
         throw new IllegalArgumentException(
            new Message()
               .title("FileSystemRenderer::renderToFile, All of the artifacts must be on the same branch for mass processing.")
               .indentInc()
               .segmentIndexed("Branches Found", branchTokens, BranchToken::getShortName)
               .toString()
         );
         //@formatter:on
      }

      if (artifactsNoNulls.isEmpty()) {
         this.setRendererOption(RendererOption.BRANCH, BranchId.valueOf(Id.SENTINEL));
         this.setRendererOption(RendererOption.BRANCH_NAME, "SENTINEL");

         return artifactsNoNulls;
      }

      var branchTokenIterator = branchTokens.iterator();

      //@formatter:off
      var branchToken =
         branchTokenIterator.hasNext()
            ? branchTokenIterator.next()
            : null;
      //@formatter:on

      // Ensure branchToken is not null before using it
      if (branchToken != null) {
         var branchId = BranchId.valueOf(branchToken.getId());

         //@formatter:off
         var branchName =
            Strings.isValidAndNonBlank(branchToken.getName())
               ? branchToken.getName()
               : branchId.getIdString();
         //@formatter:on

         this.setRendererOption(RendererOption.BRANCH, branchId);
         this.setRendererOption(RendererOption.BRANCH_NAME, branchName);
      } else {
         // Handle the case when branchToken is null
         this.setRendererOption(RendererOption.BRANCH, BranchId.valueOf(Id.SENTINEL));
         this.setRendererOption(RendererOption.BRANCH_NAME, "UNKNOWN");
      }

      return artifactsNoNulls;
   }

}
