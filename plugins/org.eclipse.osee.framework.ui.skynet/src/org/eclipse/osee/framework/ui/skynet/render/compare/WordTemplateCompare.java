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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;

public class WordTemplateCompare extends AbstractWordCompare {
   private static final IAttributeType ATTRIBUTE_TYPE = CoreAttributeTypes.WordTemplateContent;

   public WordTemplateCompare(FileSystemRenderer renderer) {
      super(renderer);
   }

   @Override
   protected PresentationType getMergePresentationType() {
      return PresentationType.MERGE_EDIT;
   }

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, ArtifactDelta artifactDelta) throws OseeCoreException {
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;

      Artifact baseArtifact = artifactDelta.getStartArtifact();
      Artifact newerArtifact = artifactDelta.getEndArtifact();

      //Check for tracked changes
      Set<Artifact> artifacts = new HashSet<Artifact>();
      artifacts.addAll(RenderingUtil.checkForTrackedChangesOn(baseArtifact));
      artifacts.addAll(RenderingUtil.checkForTrackedChangesOn(newerArtifact));

      if (!artifacts.isEmpty()) {
         if (RenderingUtil.arePopupsAllowed() || !getRenderer().getBooleanOption(IRenderer.SKIP_DIALOGS)) {
            WordUiUtil.displayWarningMessageDialog("Diff Artifacts Warning",
               "Detected tracked changes for some artifacts. Please refer to the results HTML report.");
            WordUiUtil.displayTrackedChangesOnArtifacts(artifacts);
         } else {
            OseeLog.log(SkynetGuiPlugin.class, Level.INFO,
               String.format("Test - Skipping - Detected tracked changes for some artifacts for [%s]", artifacts));
         }
      } else {
         Attribute<String> baseContent = getWordContent(baseArtifact, ATTRIBUTE_TYPE);
         Attribute<String> newerContent = getWordContent(newerArtifact, ATTRIBUTE_TYPE);

         if (!UserManager.getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
            originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
         }

         ArtifactDeltaToFileConverter converter = new ArtifactDeltaToFileConverter(getRenderer());
         Pair<IFile, IFile> compareFiles = converter.convertToFile(presentationType, artifactDelta);

         WordImageChecker.restoreOriginalValue(baseContent, originalValue);
         WordImageChecker.restoreOriginalValue(newerContent, newAnnotationValue);

         return compare(baseArtifact, newerArtifact, compareFiles.getFirst(), compareFiles.getSecond(),
            presentationType);
      }
      return "";
   }

   private Attribute<String> getWordContent(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      Attribute<String> toReturn = null;
      if (artifact != null && !artifact.isDeleted()) {
         toReturn = artifact.getSoleAttribute(attributeType);
      }
      return toReturn;
   }

   /**
    * Creates a difference report for each artifact between baseArtifact and newerArtifact. Then produces a single
    * report by combining each of the difference reports together for a single report.
    */
   @Override
   public void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<ArtifactDelta> artifactDeltas) throws OseeCoreException {
      IOperation operation = new WordChangeReportOperation(artifactDeltas, getRenderer());
      Operations.executeWorkAndCheckStatus(operation, monitor);
   }

}
