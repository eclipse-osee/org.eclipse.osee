/*
 * Created on Oct 25, 2006
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.eclipse.osee.ats.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.core.action.ActionManager;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.core.workflow.ChangeType;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateActionUsingAllActionableItems extends XNavigateItemAction {

   public CreateActionUsingAllActionableItems(XNavigateItem parent) {
      super(parent, "Create Action Using All Actionable Items - Admin", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      try {
         Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
         for (ActionableItemArtifact aia : ActionableItemManagerCore.getActionableItems(Active.Active)) {
            if (aia.isActionable()) {
               aias.add(aia);
            }
         }

         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Create Action using all AIs");
         ActionManager.createAction(null, "Big Action", "Description", ChangeType.Improvement, "1", false, null, aias,
            new Date(), UserManager.getUser(), null, transaction);
         transaction.execute();

         AWorkbench.popup("Completed", "Completed");

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
