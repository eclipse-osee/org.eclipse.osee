/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.demotest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateXWidgetExampleAction extends XNavigateItemAction {

   public CreateXWidgetExampleAction(XNavItemCat... xNavItemCat) {
      super("Create Demo Action with XWidget Examples (Website AI) - Admin", PluginUiImage.ADMIN, xNavItemCat);
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(AtsUserGroups.AtsAdmin);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      try {
         AtsApi atsApi = AtsApiService.get();
         // Clear out config cache to ensure only get live configs
         atsApi.reloadServerAndClientCaches();

         Set<IAtsActionableItem> aias = new HashSet<>();
         IAtsActionableItem aia = AtsApiService.get().getConfigService().getConfigurations().getIdToAi().get(
            DemoArtifactToken.SAW_PL_Requirements_AI.getId());
         aias.add(aia);

         String opName = CreateXWidgetExampleAction.class.getSimpleName();
         NewActionData data = atsApi.getActionService() //
            .createActionData(opName, "XWidget Example Action", "Description") //
            .andAis(aias) //
            .andChangeType(ChangeTypes.Improvement) //
            .andPriority("3") //
            .andWorkDef(DemoWorkDefinitions.WorkDefTeamXDemoWidgetTest);
         for (String staticId : Arrays.asList("my.static.id.1", "my.static.id.2", "another.static.id",
            "last.static.id")) {
            data.addAttrValue(CoreAttributeTypes.StaticId, staticId);
         }
         for (String pcrId : Arrays.asList("ATS2334", "TW432432\nNow is the time\nFor all", "TSK 234\nDOORS33",
            "PR234234")) {
            data.addAttrValue(AtsAttributeTypes.PcrId, pcrId);
         }
         NewActionData newActionData = atsApi.getActionService().createAction(data);
         if (newActionData.getRd().isErrors()) {
            XResultDataUI.report(newActionData.getRd(), opName);
         }

         AtsEditors.openATSAction(newActionData.getActResult().getAtsTeamWfs().iterator().next().getStoreObject(),
            AtsOpenOption.OpenOneOrPopupSelect);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
