/*
 * Created on Apr 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.VbaWordDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;

public class WholeWordCompare implements IComparator {

   private final FileRenderer renderer;

   public WholeWordCompare(FileRenderer renderer) {
      this.renderer = renderer;
   }

   @Override
   public String compare(IProgressMonitor monitor, PresentationType presentationType, Artifact baseVersion, Artifact newerVersion, boolean show) throws OseeCoreException {
      if (baseVersion == null && newerVersion == null) {
         throw new IllegalArgumentException("baseVersion and newerVersion can't both be null.");
      }
      Pair<String, Boolean> originalValue = null;
      Pair<String, Boolean> newAnnotationValue = null;
      Pair<String, Boolean> oldAnnotationValue = null;

      Attribute<String> baseContent = getWordContent(baseVersion, CoreAttributeTypes.WHOLE_WORD_CONTENT);
      Attribute<String> newerContent = getWordContent(newerVersion, CoreAttributeTypes.WHOLE_WORD_CONTENT);

      if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.REMOVE_TRACKED_CHANGES)) {
         oldAnnotationValue = removeAnnotations(baseContent);
         newAnnotationValue = removeAnnotations(newerContent);
      }

      if (!UserManager.getUser().getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
         originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
      }

      Branch branch = baseVersion != null ? baseVersion.getBranch() : newerVersion.getBranch();
      IFile baseFile = renderFile(monitor, renderer, baseVersion, branch, presentationType);
      IFile newerFile = renderFile(monitor, renderer, newerVersion, branch, presentationType);

      WordImageChecker.restoreOriginalValue(baseContent,
            oldAnnotationValue != null ? oldAnnotationValue : originalValue);
      WordImageChecker.restoreOriginalValue(newerContent, newAnnotationValue);
      return compare(baseVersion, newerVersion, baseFile, newerFile, presentationType, show);
   }

   @Override
   public String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException {
      String diffPath;
      String fileName = renderer.getStringOption("fileName");
      if (fileName == null || fileName.equals("")) {
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
               RenderingUtil.getRenderFolder(baseVersion.getBranch(), PresentationType.SPECIALIZED_EDIT).getLocation().toOSString() + '\\' + fileName;
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

   @Override
   public void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<Pair<Artifact, Artifact>> itemsToCompare) throws OseeCoreException {
      for (Pair<Artifact, Artifact> entry : itemsToCompare) {
         compare(monitor, presentationType, entry.getFirst(), entry.getSecond(), true);
      }
   }

   private Pair<String, Boolean> removeAnnotations(Attribute<String> attribute) throws OseeCoreException {
      Pair<String, Boolean> annotation = null;
      if (attribute != null) {
         String value = attribute.getValue();
         if (WordAnnotationHandler.containsWordAnnotations(value)) {
            annotation = new Pair<String, Boolean>(value, attribute.isDirty());
            attribute.setFromString(WordAnnotationHandler.removeAnnotations(value));
         }
      }
      return annotation;
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

}
