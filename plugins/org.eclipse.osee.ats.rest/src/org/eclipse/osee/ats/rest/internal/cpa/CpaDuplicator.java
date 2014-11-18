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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
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

   public Response duplicate() {
      XResultData rd = new XResultData(false);
      ArtifactReadable cpaArt = atsServer.getArtifactById(duplicate.getCpaUuid());
      String atsId = cpaArt.getSoleAttributeValue(AtsAttributeTypes.AtsId, null);
      String duplicatePcrId = "";
      if (!Strings.isValid(atsId)) {
         rd.logErrorWithFormat("AtsId %s is not valid.  Skipping.", atsId);
      } else {
         if (cpaArt.getSoleAttributeValue(AtsAttributeTypes.ApplicabilityWorkflow, false)) {
            String toolId = cpaArt.getSoleAttributeValue(AtsAttributeTypes.PcrToolId);
            IAtsCpaService cpaService = cpaRegistry.getServiceById(toolId);
            if (cpaService == null) {
               rd.logErrorWithFormat("CPA Tool not configured for Tool Id [%s].  Skipping.", cpaService);
            } else {
               IAtsChangeSet changes =
                  atsServer.getStoreFactory().createAtsChangeSet("Duplicate for CPA " + duplicate.getCpaUuid(),
                     AtsCoreUsers.SYSTEM_USER);
               IAtsTeamWorkflow cpaWf = atsServer.getWorkItemFactory().getTeamWf(cpaArt);
               duplicatePcrId = cpaArt.getSoleAttributeValue(AtsAttributeTypes.DuplicatedPcrId, null);
               if (Strings.isValid(duplicatePcrId)) {
                  rd.logErrorWithFormat("CPA already has duplicate pcr id set as [%s].  Skipping.", duplicatePcrId);
               } else {
                  String originatingPcrId = cpaArt.getSoleAttributeValue(AtsAttributeTypes.OriginatingPcrId);
                  duplicatePcrId = cpaService.duplicate(cpaWf, duplicate.getProgramUuid(), originatingPcrId, rd);
                  if (Strings.isValid(duplicatePcrId)) {
                     changes.setSoleAttributeValue(cpaWf, AtsAttributeTypes.DuplicatedPcrId, duplicatePcrId);
                  }
                  if (!changes.isEmpty()) {
                     changes.execute();
                  }
               }
            }
         } else {
            rd.logErrorWithFormat("Workflow %s is not an applicability workflow.  Skipping.", atsId);
         }
      }
      if (rd.isErrors()) {
         return Response.status(Status.NOT_ACCEPTABLE).entity(AHTML.simplePage(rd.toString())).build();
      }
      return Response.ok().entity(duplicatePcrId).build();
   }
}
