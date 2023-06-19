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

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WholeWordContent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.DataRightContentBuilder;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordCoreUtilClient;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WholeWordCompare;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * This Word Renderer is used for editing and previewing artifacts that contain a whole Ms Word document.
 *
 * @author Jeff C. Phillips
 * @author Loren K. Ashley
 */
public class WholeWordRenderer extends FileSystemRenderer {

   /**
    * The context menu command title for the Edit command.
    */

   private static final String COMMAND_TITLE_EDIT = "MS Whole Word Edit";

   /**
    * The context menu command title for the Preview command.
    */

   private static final String COMMAND_TITLE_PREVIEW = "MS Whole Word Preview";

   /**
    * The likely file system extension for files that hold the same type of data as is stored in main content
    * {@link Attribute} of the most common {@link Artifact} type processed by this renderer.
    */

   private static final String DEFAULT_ASSOCIATED_FILE_EXTENSION = "xml";

   private static final String FTR_END_TAG = "</w:ftr>";

   private static final String FTR_START_TAG = "<w:ftr[^>]*>";

   private static final Pattern END_PATTERN = Pattern.compile(FTR_END_TAG);

   /**
    * The {@link ImageDescriptor} used to draw the icon for this renderer's command icon.
    */

   private static ImageDescriptor imageDescriptor;

   /**
    * A list of the {@link MenuCmdDef} for the right click context menu.
    */

   private static List<MenuCmdDef> menuCommandDefinitions;

   /**
    * The program extension for MS Word documents.
    */

   private static final String PROGRAM_EXTENSION_WORD = "doc";

   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "Whole MS Word";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = WholeWordRenderer.class.getCanonicalName();

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "MS Whole Word Edit";

   private static final Pattern START_PATTERN = Pattern.compile(FTR_START_TAG);

   /**
    * The {@link Program} used to invoke MS Word.
    */

   private static Program wordApplication;

   static {

      WholeWordRenderer.imageDescriptor =
         ImageManager.getProgramImageDescriptor(WholeWordRenderer.PROGRAM_EXTENSION_WORD);

      WholeWordRenderer.wordApplication = Program.findProgram(WholeWordRenderer.PROGRAM_EXTENSION_WORD);

      //@formatter:off
      WholeWordRenderer.menuCommandDefinitions =
      List.of
         (
            new MenuCmdDef
                   (
                     CommandGroup.EDIT,
                     WholeWordRenderer.COMMAND_TITLE_EDIT,
                     WholeWordRenderer.imageDescriptor,
                     Map.of
                        (
                           RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey()
                        )
                   ),

            new MenuCmdDef
                   (
                     CommandGroup.PREVIEW,
                     WholeWordRenderer.COMMAND_TITLE_PREVIEW,
                     WholeWordRenderer.imageDescriptor,
                     Map.of
                        (
                           RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey()
                        )
                   )
         );
      //@formatter:on

   }

   private final IComparator comparator;

   /**
    * Creates a new {@link WholeWordRenderer} without any options. This constructor is used by the right-click context
    * menu to create the renderer.
    */

   public WholeWordRenderer() {
      this(null);
   }

   /**
    * Creates a new {@link WholeWordRenderer} with the provided options.
    *
    * @param options map of {@link RendererOption}s.
    */

   public WholeWordRenderer(RendererMap options) {
      super(options);
      this.comparator = new WholeWordCompare(this);
   }

   private String addDataRights(String content, String classification, Artifact artifact) {
      String toReturn = content;
      WordCoreUtil.pageType orientation = WordCoreUtilClient.getPageOrientation(artifact);

      var dataRightAnchorsResult = ServiceUtil.getOseeClient().getDataRightsEndpoint().getDataRights(
         artifact.getBranch(), classification, Collections.singletonList(ArtifactId.create(artifact)));

      var dataRightContentBuilder = new DataRightContentBuilder(dataRightAnchorsResult);

      String footer = dataRightContentBuilder.getContent(artifact, orientation);

      Matcher startFtr = START_PATTERN.matcher(footer);
      Matcher endFtr = END_PATTERN.matcher(footer);
      if (startFtr.find() && endFtr.find()) {
         ChangeSet ftrCs = new ChangeSet(footer);
         ftrCs.delete(0, startFtr.end());
         ftrCs.delete(endFtr.start(), footer.length());
         footer = ftrCs.applyChangesToSelf().toString();
      }

      startFtr.reset(content);
      endFtr.reset(content);
      ChangeSet cs = new ChangeSet(content);
      while (startFtr.find()) {
         if (endFtr.find()) {
            cs.replace(startFtr.end(), endFtr.start(), footer);
         }
      }
      toReturn = cs.applyChangesToSelf().toString();
      return toReturn;
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
         "WholeWordRenderer::addMenuCommandDefinitions, the parameter \"commands\" is null.");

      WholeWordRenderer.menuCommandDefinitions.forEach(commands::add);
   }

   /**
    * Determines the applicability rating for the {@link IRenderer} implementation.
    * <p>
    * During menu building this renderer must declare itself as applicable or else the method
    * {@link #addMenuCommandDefinitions} will not get invoked.
    * <p>
    * During command execution this renderer must return the highest applicability rating to be selected and used to
    * process the command. This renderer returns the highest applicability rating
    * ({@link IRenderer#PRESENTATION_SUBTYPE_MATCH}) when the <code>presentationType</code> type is not one of the
    * following {@link PresentationType#GENERALIZED_EDIT}, {@link PresentationType#GENERAL_REQUESTED}, or
    * {@link PresentationType#PRODUCE_ATTRIBUTE}; and the <code>artifact</code> is of the type
    * {@link CoreAttributeTypes#WholeWordContent}.
    * <p>
    *
    * @param presentationType the publishing request {@link PresentationType}.
    * @param artifact this parameter is not used and may be <code>null</code>.
    * @param options this parameter is not used and may be <code>null</code>.
    * @return the applicability rating
    */

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact,
      RendererMap rendererOptions) {
      //@formatter:off
      if (
              !presentationType.matches
                  (
                     PresentationType.GENERALIZED_EDIT,
                     PresentationType.GENERAL_REQUESTED,
                     PresentationType.PRODUCE_ATTRIBUTE
                  )

            && artifact.isAttributeTypeValid( WholeWordContent )
         ) {

         return IRenderer.PRESENTATION_SUBTYPE_MATCH;
      }

      return IRenderer.NO_MATCH;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      return WholeWordRenderer.wordApplication;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IComparator getComparator() {
      return this.comparator;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDefaultAssociatedExtension() {
      return WholeWordRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return WholeWordRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return WholeWordRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return WholeWordRenderer.RENDERER_NAME;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {
      InputStream stream = null;
      try {
         if (artifacts.isEmpty()) {
            stream = Streams.convertStringToInputStream(WordCoreUtilClient.getEmptyDocumentContent(), "UTF-8");
         } else {
            Artifact artifact = artifacts.iterator().next();
            String content = artifact.getOrInitializeSoleAttributeValue(CoreAttributeTypes.WholeWordContent);
            if (presentationType == PresentationType.DIFF && WordCoreUtil.containsWordAnnotations(content)) {
               throw new OseeStateException(
                  "Trying to diff the [%s] artifact on the [%s] branch, which has tracked changes turned on.  All tracked changes must be removed before the artifacts can be compared.",
                  artifact.getName(), artifact.getBranchToken().getName());
            }

            Set<String> unknownGuids = new HashSet<>();
            LinkType linkType = LinkType.OSEE_SERVER_LINK;
            content = WordMlLinkHandler.link(linkType, artifact, content, unknownGuids, presentationType);
            WordUiUtil.displayUnknownGuids(artifact, unknownGuids);
            WordUiUtil.getStoredResultData();

            String classification =
               artifact.getSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, "invalid");

            if (DataRightsClassification.isValid(classification)) {
               content = addDataRights(content, classification, artifact);
            }

            stream = Streams.convertStringToInputStream(content, "UTF-8");
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return stream;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.WholeWordContent);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public WholeWordRenderer newInstance() {
      return new WholeWordRenderer();
   }

   @Override
   public WholeWordRenderer newInstance(RendererMap rendererOptions) {
      return new WholeWordRenderer(rendererOptions);
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {

      if (Objects.isNull(artifacts) || artifacts.isEmpty() || Objects.isNull(presentationType)) {
         return;
      }

      for (Artifact artifact : artifacts) {
         super.open(Collections.singletonList(artifact), presentationType);
      }
   }


   @Override
   public boolean supportsCompare() {
      return true;
   }

}

/* EOF */
