/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;

public class WholeWordCompare extends AbstractWordCompare {
   private static final IAttributeType ATTRIBUTE_TYPE = CoreAttributeTypes.WholeWordContent;

   public WholeWordCompare(FileSystemRenderer renderer) {
      super(renderer);
   }

   @Override
   protected PresentationType getMergePresentationType() {
      return PresentationType.SPECIALIZED_EDIT;
   }

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, ArtifactDelta artifactDelta) throws OseeCoreException {
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;
      Pair<String, Boolean> oldAnnotationValue = null;

      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();

      Attribute<String> baseContent = getWordContent(baseArtifact, ATTRIBUTE_TYPE);
      Attribute<String> newerContent = getWordContent(newerArtifact, ATTRIBUTE_TYPE);

      if (!UserManager.getBooleanSetting(MsWordPreferencePage.REMOVE_TRACKED_CHANGES)) {
         oldAnnotationValue = removeAnnotations(baseContent);
         newAnnotationValue = removeAnnotations(newerContent);
      }

      if (!UserManager.getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
         originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
      }

      ArtifactDeltaToFileConverter converter = new ArtifactDeltaToFileConverter(getRenderer());
      Pair<IFile, IFile> compareFiles = converter.convertToFile(presentationType, artifactDelta);

      WordImageChecker.restoreOriginalValue(baseContent,
         oldAnnotationValue != null ? oldAnnotationValue : originalValue);
      WordImageChecker.restoreOriginalValue(newerContent, newAnnotationValue);
      return compare(baseArtifact, newerArtifact, compareFiles.getFirst(), compareFiles.getSecond(), presentationType);
   }

   @Override
   public void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas) throws OseeCoreException {
      for (ArtifactDelta entry : artifactDeltas) {
         compare(monitor, presentationType, entry);
      }
   }

   private Pair<String, Boolean> removeAnnotations(Attribute<String> attribute) throws OseeCoreException {
      Pair<String, Boolean> annotation = null;
      if (attribute != null) {
         String value = attribute.getValue();
         if (WordUtil.containsWordAnnotations(value)) {
            annotation = new Pair<String, Boolean>(value, attribute.isDirty());
            attribute.setFromString(WordUtil.removeAnnotations(value));
         }
      }
      return annotation;
   }

   private Attribute<String> getWordContent(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      Attribute<String> toReturn = null;
      if (artifact != null && !artifact.isDeleted()) {
         toReturn = artifact.getSoleAttribute(attributeType);
      }
      return toReturn;
   }

}
