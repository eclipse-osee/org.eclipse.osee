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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
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
      super(parent, "Does Not Work - ??", FrameworkImage.ADMIN);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;

      //      for (Artifact art : ArtifactQuery.getArtifactsFromAttributeType("ats.Branch Id", AtsPlugin.getAtsBranch())) {
      //         int branchId = art.getSoleAttributeValue("ats.Branch Id");
      //         Branch branch = null;
      //         try {
      //            branch = BranchManager.getBranch(branchId);
      //         } catch (BranchDoesNotExist ex) {
      //            System.out.println("Branch does not exist for art " + art.getHumanReadableId() + " - " + art);
      //         } catch (Exception ex) {
      //            System.err.println("Exception getting branch for art " + art.getHumanReadableId() + " - " + art);
      //         }
      //         if (branch != null) {
      //            System.err.println("Branch DOES exist for art " + art.getHumanReadableId() + " - " + art);
      //         }
      //      }

      //      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      //      transaction.execute();

      //      convertAtsLogUserIds(transaction);
      //      deleteUnAssignedUserRelations();
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

   private void purgeHrids() throws OseeCoreException {
      String[] hrids = AFile.readFile("O:\\hrids_to_delete.txt").split("\r\n");
      int x = 0;
      for (String hrid : hrids) {
         hrid = hrid.replaceAll(" ", "");
         System.out.println("Processing " + x++ + " of " + hrids.length);
         try {
            Artifact art = null;
            // Handle case where duplicate hrids
            try {
               art = ArtifactQuery.getArtifactFromId(hrid, AtsPlugin.getAtsBranch());
            } catch (MultipleArtifactsExist ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               continue;
            }
            Set<Artifact> artsToDelete = new HashSet<Artifact>();
            if (art instanceof TeamWorkFlowArtifact) {
               art = ((TeamWorkFlowArtifact) art).getParentActionArtifact();
            }
            if (art instanceof ActionArtifact) {
               artsToDelete.add(art);
               artsToDelete.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
            }
            if (artsToDelete.size() > 0) {
               System.out.println("Sleeping 5 sec...");
               Thread.sleep(5000);
               System.out.println("Purging " + artsToDelete.size() + " artifacts...");
               ArtifactPersistenceManager.purgeArtifacts(artsToDelete);
            }
         } catch (ArtifactDoesNotExist ex) {
            System.out.println("Artifact with hrid does not exist: " + hrid);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

      }

   }

   private void convertAtsLogUserIds(SkynetTransaction transaction) throws OseeCoreException {
      List<String> hrids =
            Arrays.asList("NKYBF", "J1Z48", "ZY4W5", "U9H58", "9713S", "83XVW", "59B9X", "TQD1J", "UVM7U", "HZT73",
                  "C49Q5", "RHCPY", "MBCXV", "YJFKC", "2F461", "AGW15", "K6ZGD", "9W45V", "GG43L", "G2VTQ", "CVWFC",
                  "EXMT0", "W1TS8", "JM3RD", "7Q0W3", "P9DKR", "BR2RN", "Z6B0Z", "6KT6U", "HPQJX", "QN2K3", "W0VTD",
                  "LDJMH", "6PWYH", "T8B4K", "YTNLC", "9557A", "SQQ6T", "D82X9", "2P5GC", "YK58N", "LWVT1", "KCGSQ",
                  "5X2WL", "C8HWW");
      for (Artifact art : ArtifactQuery.getArtifactsFromIds(hrids, AtsPlugin.getAtsBranch())) {
         String str = art.getSoleAttributeValue(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), null);
         str = str.replaceAll("rj236c", "1779483");
         art.setSoleAttributeFromString(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), str);
         art.persistAttributes(transaction);
      }
   }

   private void fixTestTaskResolutions() throws OseeCoreException {
      System.out.println("Started fixTestTaskResolutions...");
      for (Artifact artifact : ArtifactQuery.getArtifactsFromAttributeType(
            ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), AtsPlugin.getAtsBranch())) {
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArt = (TaskArtifact) artifact;
            String resolution =
                  ((TaskArtifact) artifact).getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(),
                        null);
            if (resolution == null) {
               System.err.println("Unexpected null resolution." + taskArt.getHumanReadableId());
               //               taskArt.deleteSoleAttribute(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
               //               taskArt.persistAttributes();
            } else {
               String newResolution = null;
               if (resolution.equals("Need_DTE_Test")) {
                  System.out.println("Rename Need_DTE_Test to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               } else if (resolution.equals("Awaiting_Code_Fix")) {
                  System.out.println("Rename Awaiting_Code_Fix to Awaiting_Code " + taskArt.getHumanReadableId());
                  newResolution = "Awaiting_Code";
               } else if (resolution.equals("Awaiting_Review")) {
                  System.out.println("Rename Awaiting_Review to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               } else if (resolution.equals("Unit_Tested")) {
                  System.out.println("Rename Unit_Tested to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               }
               if (newResolution != null) {
                  taskArt.setSoleAttributeFromString(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), newResolution);
                  taskArt.persistAttributes();
               }
            }
         }
      }
      System.out.println("Completed fixTestTaskResolutions...");
   }

   //   private void deleteUnAssignedUserRelations() throws OseeCoreException {
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsPlugin.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
   //            User unassignedUser = SkynetAuthentication.getUser(UserEnum.UnAssigned);
   //            for (Artifact art : unassignedUser.getRelatedArtifacts(CoreRelationEnumeration.Users_Artifact)) {
   //               if (art instanceof StateMachineArtifact) {
   //                  unassignedUser.deleteRelation(CoreRelationEnumeration.Users_Artifact, art);
   //               }
   //            }
   //            unassignedUser.persistRelations();
   //         }
   //      };
   //      newActionTx.execute();
   //   }

   private final boolean fixIt = false;

   //   public void cleanXViewerCustomizations() throws OseeCoreException {
   //      for (User user : SkynetAuthentication.getUsers()) {
   //         System.out.println("User: " + user);
   //
   //         SkynetUserArtifactCustomizeDefaults custDefaults = new SkynetUserArtifactCustomizeDefaults(user);
   //
   //         // Get all customizations
   //         List<String> customizations = user.getAttributesToStringList("XViewer Customization");
   //         if (customizations.size() == 0 && custDefaults.size() == 0) continue;
   //         Set<String> validGuids = new HashSet<String>();
   //         int currNumDefaults = custDefaults.getGuids().size();
   //         for (String custStr : new CopyOnWriteArrayList<String>(customizations)) {
   //            CustomizeData custData = new CustomizeData(custStr);
   //            validGuids.add(custData.getGuid());
   //
   //            // check for old customizations to remove
   //            boolean orderFound = custStr.contains("<order>");
   //            boolean namespaceNullFound = custStr.contains("namespace=\"null\"");
   //            if (orderFound || namespaceNullFound) {
   //               System.err.println("Removing " + (orderFound ? "<order>" : "namespace==null") + " customizations " + custData.getGuid());
   //               validGuids.remove(custData.getGuid());
   //               custDefaults.removeDefaultCustomization(custData);
   //               customizations.remove(custStr);
   //            } else {
   //               // Check for sort columns that are hidden
   //               for (String columnName : custData.getSortingData().getSortingNames()) {
   //                  XViewerColumn xCol = custData.getColumnData().getXColumn(columnName);
   //                  if (xCol == null) {
   //                     System.err.println("sort column not found \"" + columnName + "\" - " + custData.getGuid());
   //                  } else if (xCol.isShow() == false) {
   //                     System.err.println("sort col is hidden \"" + columnName + "\" - " + custData.getGuid());
   //                  }
   //               }
   //            }
   //         }
   //         if (validGuids.size() != custDefaults.getGuids().size()) {
   //            System.err.println("Update default customizations : " + user + " - " + currNumDefaults + " valid: " + validGuids.size());
   //            custDefaults.setGuids(validGuids);
   //         }
   //         if (fixIt) {
   //            custDefaults.save();
   //            user.setAttributeValues("XViewer Customization", customizations);
   //            user.persistAttributes();
   //         }
   //      }
   //   }

   public Result isCustomizationSortErrored(String custDataStr, CustomizeData custData) {

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
   //   private void relateDonDunne()throws OseeCoreException{
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsPlugin.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork()throws OseeCoreException{
   //            for (Artifact art : ArtifactQuery.getArtifactsFromAttribute(
   //                  ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
   //                  "%<" + SkynetAuthentication.getUser().getUserId() + ">%", AtsPlugin.getAtsBranch())) {
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

   //   private void testDeleteAttribute() throws OseeCoreException {
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

   //   private void deleteNullAttributes() throws OseeCoreException {
   //
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsPlugin.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
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
   //   private void deleteNullUserAttributes() throws OseeCoreException {
   //
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsPlugin.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
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
