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

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.RENDER_AS_HUMAN_READABLE_TEXT;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlEncoderDecoder;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.compare.DefaultArtifactCompare;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.word.WordRenderArtifactWrapperClientImpl;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 * @author Loren K. Ashley
 */

public class DefaultArtifactRenderer extends EnumRendererMap implements IRenderer {

   /**
    * The context menu command title for the Artifact Editor command.
    */

   private static final String COMMAND_TITLE_ARTIFACT_EDIT = "Artifact Editor";

   /**
    * The context menu command title for the Artifact Explorer command.
    */

   private static final String COMMAND_TITLE_ARTIFACT_EXPLORER = "Artifact Explorer";

   /**
    * The context menu command title for the Mass Editor command.
    */

   private static final String COMMAND_TITLE_MASS_EDITOR = "Mass Editor";

   /**
    * The context menu command title for the Resource History command.
    */

   private static final String COMMAND_TITLE_RESOURCE_HISTORY = "Resource History";

   /**
    * The context menu command title for the Sky Walker command.
    */

   private static final String COMMAND_TITLE_SKY_WALKER = "Sky Walker";

   private static final IComparator DEFAULT_COMPARATOR = new DefaultArtifactCompare();

   /**
    * A list of the {@link MenuCmdDef} for the right click context menu.
    */

   private static List<MenuCmdDef> menuCommandDefinitions;

   private static final String OPEN_IN_EXPLORER = "open.with.artifact.explorer";

   private static final String OPEN_IN_GRAPH = "open.with.sky.walker";

   private static final String OPEN_IN_HISTORY = "open.with.resource.history";

   private static final String OPEN_IN_TABLE_EDITOR = "open.with.mass.artifact.editor";

   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "Default Document Types";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = DefaultArtifactRenderer.class.getCanonicalName();

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "Artifact Editor";

   static {

      //@formatter:off
      DefaultArtifactRenderer.menuCommandDefinitions =
         List.of
            (
               new MenuCmdDef
                      (
                         CommandGroup.SHOW,
                         PresentationType.GENERALIZED_EDIT,
                         DefaultArtifactRenderer.COMMAND_TITLE_ARTIFACT_EDIT,
                         ImageManager.getImageDescriptor( FrameworkImage.ARTIFACT_EDITOR )
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.SHOW,
                         PresentationType.GENERALIZED_EDIT,
                         DefaultArtifactRenderer.COMMAND_TITLE_MASS_EDITOR,
                         ImageManager.getImageDescriptor( FrameworkImage.ARTIFACT_MASS_EDITOR ),
                         Map.of
                            (
                               RendererOption.OPEN_OPTION.getKey(),  DefaultArtifactRenderer.OPEN_IN_TABLE_EDITOR
                            )
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.SHOW,
                         PresentationType.GENERALIZED_EDIT,
                         DefaultArtifactRenderer.COMMAND_TITLE_ARTIFACT_EXPLORER,
                         ImageManager.getImageDescriptor( FrameworkImage.ARTIFACT_EXPLORER ),
                         Map.of
                            (
                               RendererOption.OPEN_OPTION.getKey(), DefaultArtifactRenderer.OPEN_IN_EXPLORER
                            )
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.SHOW,
                         PresentationType.GENERALIZED_EDIT,
                         DefaultArtifactRenderer.COMMAND_TITLE_RESOURCE_HISTORY,
                         ImageManager.getImageDescriptor( FrameworkImage.DB_ICON_BLUE ),
                         Map.of
                            (
                               RendererOption.OPEN_OPTION.getKey(), DefaultArtifactRenderer.OPEN_IN_HISTORY
                            )
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.SHOW,
                         PresentationType.GENERALIZED_EDIT,
                         DefaultArtifactRenderer.COMMAND_TITLE_SKY_WALKER,
                         ImageManager.getImageDescriptor( FrameworkImage.SKYWALKER ),
                         Map.of
                            (
                               RendererOption.OPEN_OPTION.getKey(), DefaultArtifactRenderer.OPEN_IN_GRAPH
                            )
                      )
            );
      //@formatter:on

   }

   protected List<MenuCmdDef> menuCommands;

   public DefaultArtifactRenderer() {
      this(null);
   }

   public DefaultArtifactRenderer(RendererMap rendererMap) {
      super(rendererMap);
      //@formatter:off
      this.menuCommands =
         ( this.getClass() == DefaultArtifactRenderer.class )
            ? DefaultArtifactRenderer.menuCommandDefinitions
            : null;
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
         "DefaultArtifactRenderer::addMenuCommandDefinitions, the parameter \"commands\" is null.");

      if (Objects.nonNull(this.menuCommands)) {
         this.menuCommands.forEach(commands::add);
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact,
      RendererMap rendererOptions) {
      if (presentationType.matches(GENERALIZED_EDIT, GENERAL_REQUESTED, PRODUCE_ATTRIBUTE)) {
         return PRESENTATION_TYPE;
      }
      if (presentationType.matches(SPECIALIZED_EDIT, DEFAULT_OPEN)) {
         return GENERAL_MATCH;
      }
      if (presentationType.matches(PREVIEW, RENDER_AS_HUMAN_READABLE_TEXT)) {
         return BASE_MATCH;
      }
      return NO_MATCH;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IComparator getComparator() {
      return DEFAULT_COMPARATOR;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return DefaultArtifactRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return DefaultArtifactRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return DefaultArtifactRenderer.RENDERER_NAME;
   }

   /**
    * {@inheritDoc}
    * <p>
    * All attribute types from the <code>attributeTypes</code> {@link Collection} except for the following:
    * <ul>
    * <li>{@link CoreAttributeTypes#WholeWordContent},</li>
    * <li>{@link CoreAttributeTypes#WordTemplateContent}, and</li>
    * <li>{@link CoreAttributeTypes#PlainTextContent}</li>
    * </ul>
    * are sorted a placed into the {@link List} to be returned. Of the excepted attribute types, the last one
    * encountered when reading the <code>attributeTypes</code> {@link Collection} is added to the end of the list to be
    * returned.
    *
    * @param artifact not used
    * @param attributeTypes a {@link Collection} of the {@link AttributeTypeToken}s to be ordered for rendering.
    * @return a list of the provided {@link AttributeTypeToken}s arranged in the rendering order for the attributes.
    */

   @Override
   public List<AttributeTypeToken> getOrderedAttributeTypes(Artifact artifact,
      Collection<? extends AttributeTypeToken> attributeTypes) {

      ArrayList<AttributeTypeToken> orderedAttributeTypes = new ArrayList<>(attributeTypes.size());

      AttributeTypeToken contentType = null;

      for (AttributeTypeToken attributeType : attributeTypes) {

         if (attributeType.matches(CoreAttributeTypes.WholeWordContent, CoreAttributeTypes.WordTemplateContent,
            CoreAttributeTypes.PlainTextContent)) {

            contentType = attributeType;

         } else {

            orderedAttributeTypes.add(attributeType);
         }
      }

      Collections.sort(orderedAttributeTypes);

      if (contentType != null) {
         orderedAttributeTypes.add(contentType);
      }

      return orderedAttributeTypes;
   }

   /**
    * Gets a copy of the renderer's options. Changes to the renderer's options will not be reflected in the returned
    * {@link RendererMap}.
    *
    * @return an immutable view of the renderer's options.
    */

   public RendererMap getRendererOptionsCopy() {
      return new EnumRendererMap(this);
   }

   /**
    * Gets an immutable view of the renderer's options. The view is backed by the renderer's options and changes to the
    * renderer's options will be reflected in the view.
    *
    * @return an immutable view of the renderer's options.
    */

   public RendererMap getRendererOptionsView() {
      return this.unmodifiableRendererMap();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int minimumRanking() {
      return NO_MATCH;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new DefaultArtifactRenderer();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IRenderer newInstance(RendererMap rendererOptions) {
      return new DefaultArtifactRenderer(rendererOptions);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            String openOption = "";
            if (DefaultArtifactRenderer.this.isRendererOptionSet(RendererOption.OPEN_OPTION)) {
               openOption = DefaultArtifactRenderer.this.getRendererOptionValue(RendererOption.OPEN_OPTION);
            }

            if (OPEN_IN_GRAPH.equals(openOption)) {
               for (Artifact artifact : artifacts) {
                  SkyWalkerView.exploreArtifact(artifact);
               }
            } else if (OPEN_IN_HISTORY.equals(openOption)) {
               for (Artifact artifact : artifacts) {
                  try {
                     HistoryView.open(artifact);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            } else if (OPEN_IN_EXPLORER.equals(openOption)) {
               for (Artifact artifact : artifacts) {
                  ArtifactExplorerUtil.revealArtifact(artifact);
               }
            } else if (OPEN_IN_TABLE_EDITOR.equals(openOption)) {
               MassArtifactEditor.editArtifacts("", artifacts);
            } else {
               try {
                  for (Artifact artifact : artifacts) {
                     AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), ArtifactEditor.EDITOR_ID);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }

   /**
    * {@inheritDoc}
    * <p>
    *
    * @implNote {@link IRender} interface TODO: Why does the default renderer know about Word ML?
    */

   @Override
   public void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType,
      WordMLProducer producer, String format, String label, String footer) {
      WordMLProducer wordMl = producer;
      boolean allAttrs = (boolean) this.getRendererOptionValue(RendererOption.ALL_ATTRIBUTES);

      wordMl.startParagraph();

      if (allAttrs) {
         if (!attributeType.matches(CoreAttributeTypes.PlainTextContent)) {
            wordMl.addWordMl(
               "<w:r><w:t> " + XmlEncoderDecoder.textToXml(attributeType.getUnqualifiedName()) + ": </w:t></w:r>");
         } else {
            wordMl.addWordMl("<w:r><w:t> </w:t></w:r>");
         }
      } else {
         // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
         wordMl.addWordMl(label);
      }

      if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {
         wordMl.endParagraph();
         String data = renderRelationOrder(new WordRenderArtifactWrapperClientImpl(artifact));
         wordMl.addWordMl(data);
      } else {
         String valueList = artifact.getAttributesToString(attributeType);
         if (format.contains(">x<")) {
            wordMl.addWordMl(format.replace(">x<", ">" + XmlEncoderDecoder.textToXml(valueList).toString() + "<"));
         } else {
            wordMl.addTextInsideParagraph(valueList);
         }
         wordMl.endParagraph();
      }
   }

   /**
    * {@inheritDoc}
    * <p>
    * {@implNote} {@link IRender} interface
    */

   @Override
   public String renderAttributeAsString(AttributeTypeId attributeType, Artifact artifact,
      PresentationType presentationType, final String defaultValue) {
      String returnValue = defaultValue;
      if (presentationType.matches(RENDER_AS_HUMAN_READABLE_TEXT)) {
         if (artifact == null) {
            returnValue = "DELETED";
         } else {
            Attribute<Object> soleAttribute = artifact.getSoleAttribute(attributeType);
            if (soleAttribute == null) {
               returnValue = "DELETED";
            } else {
               returnValue = soleAttribute.getDisplayableString();
            }
         }
      }
      return returnValue;
   }

   public String renderRelationOrder(ArtifactReadable wrappedArtifact) {
      Artifact artifact = ((WordRenderArtifactWrapperClientImpl) wrappedArtifact).getArtifact();
      StringBuilder builder = new StringBuilder();
      ArtifactGuidToWordML guidResolver = new ArtifactGuidToWordML(new OseeLinkBuilder());
      RelationOrderRenderer renderer = new RelationOrderRenderer(guidResolver);

      WordMLProducer producer = new WordMLProducer(builder);
      RelationOrderData relationOrderData = RelationManager.createRelationOrderData(artifact);
      renderer.toWordML(producer, artifact.getBranch(), relationOrderData);
      return builder.toString();
   }

   /**
    * {@inheritDoc}
    * <p>
    * The default renderer does not support compare.
    *
    * @return <code>false</code>.
    */

   @Override
   public boolean supportsCompare() {
      return false;
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
         .title( this.getClass().getSimpleName() )
         .indentInc()
         .segment( "Name", this.getName() )
         ;

      super.toMessage( indent + 1, outMessage );

      outMessage
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

}
