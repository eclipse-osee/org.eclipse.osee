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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.compare.DefaultArtifactCompare;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
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

   private final VariableMap options = new VariableMap();

   @Override
   public String getName() {
      return "Artifact Editor";
   }

   @Override
   public boolean supportsCompare() {
      return false;
   }

   @Deprecated
   public Object[] getValues() {
      return options.getValues();
   }

   @Override
   public String getStringOption(String key) throws OseeArgumentException {
      return options == null ? null : options.getString(key);
   }

   @Override
   public boolean getBooleanOption(String key) throws OseeArgumentException {
      if (options != null) {
         return options.getBoolean(key);
      }
      return false;
   }

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new DefaultArtifactRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact, Object... options) throws OseeCoreException {
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
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }

   @Override
   public void renderAttribute(IAttributeType attributeType, Artifact artifact, PresentationType presentationType, Producer producer, AttributeElement attributeElement, String footer) throws OseeCoreException {
      WordMLProducer wordMl = (WordMLProducer) producer;
      String format = attributeElement.getFormatPre();
      boolean allAttrs = getBooleanOption("allAttrs");

      wordMl.startParagraph();

      if (allAttrs) {
         if (!attributeType.matches(CoreAttributeTypes.PlainTextContent)) {
            wordMl.addWordMl("<w:r><w:t> " + Xml.escape(attributeType.getName()) + ": </w:t></w:r>");
         } else {
            wordMl.addWordMl("<w:r><w:t> </w:t></w:r>");
         }
      } else {
         // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
         wordMl.addWordMl(attributeElement.getLabel());
      }

      if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {
         wordMl.endParagraph();
         String data = renderRelationOrder(artifact);
         wordMl.addWordMl(data);
      } else {
         String valueList = artifact.getAttributesToString(attributeType);
         if (attributeElement.getFormatPre().contains(">x<")) {
            wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(valueList).toString() + "<"));
         } else {
            wordMl.addTextInsideParagraph(valueList);
         }
         wordMl.endParagraph();
      }
   }

   @Override
   public String renderAttributeAsString(IAttributeType attributeType, Artifact artifact, PresentationType presentationType, final String defaultValue) throws OseeCoreException {
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

   private String renderRelationOrder(Artifact artifact) throws OseeCoreException {
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
   public List<IAttributeType> getOrderedAttributeTypes(Artifact artifact, Collection<IAttributeType> attributeTypes) {
      ArrayList<IAttributeType> orderedAttributeTypes = new ArrayList<>(attributeTypes.size());
      IAttributeType contentType = null;

      for (IAttributeType attributeType : attributeTypes) {
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
   public void open(final List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            String openOption = getStringOption(OPEN_OPTION);
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
                  ArtifactExplorer.revealArtifact(artifact);
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

   protected boolean isRenderOption(String value) {
      boolean result = false;
      try {
         result = getBooleanOption(value);
      } catch (OseeArgumentException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return result;
   }

   @Override
   public Object getOption(String key) {
      return options.getValue(key);
   }

   @Override
   public IOseeBranch getBranchOption(String key) throws OseeArgumentException {
      return options.getBranch(key);
   }

   @Override
   public void setOption(String optionName, Object value) {
      options.setValue(optionName, value);
   }

   @Override
   public void setOptions(Object... options) throws OseeArgumentException {
      this.options.setValues(options);
   }

   @Override
   public List<Artifact> getArtifactsOption(String key) throws OseeArgumentException {
      return options.getArtifacts(key);
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(
         new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Artifact Editor", FrameworkImage.ARTIFACT_EDITOR));
      commands.add(new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Mass Editor",
         FrameworkImage.ARTIFACT_MASS_EDITOR, OPEN_OPTION, OPEN_IN_TABLE_EDITOR));
      commands.add(new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Artifact Explorer",
         FrameworkImage.ARTIFACT_EXPLORER, OPEN_OPTION, OPEN_IN_EXPLORER));
      commands.add(new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Resource History", FrameworkImage.DB_ICON_BLUE,
         OPEN_OPTION, OPEN_IN_HISTORY));
      commands.add(new MenuCmdDef(CommandGroup.SHOW, GENERALIZED_EDIT, "Sky Walker", FrameworkImage.SKYWALKER,
         OPEN_OPTION, OPEN_IN_GRAPH));
   }

   @Override
   public List<IArtifactType> getArtifactTypesOption(String key) throws OseeArgumentException {
      return options.getArtifactTypes(key);
   }
}