/*
 * Created on Aug 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.operation;

import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class EditTeamsNavigateItem extends XNavigateItemBlam {

   /**
    * @param parent
    * @param blamOperation
    */
   public EditTeamsNavigateItem(XNavigateItem parent) {
      super(parent, new EditTeamWorkflows());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem#getName()
    */
   @Override
   public String getName() {
      return "Team Workflow Search";
   }

}
