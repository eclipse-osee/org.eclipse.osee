package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public final class DirtyReportAction extends Action {
   private final Artifact artifact;

   public DirtyReportAction(Artifact artifact) {
      super();
      this.artifact = artifact;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DIRTY));
      setToolTipText("&Dirty Report");
      setText("&Dirty Report");
   }

   @Override
   public void run() {
      String rString = Artifacts.getDirtyReport(artifact);
      AWorkbench.popup("Dirty Report", !Strings.isValid(rString) ? "Not Dirty" : "Dirty -> " + rString);
   }
}
