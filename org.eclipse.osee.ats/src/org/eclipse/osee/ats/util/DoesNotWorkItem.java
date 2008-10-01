/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetUserArtifactCustomizeDefaults;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItem extends XNavigateItemAction {

   /**
    * @param parent
    */
   public DoesNotWorkItem(XNavigateItem parent) {
      super(parent, "Does Not Work - clean XViewerCustomization");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;

      cleanXViewerCustomizations();
      //      relateDonDunne();

      //      testDeleteAttribute();
      //      deleteNullUserAttributes();
      //      XNavigateItem item = AtsNavigateViewItems.getInstance().getSearchNavigateItems().get(1);
      //      System.out.println("Item " + item.getName());
      //      NavigateView.getNavigateView().handleDoubleClick(item);

      //      XResultData.runExample();

      // fixOseePeerReviews();

      AWorkbench.popup("Completed", "Complete");
   }

   private final boolean fixIt = false;

   public void cleanXViewerCustomizations() throws OseeCoreException {
      for (User user : SkynetAuthentication.getUsers()) {
         System.out.println("User: " + user);
         SkynetUserArtifactCustomizeDefaults custDefaults = new SkynetUserArtifactCustomizeDefaults(user);
         // Get all customizations
         List<String> customizations = user.getAttributesToStringList("XViewer Customization");
         if (customizations.size() == 0 && custDefaults.size() == 0) continue;
         Set<String> validGuids = new HashSet<String>();
         for (String custStr : new CopyOnWriteArrayList<String>(customizations)) {
            CustomizeData custData = new CustomizeData(custStr);
            Result result = isValidCustomizeData(custStr, custData);
            if (result.isTrue()) {
               validGuids.add(custData.getGuid());
            } else {
               OSEELog.logException(AtsPlugin.class,
                     "Removing invalid customization (" + result.getText() + ") " + custData.getGuid(), null, false);
               custDefaults.removeDefaultCustomization(custData);
               customizations.remove(custStr);
            }
         }
         if (validGuids.size() != custDefaults.getGuids().size()) {
            OSEELog.logException(AtsPlugin.class,
                  "Update default customizations : " + custDefaults.getGuids().size() + " valid: " + validGuids.size(),
                  null, false);
            custDefaults.setGuids(validGuids);
         }
         if (fixIt) {
            //         custDefaults.save();
            //         user.setAttributeValues("XViewer Customization", customizations);
            //         user.persistAttributes();
         }
      }
   }

   public Result isValidCustomizeData(String custDataStr, CustomizeData custData) {
      System.out.println("CustData: " + custDataStr);
      if (custDataStr.contains("<order>")) {
         return new Result("<order>");
      }
      return Result.TrueResult;
   }

   //   String xViewerDefaults = user.getSoleAttributeValueAsString("XViewer Defaults", null);
   //   // Get all current default guids
   //   Set<String> currentDefaultGuids = new HashSet<String>();
   //   if (xViewerDefaults != null) {
   //      for (String guid : AXml.getTagDataArray(xViewerDefaults, XVIEWER_DEFAULTS_TAG)) {
   //         if (guid != null && !guid.equals("")) {
   //            currentDefaultGuids.add(guid);
   //         }
   //      }
   //   }
   //   private void relateDonDunne()throws OseeCoreException, SQLException{
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork()throws OseeCoreException, SQLException{
   //            for (Artifact art : ArtifactQuery.getArtifactsFromAttribute(
   //                  ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
   //                  "%<" + SkynetAuthentication.getUser().getUserId() + ">%", BranchPersistenceManager.getAtsBranch())) {
   //               if ((art instanceof StateMachineArtifact) && ((StateMachineArtifact) art).getSmaMgr().getStateMgr().getAssignees().contains(
   //                     SkynetAuthentication.getUser())) {
   //                  art.addRelation(CoreRelationEnumeration.Users_User, SkynetAuthentication.getUser());
   //               }
   //            }
   //            SkynetAuthentication.getUser().persistRelations();
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }

   //   private void testDeleteAttribute() throws OseeCoreException, SQLException {
   //      Artifact art =
   //            ArtifactQuery.getArtifactsFromIds(Arrays.asList("76589"), AtsPlugin.getAtsBranch()).iterator().next();
   //      for (Attribute<?> attr : art.getAttributes()) {
   //         if (attr.getValue() == null) {
   //            System.out.println(art.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //            attr.delete();
   //         }
   //      }
   //      art.persistAttributes();
   //   }

   //   private void deleteNullAttributes() throws OseeCoreException, SQLException {
   //
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException, SQLException {
   //            int x = 0;
   //            for (String artTypeName : Arrays.asList(TeamWorkFlowArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
   //                  DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
   //                  "Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow", "Lba V13 Req Team Workflow",
   //                  "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
   //                  "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
   //                  "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
   //                  "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {
   //               for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsPlugin.getAtsBranch())) {
   //                  for (Attribute<?> attr : team.getAttributes(false)) {
   //                     if (attr.getValue() == null) {
   //                        System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //                        attr.delete();
   //                        x++;
   //                     }
   //                  }
   //                  if (team.isDirty()) team.persistAttributes();
   //               }
   //            }
   //            System.out.println("Deleted " + x);
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }
   //
   //   private void deleteNullUserAttributes() throws OseeCoreException, SQLException {
   //
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException, SQLException {
   //            int x = 0;
   //            for (String artTypeName : Arrays.asList(User.ARTIFACT_NAME)) {
   //               for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsPlugin.getAtsBranch())) {
   //                  for (Attribute<?> attr : team.getAttributes(false)) {
   //                     if (attr.getValue() == null) {
   //                        System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //                        attr.delete();
   //                        x++;
   //                     }
   //                  }
   //                  if (team.isDirty()) team.persistAttributes();
   //               }
   //            }
   //            System.out.println("Deleted " + x);
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }

   //   for (String artTypeName : Arrays.asList(TeamWorkFlowArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
   //         DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
   //         "Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow", "Lba V13 Req Team Workflow",
   //         "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
   //         "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
   //         "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
   //         "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {

}
