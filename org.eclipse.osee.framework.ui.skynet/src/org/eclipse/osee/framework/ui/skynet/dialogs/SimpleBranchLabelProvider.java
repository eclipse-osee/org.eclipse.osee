/*
 * Created on Jan 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dialogs;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public final class SimpleBranchLabelProvider extends LabelProvider {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(SimpleBranchLabelProvider.class);
   private static final Image PARENT_BRANCH_IMAGE = SkynetGuiPlugin.getInstance().getImage("branch.gif");
   private static final Image CHILD_BRANCH_IMAGE = SkynetGuiPlugin.getInstance().getImage("change_managed_branch.gif");

   public Image getImage(Object arg0) {
      Image toReturn = null;
      if (arg0 instanceof Branch) {
         Branch branch = ((Branch) arg0);
         if (branch != null) {
            try {
               toReturn = branch.getParentBranch() != null ? CHILD_BRANCH_IMAGE : PARENT_BRANCH_IMAGE;
            } catch (SQLException ex) {
               toReturn = PARENT_BRANCH_IMAGE;
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
         }
      }
      return toReturn;
   }

   public String getText(Object arg0) {
      String toReturn = "";
      if (arg0 instanceof Branch) {
         Branch branch = ((Branch) arg0);
         if (branch != null) {
            toReturn = branch.getBranchName();
         }
      }
      return toReturn;
   }
}
