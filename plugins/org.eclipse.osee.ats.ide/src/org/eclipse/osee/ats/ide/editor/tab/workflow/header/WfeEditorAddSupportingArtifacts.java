/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * Relates supporting artifacts to work item
 *
 * @author Donald G. Dunne
 */
public class WfeEditorAddSupportingArtifacts extends Job {

   private final Collection<Artifact> supportingArtifacts;
   private final IAtsWorkItem workItem;

   public WfeEditorAddSupportingArtifacts(IAtsWorkItem workItem, Collection<Artifact> supportingArtifacts) {
      super("Add Supporting Artifacts");
      this.workItem = workItem;
      this.supportingArtifacts = supportingArtifacts;
   }

   public XResultData validate() {
      XResultData results = new XResultData();
      if (supportingArtifacts.isEmpty()) {
         results.error("Must pass in supporting artifacts");
      }
      for (Artifact art : supportingArtifacts) {
         if (!art.isOnBranch(AtsApiService.get().getAtsBranch())) {
            results.error("Can not relate artifacts that are not on the ATS Branch");
         }
      }
      return results;
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      XResultData results = validate();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Related supporting artifacts");
      for (Artifact art : supportingArtifacts) {
         if (!AtsApiService.get().getRelationResolver().areRelated(workItem.getStoreObject(),
            CoreRelationTypes.SupportingInfo_SupportingInfo, art)) {
            changes.relate(workItem, CoreRelationTypes.SupportingInfo_SupportingInfo, art);
         }
      }
      if (changes.isEmpty()) {
         AWorkbench.popup("Nothing to relate");
      } else {
         changes.execute();
      }
      return Status.OK_STATUS;
   }

}
