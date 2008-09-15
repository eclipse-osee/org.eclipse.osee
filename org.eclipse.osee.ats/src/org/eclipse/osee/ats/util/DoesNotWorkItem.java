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

import java.sql.SQLException;
import java.util.Arrays;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItem extends XNavigateItemAction {

   /**
    * @param parent
    */
   public DoesNotWorkItem(XNavigateItem parent) {
      super(parent, "Does Not Work - deleteNullAttributes");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException, SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;

      //      relateDonDunne();

      //      testDeleteAttribute();
      deleteNullUserAttributes();
      //      XNavigateItem item = AtsNavigateViewItems.getInstance().getSearchNavigateItems().get(1);
      //      System.out.println("Item " + item.getName());
      //      NavigateView.getNavigateView().handleDoubleClick(item);

      //      XResultData.runExample();

      // fixOseePeerReviews();

      AWorkbench.popup("Completed", "Complete");
   }

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

   private void deleteNullAttributes() throws OseeCoreException, SQLException {

      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {

         @Override
         protected void handleTxWork() throws OseeCoreException, SQLException {
            int x = 0;
            for (String artTypeName : Arrays.asList(TeamWorkFlowArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
                  DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
                  "Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow", "Lba V13 Req Team Workflow",
                  "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
                  "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
                  "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
                  "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {
               for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsPlugin.getAtsBranch())) {
                  for (Attribute<?> attr : team.getAttributes(false)) {
                     if (attr.getValue() == null) {
                        System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
                        attr.delete();
                        x++;
                     }
                  }
                  if (team.isDirty()) team.persistAttributes();
               }
            }
            System.out.println("Deleted " + x);
         }
      };
      newActionTx.execute();

   }

   private void deleteNullUserAttributes() throws OseeCoreException, SQLException {

      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {

         @Override
         protected void handleTxWork() throws OseeCoreException, SQLException {
            int x = 0;
            for (String artTypeName : Arrays.asList(User.ARTIFACT_NAME)) {
               for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsPlugin.getAtsBranch())) {
                  for (Attribute<?> attr : team.getAttributes(false)) {
                     if (attr.getValue() == null) {
                        System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
                        attr.delete();
                        x++;
                     }
                  }
                  if (team.isDirty()) team.persistAttributes();
               }
            }
            System.out.println("Deleted " + x);
         }
      };
      newActionTx.execute();

   }

   //   for (String artTypeName : Arrays.asList(TeamWorkFlowArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
   //         DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
   //         "Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow", "Lba V13 Req Team Workflow",
   //         "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
   //         "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
   //         "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
   //         "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {

}
