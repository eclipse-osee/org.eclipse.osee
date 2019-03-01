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

package org.eclipse.osee.framework.ui.skynet.render;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.api.WordArtifactChange;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.HttpWordUpdateRequest;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public class UpdateArtifactOperation extends AbstractOperation {
   private final File workingFile;
   private final List<Artifact> artifacts;
   private final BranchId branch;
   private final boolean threeWayMerge;

   public UpdateArtifactOperation(File workingFile, List<Artifact> artifacts, BranchId branch, boolean threeWayMerge) {
      super("Update Artifact", Activator.PLUGIN_ID);
      this.workingFile = workingFile;
      this.artifacts = artifacts;
      this.branch = branch;
      this.threeWayMerge = threeWayMerge;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<Long> transferArts = Lists.newLinkedList();

      for (Artifact art : artifacts) {
         transferArts.add(art.getId());
      }

      byte[] data = Lib.fileToBytes(workingFile);
      WordUpdateData wud = new WordUpdateData();
      wud.setWordData(data);
      wud.setArtifacts(transferArts);
      wud.setBranch(branch);
      wud.setThreeWayMerge(threeWayMerge);
      wud.setComment(getComment());
      wud.setMultiEdit(UserManager.getBooleanSetting(MsWordPreferencePage.MUTI_EDIT_SAVE_ALL_CHANGES));
      wud.setUserArtId(UserManager.getUser());

      WordUpdateChange change = HttpWordUpdateRequest.updateWordArtifacts(wud);
      postProcessChange(change);
      WordMlChangesDialog changes = new WordMlChangesDialog(change);
      changes.doWork(null);
   }

   private void postProcessChange(WordUpdateChange change) {
      if (change.getTx() != null && change.getBranch() != null) {
         // Collect attribute events
         ArtifactEvent artifactEvent = new ArtifactEvent(TransactionToken.valueOf(change.getTx(), branch));

         for (Artifact artifact : artifacts) {
            WordArtifactChange artChange = change.getWordArtifactChange(artifact.getArtId());
            if (artChange != null) {
               artifact.reloadAttributesAndRelations();
               Collection<AttributeChange> attrChanges = getAttributeChanges(artifact, artChange);
               if (!attrChanges.isEmpty()) {
                  EventModifiedBasicGuidArtifact guidArt = new EventModifiedBasicGuidArtifact(artifact.getBranch(),
                     artifact.getArtifactType(), artifact.getGuid(), attrChanges);
                  artifactEvent.addArtifact(guidArt);
               }
            }
         }
         if (!artifactEvent.getArtifacts().isEmpty()) {
            OseeEventManager.kickPersistEvent(this, artifactEvent);
         }
      }
   }

   private Collection<AttributeChange> getAttributeChanges(Artifact artifact, WordArtifactChange change) {
      List<AttributeChange> attributeChanges = new LinkedList<>();
      for (AttributeTypeId attributeType : change.getChangedAttrTypes()) {
         Attribute<?> attribute = artifact.getSoleAttribute(attributeType);
         if (attribute != null) {
            AttributeChange attributeChange = attribute.createAttributeChangeFromSelf();
            attributeChanges.add(attributeChange);
         }
      }
      return attributeChanges;
   }

   private String getComment() {
      StringBuilder sb = new StringBuilder(getClass().getSimpleName());
      int numArts = artifacts.size();
      sb.append(" - ");
      if (numArts == 1) {
         sb.append(artifacts.get(0).toStringWithId());
      } else {
         sb.append(Integer.toString(numArts));
         sb.append(" Artifacts");
      }
      return sb.toString();
   }

   private static final class WordMlChangesDialog extends AbstractOperation {

      private final WordUpdateChange change;

      public WordMlChangesDialog(WordUpdateChange change) {
         super("Tracked Changes Dialog", Activator.PLUGIN_ID);
         this.change = change;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {

               if (change != null) {
                  if (!change.getTrackedChangeArts().isEmpty()) {
                     XResultData resultData = new XResultData(false);
                     resultData.setTitle("Artifacts with Tracked Changes");
                     resultData.addRaw("The following artifacts contain tracked changes and could not be saved." //
                        + "\nPlease accept or reject and turn off tracked changes, then save the artifact.\n\n");
                     for (Map.Entry<Long, String> entry : change.getTrackedChangeArts().entrySet()) {
                        resultData.addRaw("Artifact ");
                        resultData.addRaw("id: " + entry.getKey() + ", ");
                        resultData.addRaw("name: " + entry.getValue() + "\n");
                     }
                     XResultDataUI.report(resultData, resultData.getTitle());
                  }

                  if (!change.getInvalidApplicabilityTagArts().isEmpty()) {
                     XResultData resultData = new XResultData(false);
                     resultData.setTitle("Artifacts with Invalid Applicability Tags");
                     resultData.addRaw(
                        "The following artifacts contain invalid feature values and/or inconsistent start and ends tags." //
                           + "\nPlease make sure the feature values used are found in the FeatureDefinition Artifact and the start and end tags match.\n" + "This must be fixed before commit into the parent branch occurs.\n\n");

                     for (Map.Entry<Long, String> entry : change.getInvalidApplicabilityTagArts().entrySet()) {
                        resultData.addRaw("Artifact ");
                        resultData.addRaw("id: " + entry.getKey() + ", ");
                        resultData.addRaw("name: " + entry.getValue() + "\n");
                     }
                     XResultDataUI.report(resultData, resultData.getTitle());
                  }
               }
            }
         });
      }
   }
}
