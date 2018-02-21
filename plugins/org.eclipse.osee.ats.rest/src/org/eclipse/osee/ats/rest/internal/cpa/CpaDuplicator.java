/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.cpa;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.osee.ats.api.cpa.DuplicateCpa;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class CpaDuplicator {

   private final IAtsServer atsServer;
   private final DuplicateCpa duplicate;
   private final CpaServiceRegistry cpaRegistry;

   public CpaDuplicator(final DuplicateCpa duplicate, IAtsServer atsServer, CpaServiceRegistry cpaRegistry) {
      this.duplicate = duplicate;
      this.atsServer = atsServer;
      this.cpaRegistry = cpaRegistry;
   }

   public XResultData duplicate() {
      XResultData rd = new XResultData(false);
      ArtifactReadable cpaArt = (ArtifactReadable) atsServer.getQueryService().getArtifactById(duplicate.getCpaId());
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
               IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet(
                  "Duplicate for CPA " + duplicate.getCpaId(), AtsCoreUsers.SYSTEM_USER);
               IAtsTeamWorkflow cpaWf = atsServer.getWorkItemFactory().getTeamWf(cpaArt);
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
                     TransitionHelper helper = new TransitionHelper("Complete Applicability Workflow",
                        Arrays.asList(cpaWf), "Completed", new ArrayList<IAtsUser>(), "", changes,
                        atsServer, TransitionOption.OverrideAssigneeCheck);
                     IAtsUser asUser = atsServer.getUserService().getUserById(duplicate.getUserId());
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
