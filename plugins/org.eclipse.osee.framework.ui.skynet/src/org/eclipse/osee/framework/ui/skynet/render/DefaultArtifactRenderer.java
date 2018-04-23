/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.OptionType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
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
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.compare.DefaultArtifactCompare;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public class DefaultArtifactRenderer implements IRenderer {
   private static final IComparator DEFAULT_COMPARATOR = new DefaultArtifactCompare();
   private static final String OPEN_IN_TABLE_EDITOR = "open.with.mass.artifact.editor";
   private static final String OPEN_IN_GRAPH = "open.with.sky.walker";
   private static final String OPEN_IN_HISTORY = "open.with.resource.history";
   private static final String OPEN_IN_EXPLORER = "open.with.artifact.explorer";

   private Map<RendererOption, Object> rendererOptions;

   public DefaultArtifactRenderer(Map<RendererOption, Object> rendererOptions) {
      this.rendererOptions = rendererOptions;
   }

   public DefaultArtifactRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   public Map<RendererOption, Object> getRendererOptions() {
      return rendererOptions;
   }

   public Object getRendererOptionValue(RendererOption key) {
      if (rendererOptions.containsKey(key)) {
         return rendererOptions.get(key);
      } else if (key.getType().equals(OptionType.Boolean)) {
         return false;
      } else if (key.getType().equals(OptionType.ArtifactId)) {
         return ArtifactId.SENTINEL;
      } else if (key.getType().equals(OptionType.BranchId)) {
         return BranchId.SENTINEL;
      }

      return null;
   }

   // TODO: will remove. Do not like this.
   public void updateOptions(Map<RendererOption, Object> rendererOptions) {
      this.rendererOptions = rendererOptions;
   }

   public void updateOption(RendererOption key, Object value) {
      rendererOptions.put(key, value);
   }

   @Override
   public String getName() {
      return "Artifact Editor";
   }

   @Override
   public boolean supportsCompare() {
      return false;
   }

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new DefaultArtifactRenderer();
   }

   @Override
   public IRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new DefaultArtifactRenderer(rendererOptions);
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
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

   @Override
   public int minimumRanking() {
      return NO_MATCH;
   }

   @Override
   public void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer) {
      WordMLProducer wordMl = producer;
      boolean allAttrs = (boolean) rendererOptions.get(RendererOption.ALL_ATTRIBUTES);

      wordMl.startParagraph();

      if (allAttrs) {
         if (!attributeType.matches(CoreAttributeTypes.PlainTextContent)) {
            wordMl.addWordMl("<w:r><w:t> " + Xml.escape(attributeType.getName()) + ": </w:t></w:r>");
         } else {
            wordMl.addWordMl("<w:r><w:t> </w:t></w:r>");
         }
      } else {
         // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
         wordMl.addWordMl(label);
      }

      if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {
         wordMl.endParagraph();
         String data = renderRelationOrder(artifact);
         wordMl.addWordMl(data);
      } else {
         String valueList = artifact.getAttributesToString(attributeType);
         if (format.contains(">x<")) {
            wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(valueList).toString() + "<"));
         } else {
            wordMl.addTextInsideParagraph(valueList);
         }
         wordMl.endParagraph();
      }
   }

   @Override
   public String renderAttributeAsString(AttributeTypeId attributeType, Artifact artifact, PresentationType presentationType, final String defaultValue) {
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

   private String renderRelationOrder(Artifact artifact) {
      StringBuilder builder = new StringBuilder();
      ArtifactGuidToWordML guidResolver = new ArtifactGuidToWordML(new OseeLinkBuilder());
      RelationOrderRenderer renderer =
         new RelationOrderRenderer(ServiceUtil.getOseeCacheService().getRelationTypeCache(), guidResolver);

      WordMLProducer producer = new WordMLProducer(builder);
      RelationOrderData relationOrderData = RelationManager.createRelationOrderData(artifact);
      renderer.toWordML(producer, artifact.getBranch(), relationOrderData);
      return builder.toString();
   }

   @Override
   public IComparator getComparator() {
      return DEFAULT_COMPARATOR;
   }

   @Override
   public List<AttributeTypeToken> getOrderedAttributeTypes(Artifact artifact, Collection<? extends AttributeTypeToken> attributeTypes) {
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

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            String openOption = "";
            if (rendererOptions.containsKey(RendererOption.OPEN_OPTION)) {
               openOption = (String) rendererOptions.get(RendererOption.OPEN_OPTION);
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

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(
         new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Artifact Editor", FrameworkImage.ARTIFACT_EDITOR));
      commands.add(new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Mass Editor",
         FrameworkImage.ARTIFACT_MASS_EDITOR, RendererOption.OPEN_OPTION.getKey(), OPEN_IN_TABLE_EDITOR));
      commands.add(new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Artifact Explorer",
         FrameworkImage.ARTIFACT_EXPLORER, RendererOption.OPEN_OPTION.getKey(), OPEN_IN_EXPLORER));
      commands.add(new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Resource History", FrameworkImage.DB_ICON_BLUE,
         RendererOption.OPEN_OPTION.getKey(), OPEN_IN_HISTORY));
      commands.add(new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Sky Walker", FrameworkImage.SKYWALKER,
         RendererOption.OPEN_OPTION.getKey(), OPEN_IN_GRAPH));
   }

}