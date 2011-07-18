/*
 * Created on Jul 14, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.walker.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.walker.ActionWalkerView;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenActionViewAction extends Action {

   public OpenActionViewAction() {
      super("Open Action View");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ACTION));
   }

   @Override
   public void run() {
      try {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ActionWalkerView.VIEW_ID);
      } catch (PartInitException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Unable to open outline", ex);
      }
   }

}
