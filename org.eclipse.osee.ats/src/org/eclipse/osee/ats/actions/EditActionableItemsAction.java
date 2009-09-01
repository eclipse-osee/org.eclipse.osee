/*
 * Created on Aug 31, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EditActionableItemsAction extends Action {

   private final TeamWorkFlowArtifact teamWf;

   public EditActionableItemsAction(TeamWorkFlowArtifact teamWf) {
      super("Add/Update Actionable Items/Workflows");
      this.teamWf = teamWf;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ACTIONABLE_ITEM));
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.action.Action#run()
    */
   @Override
   public void run() {
      try {
         AtsUtil.editActionableItems(teamWf.getParentActionArtifact());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
