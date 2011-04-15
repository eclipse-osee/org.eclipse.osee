/*
 * Created on Apr 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.goal;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactCheckTreeDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class GoalCheckTreeDialog extends ArtifactCheckTreeDialog {

   public GoalCheckTreeDialog(Collection<? extends Artifact> artifacts) {
      super(artifacts, new GoalLabelProvider());
      setTitle("Select Goals");

   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);
      getTreeViewer().setSorter(new GoalViewerSorter());
      return control;
   }

}
