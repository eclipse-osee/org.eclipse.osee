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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.compare.DefaultArtifactCompare;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Philips
 */
public class DefaultArtifactRenderer implements IRenderer {
   private static final IComparator DEFAULT_COMPARATOR = new DefaultArtifactCompare();

   private VariableMap options;

   public String getName() {
      return "Artifact Editor";
   }

   public void print(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      throw new OseeCoreException("The default renderer does not support the print operation");
   }

   public void print(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         print(artifact, monitor);
      }
   }

   public boolean supportsCompare() {
      return false;
   }

   @Override
   public void setOptions(VariableMap options) throws OseeArgumentException {
      this.options = options;
   }

   @Override
   public VariableMap getOptions() {
      return options;
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
   public DefaultArtifactRenderer newInstance() throws OseeCoreException {
      return new DefaultArtifactRenderer();
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (presentationType == PresentationType.GENERALIZED_EDIT) {
         return PRESENTATION_TYPE;
      }
      return DEFAULT_MATCH;
   }

   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      open(artifacts);
   }

   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      ArtifactEditor.editArtifacts(artifacts);
   }

   @Override
   public void openMergeEdit(List<Artifact> artifacts) throws OseeCoreException {
      ArtifactEditor.editArtifacts(artifacts);
   }

   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }

   @Override
   public void renderAttribute(String attributeTypeName, Artifact artifact, PresentationType presentationType, Producer producer, VariableMap map, AttributeElement attributeElement) throws OseeCoreException {
      WordMLProducer wordMl = (WordMLProducer) producer;
      String format = attributeElement.getFormat();
      boolean allAttrs = map.getBoolean("allAttrs");

      wordMl.startParagraph();

      if (allAttrs) {
         wordMl.addWordMl("<w:r><w:t> " + Xml.escape(attributeTypeName) + ": </w:t></w:r>");
      } else {
         // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
         wordMl.addWordMl(attributeElement.getLabel());
      }

      if (attributeTypeName.equals(CoreAttributeTypes.RELATION_ORDER.getName())) {
         wordMl.endParagraph();
         String data = renderRelationOrder(artifact);
         wordMl.addWordMl(data);
      } else {
         String valueList = artifact.getAttributesToString(attributeTypeName);
         if (attributeElement.getFormat().contains(">x<")) {
            wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(valueList).toString() + "<"));
         } else {
            wordMl.addTextInsideParagraph(valueList);
         }
         wordMl.endParagraph();
      }
   }

   private String renderRelationOrder(Artifact artifact) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      ArtifactGuidToWordML guidResolver = new ArtifactGuidToWordML(new OseeLinkBuilder());
      RelationOrderRenderer renderer =
            new RelationOrderRenderer(SkynetGuiPlugin.getInstance().getOseeCacheService().getRelationTypeCache(),
                  guidResolver, RelationManager.getSorterProvider());

      WordMLProducer producer = new WordMLProducer(builder);
      RelationOrderData relationOrderData = RelationManager.createRelationOrderData(artifact);
      renderer.toWordML(producer, artifact.getBranch(), relationOrderData);
      return builder.toString();
   }

   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      return ArtifactImageManager.getImage(artifact);
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(2);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.artifacteditor.command");
      }

      return commandIds;
   }

   @Override
   public IComparator getComparator() {
      return DEFAULT_COMPARATOR;
   }

   public List<AttributeType> orderAttributeNames(Artifact artifact, Collection<AttributeType> attributeTypes) {
      ArrayList<AttributeType> orderedAttributeTypes = new ArrayList<AttributeType>(attributeTypes.size());
      AttributeType contentType = null;

      for (AttributeType attributeType : attributeTypes) {
         if (attributeType.equals(CoreAttributeTypes.WHOLE_WORD_CONTENT) || attributeType.equals(CoreAttributeTypes.WORD_TEMPLATE_CONTENT)) {
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
}