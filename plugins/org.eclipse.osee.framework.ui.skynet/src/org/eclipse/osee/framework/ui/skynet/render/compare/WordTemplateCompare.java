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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.VbaWordDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;

public class WordTemplateCompare implements IComparator {
   private static final IAttributeType ATTRIBUTE_TYPE = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;

   private final ArtifactDeltaToFileConverter converter;

   public WordTemplateCompare(FileSystemRenderer renderer) {
      this.converter = new ArtifactDeltaToFileConverter(renderer);
   }

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, ArtifactDelta delta, boolean show) throws OseeCoreException {
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;

      Artifact baseArtifact = delta.getStartArtifact();
      Artifact newerArtifact = delta.getEndArtifact();

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

         Attribute<String> baseContent = getWordContent(baseArtifact, ATTRIBUTE_TYPE);
         Attribute<String> newerContent = getWordContent(newerArtifact, ATTRIBUTE_TYPE);

         if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
            originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
         }

         Pair<IFile, IFile> compareFiles = converter.convertToFile(presentationType, delta);

         WordImageChecker.restoreOriginalValue(baseContent, originalValue);
         WordImageChecker.restoreOriginalValue(newerContent, newAnnotationValue);

         return compare(baseArtifact, newerArtifact, compareFiles.getFirst(), compareFiles.getSecond(),
               presentationType, show);
      }
      return "";
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException {
      String diffPath;

      String fileName = converter.getRenderer().getStringOption("fileName");
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
         IFolder folder = RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.MERGE_EDIT);
         diffPath = folder.getLocation().toOSString() + '\\' + fileName;
      }

      VbaWordDiffGenerator diffGenerator = new VbaWordDiffGenerator();
      diffGenerator.initialize(presentationType == PresentationType.DIFF,
            presentationType == PresentationType.MERGE_EDIT);

      if (presentationType == PresentationType.MERGE_EDIT && baseVersion != null) {
         IFolder folder = RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.MERGE_EDIT);
         converter.getRenderer().addFileToWatcher(folder, diffPath.substring(diffPath.lastIndexOf('\\') + 1));
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
   public void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<ArtifactDelta> artifactsToCompare) throws OseeCoreException {
      String reportDirName = converter.getRenderer().getStringOption("diffReportFolderName");
      boolean isSuppressWord = converter.getRenderer().getBooleanOption("suppressWord");

      IOperation operation =
            new WordChangeReportOperation(artifactsToCompare, converter, reportDirName, isSuppressWord);
      Operations.executeWorkAndCheckStatus(operation, monitor, 1.0);
   }
}
