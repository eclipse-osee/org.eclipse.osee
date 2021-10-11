/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.rest.internal.cpa;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.cpa.DuplicateCpa;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CpaDuplicator {

   private final AtsApi atsApi;
   private final DuplicateCpa duplicate;
   private final CpaServiceRegistry cpaRegistry;

   public CpaDuplicator(final DuplicateCpa duplicate, AtsApi atsApi, CpaServiceRegistry cpaRegistry) {
      this.duplicate = duplicate;
      this.atsApi = atsApi;
      this.cpaRegistry = cpaRegistry;
   }

   public XResultData duplicate() {
      XResultData rd = new XResultData(false);
      ArtifactReadable cpaArt = (ArtifactReadable) atsApi.getQueryService().getArtifactById(duplicate.getCpaId());
      String atsId = cpaArt.getSoleAttributeValue(AtsAttributeTypes.AtsId, null);
      String duplicatePcrId = "";
      if (!Strings.isValid(atsId)) {
         rd.errorf("AtsId %s is not valid.  Skipping.", atsId);
      } else {
         if (cpaArt.getSoleAttributeValue(AtsAttributeTypes.ApplicabilityWorkflow, false)) {
            String toolId = cpaArt.getSoleAttributeValue(AtsAttributeTypes.PcrToolId);
            IAtsCpaService cpaService = cpaRegistry.getServiceById(toolId);
            if (cpaService == null) {
               rd.errorf("CPA Tool not configured for Tool Id [%s].  Skipping.", cpaService);
            } else {
               IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(
                  "Duplicate for CPA " + duplicate.getCpaId(), AtsCoreUsers.SYSTEM_USER);
               IAtsTeamWorkflow cpaWf = atsApi.getWorkItemService().getTeamWf(cpaArt);
               duplicatePcrId = cpaArt.getSoleAttributeValue(AtsAttributeTypes.DuplicatedPcrId, null);
               if (Strings.isValid(duplicatePcrId)) {
                  rd.errorf("CPA already has duplicate pcr id set as [%s].  Skipping.", duplicatePcrId);
               } else {
                  String originatingPcrId = cpaArt.getSoleAttributeValue(AtsAttributeTypes.OriginatingPcrId);
                  duplicatePcrId = cpaService.duplicate(cpaWf, duplicate.getProgramId(), duplicate.getVersionId(),
                     originatingPcrId, rd);
                  if (Strings.isValid(duplicatePcrId)) {
                     changes.setSoleAttributeValue(cpaWf, AtsAttributeTypes.DuplicatedPcrId, duplicatePcrId);
                     changes.setSoleAttributeValue(cpaWf, AtsAttributeTypes.ApplicableToProgram, "Yes");
                  }
                  if (duplicate.isCompleteCpa()) {
                     TransitionHelper helper =
                        new TransitionHelper("Complete Applicability Workflow", Arrays.asList(cpaWf), "Completed",
                           new ArrayList<AtsUser>(), "", changes, atsApi, TransitionOption.OverrideAssigneeCheck);
                     AtsUser asUser = atsApi.getUserService().getUserByUserId(duplicate.getUserId());
                     if (asUser == null) {
                        rd.errorf("Invalid userId [%s].  Skipping.", asUser);
                     }
                     helper.setTransitionUser(asUser);
                     TransitionManager mgr = new TransitionManager(helper);
                     mgr.handleAll();
                  }
                  if (!changes.isEmpty()) {
                     changes.execute();
                  }
               }
            }
         } else {
            rd.errorf("Workflow %s is not an applicability workflow.  Skipping.", atsId);
         }
      }
      return rd;
   }
}
