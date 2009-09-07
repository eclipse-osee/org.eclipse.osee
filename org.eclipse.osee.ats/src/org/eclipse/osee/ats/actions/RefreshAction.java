/*
 * Created on Sep 4, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class RefreshAction extends Action {

   private final IRefreshActionHandler iRefreshActionHandler;

   public static interface IRefreshActionHandler {
      public void refreshActionHandler();
   }

   public RefreshAction(IRefreshActionHandler iRefreshActionHandler) {
      this.iRefreshActionHandler = iRefreshActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
      setToolTipText("Refresh");
   }

   @Override
   public void run() {
      try {
         iRefreshActionHandler.refreshActionHandler();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
