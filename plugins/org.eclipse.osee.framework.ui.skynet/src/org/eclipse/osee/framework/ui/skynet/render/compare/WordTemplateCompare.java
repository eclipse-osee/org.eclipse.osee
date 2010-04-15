/*
 * Created on Apr 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.VbaWordDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;

public class WordTemplateCompare implements IComparator {

   private final FileRenderer renderer;
   private final IAttributeType attributeType = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;

   public WordTemplateCompare(FileRenderer renderer) {
      this.renderer = renderer;
   }

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, Artifact baseArtifact, Artifact newerArtifact, boolean show) throws OseeCoreException {
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;
      Pair<String, Boolean> oldAnnotationValue = null;

      //Check for tracked changes
      Set<Artifact> artifacts = new HashSet<Artifact>();
      artifacts.addAll(RenderingUtil.checkForTrackedChangesOn(baseArtifact));
      artifacts.addAll(RenderingUtil.checkForTrackedChangesOn(newerArtifact));

      if (!artifacts.isEmpty()) {
         if (RenderingUtil.arePopupsAllowed()) {
            WordUiUtil.displayWarningMessageDialog("Diff Artifacts Warning",
                  "Detected tracked changes for some artifacts. Please refer to the results HTML report.");
            WordUiUtil.displayTrackedChangesOnArtifacts(artifacts);
         }
      } else {

         if (baseArtifact == null && newerArtifact == null) {
            throw new OseeArgumentException("baseVersion and newerVersion can't both be null.");
         }

         Attribute<String> baseContent = getWordContent(baseArtifact, attributeType);
         Attribute<String> newerContent = getWordContent(newerArtifact, attributeType);

         if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
            originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
         }

         Branch branch = baseArtifact != null ? baseArtifact.getBranch() : newerArtifact.getBranch();

         IFile baseFile = renderFile(monitor, renderer, baseArtifact, branch, presentationType);
         IFile newerFile = renderFile(monitor, renderer, newerArtifact, branch, presentationType);

         WordImageChecker.restoreOriginalValue(baseContent,
               oldAnnotationValue != null ? oldAnnotationValue : originalValue);
         WordImageChecker.restoreOriginalValue(newerContent, newAnnotationValue);

         return compare(baseArtifact, newerArtifact, baseFile, newerFile, presentationType, show);
      }
      return "";
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException {
      String diffPath;

      String fileName = renderer.getStringOption("fileName");
      if (!Strings.isValid(fileName)) {
         if (baseVersion != null) {
            String baseFileStr = baseFile.getLocation().toOSString();
            diffPath =
                  baseFileStr.substring(0, baseFileStr.lastIndexOf(')') + 1) + " to " + (newerVersion != null ? newerVersion.getTransactionNumber() : " deleted") + baseFileStr.substring(baseFileStr.lastIndexOf(')') + 1);
         } else {
            String baseFileStr = newerFile.getLocation().toOSString();
            diffPath =
                  baseFileStr.substring(0, baseFileStr.lastIndexOf('(') + 1) + "new " + baseFileStr.substring(baseFileStr.lastIndexOf('(') + 1);
         }
      } else {
         diffPath =
               RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.MERGE_EDIT).getLocation().toOSString() + '\\' + fileName;
      }

      VbaWordDiffGenerator diffGenerator = new VbaWordDiffGenerator();
      diffGenerator.initialize(presentationType == PresentationType.DIFF,
            presentationType == PresentationType.MERGE_EDIT);

      if (presentationType == PresentationType.MERGE_EDIT && baseVersion != null) {
         renderer.addFileToWatcher(RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.MERGE_EDIT),
               diffPath.substring(diffPath.lastIndexOf('\\') + 1));
         diffGenerator.addComparison(baseFile, newerFile, diffPath, true);
         diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "mergeDocs.vbs", show);
      } else {
         if (RenderingUtil.arePopupsAllowed()) {
            diffGenerator.addComparison(baseFile, newerFile, diffPath, false);
            diffGenerator.finish(diffPath.substring(0, diffPath.lastIndexOf('\\')) + "/compareDocs.vbs", show);
         }
      }

      return diffPath;
   }

   private IFile renderFile(IProgressMonitor monitor, FileRenderer renderer, Artifact artifact, Branch branch, PresentationType presentationType) throws OseeCoreException {
      IFile toReturn = null;
      if (presentationType == PresentationType.MERGE || presentationType == PresentationType.MERGE_EDIT) {
         toReturn = renderer.renderForMerge(monitor, artifact, branch, presentationType);
      } else {
         toReturn = renderer.renderForDiff(monitor, artifact, branch);
      }
      return toReturn;
   }

   private Attribute<String> getWordContent(Artifact artifact, IAttributeType attributeType) throws OseeCoreException {
      Attribute<String> toReturn = null;
      if (artifact != null) {
         toReturn = artifact.getSoleAttribute(attributeType);
      }
      return toReturn;
   }

   /**
    * Creates a difference report for each artifact between baseArtifact and newerArtifact. Then produces a single
    * report by combining each of the difference reports together for a single report.
    */
   @Override
   public void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<Pair<Artifact, Artifact>> artifactsToCompare) throws OseeCoreException {
      String reportDirName = renderer.getStringOption("diffReportFolderName");
      boolean isSuppressWord = renderer.getBooleanOption("suppressWord");

      IOperation operation = new WordChangeReportOperation(artifactsToCompare, renderer, reportDirName, isSuppressWord);
      Operations.executeWorkAndCheckStatus(operation, monitor, 1.0);
   }
}
