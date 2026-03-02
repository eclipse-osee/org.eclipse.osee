/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Class that, given a beginning artifact, can provide all the data for attrs, rels, relatedArts and etc. This can
 * eventually be sent from server rather than client loading it. It does provide for a single loading of data so each
 * widget in a WorkflowEditor doesn't need to make it's own laoding and conversions.
 *
 * @author Donald G. Dunne
 */
public class XXWorkItemData {

   private static Map<Long, XXWorkItemData> idToXXWid = new HashMap<>(500);
   public static XXWorkItemData SENTINEL = new XXWorkItemData(null);

   private final Artifact artifact;
   private boolean teamWfLoaded = false;
   private IAtsTeamWorkflow teamWf;
   private boolean versionLoaded = false;
   private IAtsVersion version;
   private IAtsWorkItem workItem;
   private Collection<IAtsVersion> selectableVersions;
   private TransactionId txId = TransactionId.SENTINEL;

   public static XXWorkItemData get(Artifact art) {
      Conditions.assertNotNull(art, "Artifact can not be null");
      XXWorkItemData xxWid = idToXXWid.get(art.getId());
      if (xxWid == null) {
         System.err.println("Create new xxWid: " + art.toStringWithId());
         xxWid = new XXWorkItemData(art);
         idToXXWid.put(art.getId(), xxWid);
      } else {
         xxWid.set(art);
         System.err.println("Cached xxWid: " + art.toStringWithId());
      }
      return xxWid;
   }

   private void set(Artifact art) {
      if (txId.isInvalid()) {
         txId = art.getTransaction();
      } else if (!art.getTransaction().equals(txId)) {
         System.err.println("Cleared Wid cause new txid");
         teamWfLoaded = false;
         teamWf = null;
         versionLoaded = false;
         workItem = null;
         txId = art.getTransaction();
      }
   }

   private XXWorkItemData(Artifact artifact) {
      this.artifact = artifact;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public boolean isTeamWf() {
      return artifact != null && artifact.isOfType(AtsArtifactTypes.TeamWorkflow);
   }

   public boolean isWorkItem() {
      return artifact != null && artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact);
   }

   public IAtsTeamWorkflow getTeamWf() {
      if (teamWf == null && !teamWfLoaded) {
         IAtsWorkItem workItem = getWorkItem();
         if (workItem.isTeamWorkflow()) {
            teamWf = (IAtsTeamWorkflow) workItem;
         }
         teamWfLoaded = true; // So don't attempt to reload if not teamWf
      }
      return teamWf;
   }

   public IAtsWorkItem getWorkItem() {
      if (workItem == null && artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         workItem = AtsApiService.get().getWorkItemService().getWorkItem(artifact);
      }
      return workItem;
   }

   public IAtsVersion getVersion() {
      if (version == null && !versionLoaded) {
         IAtsTeamWorkflow teamWf = getTeamWf();
         if (teamWf != null) {
            version = AtsApiService.get().getVersionService().getTargetedVersion(teamWf);
         }
         versionLoaded = true; // So don't attempt to reload if no version or not teamwf
      }
      return null;
   }

   public Collection<IAtsVersion> getSelectableVersions() {
      if (selectableVersions == null) {
         if (isTeamWf()) {
            IAtsTeamWorkflow teamWf = getTeamWf();
            selectableVersions = AtsApiService.get().getVersionService().getVersionsFromTeamDefHoldingVersions(
               teamWf.getTeamDef(), VersionReleaseType.UnReleased, VersionLockedType.UnLocked);
         } else {
            selectableVersions = Collections.emptyList();
         }
      }
      return selectableVersions;
   }

}
