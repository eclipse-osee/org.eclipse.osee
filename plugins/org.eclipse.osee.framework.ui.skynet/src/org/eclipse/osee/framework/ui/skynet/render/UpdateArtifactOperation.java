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

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.report.api.WordArtifactChange;
import org.eclipse.osee.define.report.api.WordUpdateChange;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.HttpWordUpdateRequest;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import com.google.common.collect.Lists;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public class UpdateArtifactOperation extends AbstractOperation {
   private final File workingFile;
   private final List<Artifact> artifacts;
   private final IOseeBranch branch;
   private final boolean threeWayMerge;

   public UpdateArtifactOperation(File workingFile, List<Artifact> artifacts, IOseeBranch branch, boolean threeWayMerge) {
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
         transferArts.add((long) art.getArtId());
      }

      byte[] data = Lib.fileToBytes(workingFile);
      WordUpdateData wud = new WordUpdateData();
      wud.setWordData(data);
      wud.setArtifacts(transferArts);
      wud.setBranch(branch.getUuid());
      wud.setThreeWayMerge(threeWayMerge);
      wud.setComment(getComment());
      wud.setMultiEdit(UserManager.getBooleanSetting(MsWordPreferencePage.MUTI_EDIT_SAVE_ALL_CHANGES));
      wud.setUserArtId((long) UserManager.getUser().getArtId());

      WordUpdateChange change = HttpWordUpdateRequest.updateWordArtifacts(wud);
      postProcessChange(change);
   }

   private void postProcessChange(WordUpdateChange change) {
      // Collect attribute events
      Integer tx = change.getTx();
      ArtifactEvent artifactEvent = new ArtifactEvent(change.getBranchUuid());
      artifactEvent.setTransactionId(tx);

      for (Artifact artifact : artifacts) {
         WordArtifactChange artChange = change.getWordArtifactChange(artifact.getArtId());
         if (artChange != null) {
            Collection<AttributeChange> attrChanges = getAttributeChanges(artifact, artChange);
            if (!attrChanges.isEmpty()) {
               EventModifiedBasicGuidArtifact guidArt =
                  new EventModifiedBasicGuidArtifact(artifact.getBranch().getUuid(),
                     artifact.getArtifactType().getGuid(), artifact.getGuid(), attrChanges);
               artifactEvent.getArtifacts().add(guidArt);

               artifact.reloadAttributesAndRelations();
            }
         }
      }
      if (!artifactEvent.getArtifacts().isEmpty()) {
         OseeEventManager.kickPersistEvent(this, artifactEvent);
      }
   }

   private Collection<AttributeChange> getAttributeChanges(Artifact artifact, WordArtifactChange change) {
      List<AttributeChange> attributeChanges = new LinkedList<AttributeChange>();
      for (long attrTypeId : change.getChangedAttrTypes()) {
         AttributeType type = AttributeTypeManager.getTypeByGuid(attrTypeId);
         Attribute<?> attribute = artifact.getSoleAttribute(type);
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
}
