/*
 * Created on Sep 4, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ExpandAllAction extends Action {

   private final IExpandAllHandler iExpandAllHandler;
   private TreeViewer treeViewer = null;

   public static interface IExpandAllHandler {
      public void expandAllActionHandler();
   }

   public ExpandAllAction(TreeViewer treeViewer) {
      this((IExpandAllHandler) null);
      this.treeViewer = treeViewer;
   }

   public ExpandAllAction(IExpandAllHandler iRefreshActionHandler) {
      this.iExpandAllHandler = iRefreshActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EXPAND_ALL));
      setToolTipText("Expand All");
   }

   @Override
   public void run() {
      try {
         if (treeViewer != null) {
            treeViewer.expandAll();
         } else {
            iExpandAllHandler.expandAllActionHandler();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
