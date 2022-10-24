/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.api.MsWordPreviewRequestData;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.compare.DefaultArtifactCompare;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * This WordRenderer is used for the purpose of making a REST Call to the PublishingEndpoint for previewing artifacts.
 *
 * @author Branden W. Phillips
 * @author Loren K. Ashley
 */

public class MSWordTemplateServerRenderer implements IRenderer {

   /**
    * The context menu command title for the Preview command.
    */

   private static final String COMMAND_TITLE_PREVIEW = "MS Word Preview (Server)";

   /**
    * The context menu command title for the Preview No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES = "MS Word Preview No Attributes (Server)";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN = "MS Word Preview With Children (Server)";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES =
      "MS Word Preview With Children No Attributes (Server)";

   /**
    * The default {@link IComparator} implementation for {@link Artifacts}.
    */

   private static final IComparator DEFAULT_ARTIFACT_COMPARATOR = new DefaultArtifactCompare();

   /**
    * The {@link ImageDescriptor} used to draw the icon for this renderer's command icon.
    */

   private static ImageDescriptor imageDescriptor;

   /**
    * A list of the {@link MenuCmdDef} for the right click context menu.
    */

   private static List<MenuCmdDef> menuCommandDefinitions;

   /**
    * Monitors temporary word files.
    *
    * @implNote TODO: Temporary files are added to the watcher but never removed.
    */

   private static final ArtifactFileMonitor monitor = new ArtifactFileMonitor();

   /**
    * The program extension for MS Word documents.
    */

   private static final String PROGRAM_EXTENSION_WORD = "doc";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer";

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "Server Side MS Word Edit";

   /**
    * The {@link Program} used to invoke MS Word.
    */

   private static Program wordApplication;

   /*
    * Build menu commands
    */

   static {

      MSWordTemplateServerRenderer.imageDescriptor =
         ImageManager.getProgramImageDescriptor(MSWordTemplateServerRenderer.PROGRAM_EXTENSION_WORD);

      MSWordTemplateServerRenderer.wordApplication =
         Program.findProgram(MSWordTemplateServerRenderer.PROGRAM_EXTENSION_WORD);

      //@formatter:off
      MSWordTemplateServerRenderer.menuCommandDefinitions =
      List.of
         (
            new MenuCmdDef
                   (
                     CommandGroup.PREVIEW_SERVER,
                     MSWordTemplateServerRenderer.COMMAND_TITLE_PREVIEW,
                     MSWordTemplateServerRenderer.imageDescriptor,
                     Map.of
                        (
                           RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                           RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_VALUE.getKey()
                        )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW_SERVER,
                      MSWordTemplateServerRenderer.COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES,
                      MSWordTemplateServerRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW_SERVER,
                      MSWordTemplateServerRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN,
                      MSWordTemplateServerRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW_SERVER,
                      MSWordTemplateServerRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES,
                      MSWordTemplateServerRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_WITH_RECURSE_NO_ATTRIBUTES_VALUE.getKey()
                         )
                   )
         );
      //@formatter:on
   }

   /**
    * Map of rendering options. This contents of the map may be modified after the renderer has been created.
    */

   private final Map<RendererOption, Object> rendererOptions;

   /**
    * Creates a new {@link MSWordTemplateServerRenderer} without any options. This constructor is used by the
    * right-click context menu to create the renderer.
    */

   public MSWordTemplateServerRenderer() {
      this.rendererOptions = new EnumMap<>(RendererOption.class);
   }

   /**
    * Creates a new {@link MSWordTemplateServerRenderer} with the provided options.
    *
    * @param options map of {@link RendererOption}s.
    */

   public MSWordTemplateServerRenderer(Map<RendererOption, Object> options) {
      //@formatter:off
      this.rendererOptions =
         Objects.nonNull( options ) && !options.isEmpty()
            ? new EnumMap<>( options )
            : new EnumMap<>(RendererOption.class);
      //@formatter:on
   }

   /**
    * Adds the context menu command entries for this renderer to the specified list of {@link MenuCmdDef} objects for
    * the specified artifact.
    *
    * @param commands the {@link List} of {@link MenuCmdDef} objects to be appended to. This parameter maybe an empty
    * list but should not be <code>null</code>.
    * @param the {@link Artifact} context menu commands are to be offered for. This parameter is not used.
    * @throws NullPointerException when the parameter <code>commands</code> is <code>null</code>.
    */

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {

      Objects.requireNonNull(commands,
         "MSWordTemplateServerRenderer::addMenuCommandDefinitions, the parameter \"commands\" is null.");

      MSWordTemplateServerRenderer.menuCommandDefinitions.forEach(commands::add);
   }

   /**
    * Determines the applicability rating for the {@link IRenderer} implementation.
    * <p>
    * During menu building this renderer must declare itself as applicable or else the method
    * {@link #addMenuCommandDefinitions} will not get invoked.
    * <p>
    * During command execution this renderer must return the highest applicability rating to be selected and used to
    * process the command. This renderer determines it's applicability in the same manner as the client side renderer.
    * However, when the {@link PresentationType} is {@link PresentationType#PREVIEW_SERVER} the applicability rating is
    * incremented so that this (server side) render will win out over the client side renderer.
    * <p>
    *
    * @param presentationType the publishing request {@link PresentationType}.
    * @param artifact this parameter is not used and may be <code>null</code>.
    * @param options this parameter is not used and may be <code>null</code>.
    * @return the applicability rating
    */

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      var rating = MSWordTemplateRendererUtils.getApplicabilityRating(presentationType, artifact, rendererOptions);
      if (PresentationType.PREVIEW_SERVER.equals(presentationType)) {
         rating++;
      }
      return rating;
   }

   /**
    * Gets the {@link IComparator} implementation used to compare {@link Artifact}s.
    *
    * @return {@link IComparator} implementation for {@link Artifact}s.
    */

   @Override
   public IComparator getComparator() {
      return MSWordTemplateServerRenderer.DEFAULT_ARTIFACT_COMPARATOR;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return MSWordTemplateServerRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return MSWordTemplateServerRenderer.RENDERER_NAME;
   }

   /**
    * Unsupported operation.
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public List<AttributeTypeToken> getOrderedAttributeTypes(Artifact artifact, Collection<? extends AttributeTypeToken> attributeTypes) {
      throw new UnsupportedOperationException();
   }

   /**
    * Gets the specified {@link RendererOption} value. The following default values are provided renderer option values
    * of the following types:
    * <dl>
    * <dt>Boolean</dt>
    * <dd>false</dd>
    * <dt>ArtifactId</dt>
    * <dd>ArtifactId.SENTINEL</dt>
    * <dt>BranchId</dt>
    * <dd>BranchId.SENTINEL</dt>
    * </dl>
    * Unspecified renderer option values of other types are returned as <code>null</code>.
    *
    * @param key the {@link RendererOption} to get.
    * @return the value of the specified {@link RendererOption}.
    */

   public Object getRendererOptionValue(RendererOption key) {

      var value = this.rendererOptions.get(key);

      if (Objects.nonNull(value)) {
         return value;
      }

      switch (key.getType()) {
         case Boolean:
            return false;

         case ArtifactId:
            return ArtifactId.SENTINEL;

         case BranchId:
            return BranchId.SENTINEL;

         default:
            return null;
      }
   }

   /**
    * Sends a MsWord document publishing request to the OSEE server and returns the response content as an
    * {@link InputStream}.
    *
    * @param presentationType enumeration describing how the results will be presented to the user and used for the
    * publishing template selection.
    * @param publishArtifacts a list of the {@link Artifact} objects to be published.
    * @return an {@link InputStream} containing the MsWord content of the published artifacts.
    * @throws OseeCoreException when:
    * <ul>
    * <li>inputs are invalid,</li>
    * <li>failed OSEE server response,</li>
    * <li>no Word content is returned from the server,</li>
    * <li>the Word content is not properly terminated, or</li>
    * <li>the Word content was not properly encoded for the {@link InputStream}.</li>
    * </ul>
    */

   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> publishArtifacts) {

      /*
       * Validate Inputs
       */

      //@formatter:off
      if(    Objects.isNull(presentationType)
          || Objects.isNull(publishArtifacts)
          || (publishArtifacts.size() == 0) ) {

         throw new OseeCoreException
                      (
                        new StringBuilder( 1024 )
                           .append( "MsWord Renderer invalid inputs." ).append( "\n" )
                           .append( "   PresentationType:  " )
                              .append
                                 (
                                   Objects.nonNull( presentationType )
                                      ? presentationType
                                      : "(no presentation type specified)"
                                 )
                           .append( "\n" )
                              .append( "   Publish Artifacts: " )
                              .append
                                 (
                                   Objects.nonNull( publishArtifacts )
                                      ? ( publishArtifacts.size() > 0 )
                                         ? publishArtifacts.stream().map( ArtifactId::toString ).collect( Collectors.joining( ", ", "[ ", " ]" ) )
                                         : "(no artifacts to publish specified)"
                                      : "(no artifacts to publish specified)"
                                 )
                           .append( "\n" )
                           .toString()
                      );
      }

      /*
       * Create the server request data
       */

      var publishingTemplateRequest =
         new PublishingTemplateRequest
                (
                   this.getIdentifier(),                                                 /* Renderer Id                */
                   publishArtifacts.get(0).getArtifactTypeName(),                        /* Publish Artifact Type Name */
                   presentationType.name(),                                              /* Presentation Type          */
                   (String) this.getRendererOptionValue(RendererOption.TEMPLATE_OPTION)  /* Option                     */
                );

      var branchId =
         BranchId.create
            (
               publishArtifacts.get(0).getBranch().getId(),                 /* Branch Id */
               ((ArtifactId) getRendererOptionValue(RendererOption.VIEW))   /* View Id   */
            );
      //@formatter:on

      /*
       * Make list of Artifact Identifiers from the list of Artifacts to be published
       */

      var publishArtifactIds =
         publishArtifacts.stream().map(Artifact::getId).map(ArtifactId::valueOf).collect(Collectors.toList());

      var msWordPreviewRequestData =
         new MsWordPreviewRequestData(publishingTemplateRequest, branchId, publishArtifactIds);

      /*
       * Make the server call for the publish
       */

      return PublishingRequestHandler.msWordPreview(msWordPreviewRequestData);
   }

   /**
    * This method is forced to be implemented but currently should never be used.
    */

   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new UpdateArtifactOperation(file, artifacts, branch, false);
   }

   /**
    * Gets the minimum applicability ranking for this {@link IRenderer} implementation.
    *
    * @return {@link IRenderer#NO_MATCH}.
    */

   @Override
   public int minimumRanking() {
      return IRenderer.NO_MATCH;
   }

   /**
    * Creates a new {@link MSWordTemplateServerRenderer} without any options.
    *
    * @return an {@link MSWordTemplateServerRenderer} object.
    */

   @Override
   public MSWordTemplateServerRenderer newInstance() {
      return new MSWordTemplateServerRenderer();
   }

   /**
    * Creates a new {@link MSWordTemplateServerRenderer} with the map of provided {@link RendererOption}s.
    *
    * @return an {@link MSWordTemplateServerRenderer} object.
    */

   @Override
   public MSWordTemplateServerRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new MSWordTemplateServerRenderer(rendererOptions);
   }

   /**
    * Opens the artifact preview in MS Word or an alternate editor if MS Word is not installed on the client. This
    * method does not wait for the MS Word or alternate application to be closed.
    *
    * @param artifacts a {@link List} of the {@link Artifact}s to be previewed.
    * @param presentationType how the artifacts are to be displayed.
    * @throws OseeArgumentException when an application to open the preview is not found.
    */

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {

      if (Objects.isNull(artifacts) || artifacts.isEmpty()) {
         return;
      }

      if (PresentationType.DEFAULT_OPEN.equals(presentationType)) {
         presentationType = PresentationType.PREVIEW_SERVER;
      }

      var branchToken = artifacts.get(0).getBranchToken();

      if (!MSWordTemplateRendererUtils.artifactsOnSameBranch(artifacts)) {
         this.displayErrorDocument(branchToken, artifacts, presentationType,
            "All of the artifacts must be on the same branch to be mass edited");
         return;
      }

      InputStream inputStream = null;

      try {
         inputStream = this.getRenderInputStream(presentationType, artifacts);
      } catch (WebApplicationException wae) {
         this.displayErrorDocument(branchToken, artifacts, presentationType, wae.getCause().getMessage());
         return;
      } catch (Exception e) {
         this.displayErrorDocument(branchToken, artifacts, presentationType, e.getMessage());
         return;
      }

      this.displayWordDocument(branchToken, artifacts, presentationType, inputStream);
   }

   private void displayWordDocument(BranchToken branchToken, List<Artifact> artifacts, PresentationType presentationType, InputStream inputStream) {

      //@formatter:off
      IFile workingFile =
         RenderingUtil.getRenderFile
            (
               this,                                         /* IRenderer renderer */
               RenderingUtil.toFileName(branchToken).trim(), /* String subFolder */
               RenderingUtil.constructFilename               /* String filename */
                  (
                     RenderingUtil.getNameFromArtifacts
                        (
                           artifacts,
                           presentationType
                        ),
                     null,
                     ".xml"
                  ),
               presentationType                              /* PresentationType presentationType */
            );

      AIFile.writeToFile(workingFile, inputStream);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         File file = workingFile.getLocation().toFile();
         monitor.addFile(file, this.getUpdateOperation(file, artifacts, branchToken, presentationType));
      } else if (presentationType == PresentationType.PREVIEW_SERVER) {
         monitor.markAsReadOnly(workingFile);
      }

      try {
         if (RenderingUtil.arePopupsAllowed()) {

            RenderingUtil.ensureFilenameLimit(workingFile);

            MSWordTemplateServerRenderer.wordApplication.execute(workingFile.getLocation().toFile().getAbsolutePath());

         } else {

            OseeLog.logf(Activator.class, Level.INFO,
               "Test - Opening File - [%s]" + workingFile.getLocation().toFile().getAbsolutePath());

         }
      } catch (Exception ex) {

         var workbench = PlatformUI.getWorkbench();
         var editorDescriptor = workbench.getEditorRegistry().getDefaultEditor(workingFile.getName());

         if (editorDescriptor != null) {
            try {

               var page = workbench.getActiveWorkbenchWindow().getActivePage();
               page.openEditor(new FileEditorInput(workingFile), editorDescriptor.getId());

            } catch (PartInitException | NullPointerException ex1) {

               throw new OseeArgumentException(
                  "No program associated with the extension [%s] found on your local machine.",
                  workingFile.getFileExtension());

            }
         }
      }

   }

   private void displayErrorDocument(BranchToken branchToken, List<Artifact> artifacts, PresentationType presentationType, String errorMessage) {

      //@formatter:off
      var message =
         new Message()
                .title( "Failed to Publish MS Word document." )
                .indentInc()
                .segment( "Artifacts", artifacts,   Artifact::getIdString    )
                .segment( "Branch",    branchToken, BranchToken::getIdString )
                .blank()
                .indentDec()
                .title( "Reason Follows:" )
                .blank()
                .block( errorMessage )
                .toString();
      //@formatter:on

      OseeLog.log(this.getClass(), Level.WARNING, message);

      if (RenderingUtil.arePopupsAllowed()) {

         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openError(Displays.getActiveShell(), "Publishing Error", message);
            }
         });
      }

   }

   /**
    * Rendering the attributes of an artifact are not supported by this renderer.
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer) {
      throw new UnsupportedOperationException();
   }

   /**
    * Rendering the attributes of an artifact are not supported by this renderer.
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public String renderAttributeAsString(AttributeTypeId attributeType, Artifact artifact, PresentationType presentationType, String defaultValue) {
      throw new UnsupportedOperationException();
   }

   private IFile renderToFile(List<Artifact> artifacts, PresentationType presentationType) {

      var branchToken = Objects.nonNull(artifacts) && (artifacts.size() > 1) ? artifacts.get(0).getBranchToken() : null;

      var renderInputStream = this.getRenderInputStream(presentationType, artifacts);

      //@formatter:off
      IFile workingFile =
         RenderingUtil.getRenderFile
            (
               this,                                                                   /* IRenderer renderer  */
               RenderingUtil.toFileName(branchToken).trim(),                           /* String    subFolder */
               RenderingUtil.constructFilename                                         /* String    filename  */
                  (
                     RenderingUtil.getNameFromArtifacts(artifacts, presentationType),
                     null,
                     ".xml"
                  ),
               presentationType                                                       /* PresentationType presentationType */
            );
      //@formatter:on

      AIFile.writeToFile(workingFile, renderInputStream);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         File file = workingFile.getLocation().toFile();
         monitor.addFile(file, this.getUpdateOperation(file, artifacts, branchToken, presentationType));
      } else if (presentationType == PresentationType.PREVIEW_SERVER) {
         monitor.markAsReadOnly(workingFile);
      }
      return workingFile;
   }

   /**
    * This {@link IRenderer} implementation uses the default {@link IComparator} implementation for {@link Artifacts}.
    *
    * @return <code>true</code>.
    */

   @Override
   public boolean supportsCompare() {
      return true;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public void updateOption(RendererOption key, Object value) {
      //@formatter:off
      this.rendererOptions.put
         (
            Objects.requireNonNull( key,   "MSWordTemplateServerRenderer::updateOption, parameter \"key\" cannot be null." ),
            Objects.requireNonNull( value, "MSWordTemplateServerRenderer::updateOption, parameter \"value\" cannot be null." )
         );
      //@formatter:on
   }

}

/* EOF */
