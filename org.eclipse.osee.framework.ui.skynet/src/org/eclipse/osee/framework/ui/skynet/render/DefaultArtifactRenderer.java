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
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Philips
 */
public class DefaultArtifactRenderer implements IRenderer {
   private VariableMap options;

   /**
    * @param rendererId
    */
   public DefaultArtifactRenderer() {
      super();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getName()
    */
   public String getName() {
      return "Artifact Editor";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#print(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void print(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#print(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void print(List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         print(artifact, monitor);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#compare(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IProgressMonitor monitor, PresentationType presentationType, boolean show) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#compareArtifacts(java.util.List, java.util.List, org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public void compareArtifacts(List<Artifact> baseArtifacts, List<Artifact> newerArtifacts, IProgressMonitor monitor, Branch branch, PresentationType presentationType) throws OseeCoreException {
      for (int i = 0; i < baseArtifacts.size(); i++) {
         compare(baseArtifacts.get(i), newerArtifacts.get(i), monitor, presentationType, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#supportsCompare()
    */
   public boolean supportsCompare() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#setRendererOptions(java.lang.String[])
    */
   @Override
   public void setOptions(VariableMap options) throws OseeArgumentException {
      this.options = options;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getOptions()
    */
   @Override
   public VariableMap getOptions() {
      return options;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getStringOption(java.lang.String)
    */
   @Override
   public String getStringOption(String key) throws OseeArgumentException {
      return options == null ? null : options.getString(key);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getBooleanOption(java.lang.String)
    */
   @Override
   public boolean getBooleanOption(String key) throws OseeArgumentException {
      if (options != null) {
         Boolean option = options.getBoolean(key);
         if (option != null) {
            return option;
         }
      }
      return false;
   }

   /* (non-Javadoc)
   * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
   */
   @Override
   public DefaultArtifactRenderer newInstance() throws OseeCoreException {
      return new DefaultArtifactRenderer();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#isValidFor(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (presentationType == PresentationType.GENERALIZED_EDIT) {
         return PRESENTATION_TYPE;
      }

      return DEFAULT_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#preview(java.util.List)
    */
   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      open(artifacts);
   }

   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      ArtifactEditor.editArtifacts(artifacts);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#minimumRanking()
    */
   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#renderAttribute(java.lang.String)
    */
   @Override
   public void renderAttribute(String attributeTypeName, Artifact artifact, PresentationType presentationType, Producer producer, VariableMap map, AttributeElement attributeElement) throws OseeCoreException {
      WordMLProducer wordMl = (WordMLProducer) producer;
      String format = attributeElement.getFormat();
      boolean allAttrs = map.getBoolean("allAttrs");

      wordMl.startParagraph();
      // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
      if (allAttrs) {
         wordMl.addWordMl("<w:r><w:t> " + attributeTypeName + ": </w:t></w:r>");
      } else {
         wordMl.addWordMl(attributeElement.getLabel());
      }

      String valueList = Collections.toString(", ", artifact.getAttributes(attributeTypeName));

      if (attributeElement.getFormat().contains(">x<")) {
         wordMl.addWordMl(format.replace(">x<", ">" + valueList + "<"));
      } else {
         wordMl.addTextInsideParagraph(valueList);
      }
      wordMl.endParagraph();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getImage()
    */
   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      return ImageManager.getImage(artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#rendererId()
    */
   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(2);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.artifacteditor.command");
      }

      return commandIds;
   }
}