package org.eclipse.osee.framework.ui.skynet.change.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.change.IChangeReportView;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class ReloadChangeReportAction extends Action {

   private final IChangeReportView view;

   public ReloadChangeReportAction(IChangeReportView view) {
      super("Reload Change Report", Action.AS_PUSH_BUTTON);
      setId("reload.change.report");
      setToolTipText("Reloads the change report");
      setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      this.view = view;
   }

   @Override
   public void run() {
      view.recomputeChangeReport();
   }
}