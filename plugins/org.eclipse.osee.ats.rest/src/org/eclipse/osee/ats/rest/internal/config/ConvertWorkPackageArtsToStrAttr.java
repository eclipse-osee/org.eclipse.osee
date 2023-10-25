/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * See getDescription() below
 *
 * @author Donald G Dunne
 */
public class ConvertWorkPackageArtsToStrAttr implements IAtsDatabaseConversion {

   private static final String WFSEXIST = "wfsexist";
   private final OrcsApi orcsApi;
   private final boolean debug = false;
   private final String TITLE = "Convert Work Packages to String Attribute";
   private AtsApi atsApi;
   private final Map<ArtifactId, ArtifactToken> idToWorkPackage = new HashMap<>();

   public ConvertWorkPackageArtsToStrAttr(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public void run(XResultData rd, boolean reportOnly, AtsApi atsApi) {
      this.atsApi = atsApi;

      List<ArtifactToken> allWorkPackArts = atsApi.getQueryService().getArtifacts(AtsArtifactTypes.WorkPackage);

      validateWorkPackages(allWorkPackArts, rd);

      List<ArtifactToken> referencedWorkPackArts = deleteUnusedWorkPackages(allWorkPackArts, rd, reportOnly, atsApi);

      convertWorkItemsToStringAttr(referencedWorkPackArts, rd, reportOnly, atsApi);

      rd.log("Complete");
   }

   private void validateWorkPackages(List<ArtifactToken> workPackArts, XResultData rd) {
      for (ArtifactToken workPackArt : workPackArts) {
         try {
            Pair<String, String> idAndFull = getId(workPackArt);
            System.err.println("Id: " + idAndFull.getFirst());
         } catch (Exception ex) {
            System.err.println("Exception on " + workPackArt.getId());
         }
      }
   }

   private Pair<String, String> getId(ArtifactToken workPackArt) {
      String activityId =
         atsApi.getAttributeResolver().getSoleAttributeValue(workPackArt, AtsAttributeTypes.ActivityId, "Unknown");
      String workPkgId =
         atsApi.getAttributeResolver().getSoleAttributeValue(workPackArt, AtsAttributeTypes.WorkPackageId, "Unknown");

      String id = String.format("wpid: %s actId: %s artId: %s", workPkgId, activityId, workPackArt.getIdString());
      return new Pair<String, String>(id, id);
   }

   private List<ArtifactToken> deleteUnusedWorkPackages(List<ArtifactToken> workPackArts, XResultData rd,
      boolean reportOnly, AtsApi atsApi) {

      List<ArtifactToken> referencedWorkPackArts = new ArrayList<>();

      int size = workPackArts.size();
      int count = 1;
      List<Collection<ArtifactToken>> subDivide = Collections.subDivide(workPackArts, 200);
      for (Collection<ArtifactToken> workPackArtsColl : subDivide) {
         IAtsChangeSet changes = null;
         if (!reportOnly) {
            changes = atsApi.createChangeSet(getName() + " - Delete", AtsCoreUsers.SYSTEM_USER);
         }
         for (ArtifactToken workPackArt : workPackArtsColl) {
            System.err.println(String.format("Processing %s/%s...", count++, size));
            if (atsApi.getAttributeResolver().hasTag(workPackArt, WFSEXIST)) {
               System.err.println("---> Skipping already processed: " + workPackArt.getIdString());
               referencedWorkPackArts.add(workPackArt);
               continue;
            }

            ElapsedTime time3 = new ElapsedTime("Load work package wfs");
            Collection<ArtifactToken> wfArts =
               atsApi.getQueryService().getArtifacts(AtsArtifactTypes.AbstractWorkflowArtifact,
                  AtsAttributeTypes.WorkPackageReference, workPackArt.getIdString(), atsApi.getAtsBranch());
            System.err.println("orcs query 2 found " + wfArts.size());
            time3.end(Units.SEC);

            String msg =
               String.format("Work Package %s found %s workItems\n", workPackArt.getIdString(), wfArts.size());
            System.err.print(msg);
            rd.logf(msg);
            if (wfArts.isEmpty()) {
               String delMsg = "---> Need to delete work package " + workPackArt.getIdString();
               System.err.println(delMsg);
               rd.logf(delMsg);
               if (!reportOnly) {
                  changes.deleteArtifact(workPackArt);
               }

            } else {
               if (!reportOnly) {
                  changes.addTag(workPackArt, WFSEXIST);
                  referencedWorkPackArts.add(workPackArt);
               }
            }
         }
         if (!reportOnly && !changes.isEmpty()) {
            TransactionToken tx = changes.execute();
            rd.logf("Transaction %s\n", tx.getIdString());
         }
      }
      return referencedWorkPackArts;
   }

   private void convertWorkItemsToStringAttr(List<ArtifactToken> referencedWorkPackArts, XResultData rd,
      boolean reportOnly, AtsApi atsApi) {
      try {
         int size = referencedWorkPackArts.size();
         int count = 1;
         List<Collection<ArtifactToken>> subDivide = Collections.subDivide(referencedWorkPackArts, 400);
         for (Collection<ArtifactToken> workPackArtsColl : subDivide) {
            IAtsChangeSet changes = null;
            if (!reportOnly) {
               changes = atsApi.createChangeSet(getName() + " - Convert", AtsCoreUsers.SYSTEM_USER);
            }

            for (ArtifactToken workPackArt : workPackArtsColl) {
               System.err.println(String.format("Convert: Processing %s/%s...", count++, size));

               ElapsedTime time3 = new ElapsedTime("Load work package wfs");
               Collection<ArtifactToken> workflowArts =
                  atsApi.getQueryService().getArtifacts(AtsArtifactTypes.AbstractWorkflowArtifact,
                     AtsAttributeTypes.WorkPackageReference, workPackArt.getIdString(), atsApi.getAtsBranch());
               System.err.println("query found " + workflowArts.size() + " for wp " + workPackArt.toStringWithId());
               time3.end(Units.SEC);

               if (workflowArts.isEmpty()) {
                  if (!reportOnly) {
                     changes.deleteArtifact(workPackArt);
                     System.err.println("---> Deleted unused work package: " + workPackArt.toStringWithId());
                  } else {
                     System.err.println("---> Delete unused work package: " + workPackArt.toStringWithId());
                  }
               }

               for (ArtifactToken workflowArt : workflowArts) {
                  Pair<String, String> idAndFull = getId(workPackArt);
                  String storeId = idAndFull.getFirst();

                  String msg =
                     String.format("Work Package [%s] to be set on %s", storeId, workflowArt.toStringWithId());
                  System.err.println(msg);
                  rd.log(msg + "\n");

                  IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(workflowArt);
                  if (workItem.isCompletedOrCancelled()) {
                     if (reportOnly) {
                        System.err.println("---> need to set and removed ref");
                     } else {
                        changes.setSoleAttributeValue(workflowArt, AtsAttributeTypes.WorkPackage, storeId);
                        changes.deleteAttributes(workflowArt, AtsAttributeTypes.WorkPackageReference);
                        System.err.println("---> set and removed ref");
                     }
                  } else {
                     System.err.println("---> skipped cause inwork");
                  }
               }
            }

            if (!reportOnly && !changes.isEmpty()) {
               TransactionToken tx = changes.execute();
               System.err.println("Transaction: " + tx.getIdString());
               rd.logf("Transaction %s\n", tx.getIdString());
            }
         }

      } catch (Exception ex) {
         System.err.println(Lib.exceptionToString(ex));
         rd.errorf("Exception processing ... %s\n", ex.getLocalizedMessage());
      }
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("Convert Work Packages to String Attribute");
      data.append("This will create new attrs as needed and delete work package artifacts.\n" //
         + "Can be run multiple times without corruption.\n" //
         + "Should be run periodically on 0.26.11\n");
      return data.toString();
   }

   @Override
   public String getName() {
      return TITLE;
   }
}