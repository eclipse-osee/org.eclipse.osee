/*
 * Created on May 25, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.navigate;

import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.support.test.util.DemoUsers;

/**
 * @author Donald G. Dunne
 */
public class SetJoeSmithAsAdminNavigateItem extends XNavigateItem {

   public SetJoeSmithAsAdminNavigateItem(XNavigateItem parent) {
      super(parent, "Set Joe Smith as ATS Admin", FrameworkImage.GEAR);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      User user = UserManager.getUser(DemoUsers.Joe_Smith);
      AtsUtilCore.getAtsAdminGroup().addMember(user);
      AtsUtilCore.getAtsAdminGroup().getGroupArtifact().persist(getName());
      AWorkbench.popup("Completed - Restart to see changes.");
   }

}
