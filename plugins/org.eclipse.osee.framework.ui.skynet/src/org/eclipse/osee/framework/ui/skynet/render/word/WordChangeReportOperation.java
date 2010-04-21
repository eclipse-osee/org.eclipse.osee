/*
 * Created on Apr 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

public class WordChangeReportOperation extends AbstractOperation {
   private final Collection<Change> changes;
   private final boolean suppressWord;
   private final String diffReportFolderName;

   public WordChangeReportOperation(Collection<Change> changes, boolean suppressWord, String diffReportFolderName) {
      super("Word Change Report", SkynetGuiPlugin.PLUGIN_ID);
      this.changes = changes;
      this.suppressWord = suppressWord;
      this.diffReportFolderName = diffReportFolderName;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      VariableMap variableMap = new VariableMap();
      variableMap.setValue("suppressWord", suppressWord);
      variableMap.setValue("diffReportFolderName", diffReportFolderName);

      Collection<Pair<Artifact, Artifact>> compareArtifacts = ChangeManager.getCompareArtifacts(changes);

      WordTemplateRenderer renderer = new WordTemplateRenderer();
      renderer.setOptions(variableMap);
      renderer.getComparator().compareArtifacts(monitor, PresentationType.DIFF, compareArtifacts);
   }
}