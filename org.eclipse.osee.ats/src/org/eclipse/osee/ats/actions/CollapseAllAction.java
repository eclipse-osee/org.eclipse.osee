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
public class CollapseAllAction extends Action {

   private final ICollapseAllHandler iCollapseAllHandler;
   private TreeViewer treeViewer;

   public static interface ICollapseAllHandler {
      public void expandAllActionHandler();
   }

   public CollapseAllAction(TreeViewer treeViewer) {
      this((ICollapseAllHandler) null);
      this.treeViewer = treeViewer;
   }

   public CollapseAllAction(ICollapseAllHandler iCollapseActionHandler) {
      this.iCollapseAllHandler = iCollapseActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.COLLAPSE_ALL));
      setToolTipText("Collapse All");
   }

   @Override
   public void run() {
      try {
         if (treeViewer != null) {
            treeViewer.collapseAll();
         } else {
            iCollapseAllHandler.expandAllActionHandler();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
