package org.eclipse.osee.framework.ui.skynet.artifact.editor.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class OpenHistoryAction extends Action {

   private final Artifact artifact;

   public OpenHistoryAction(Artifact artifact) {
      super();
      this.artifact = artifact;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DB_ICON_BLUE));
      setToolTipText("Show this artifact in the Resource History");
   }

   @Override
   public void run() {
      try {
         HistoryView.open(artifact);
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
