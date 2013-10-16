/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.task.createtasks;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Shawn F. Cook
 */
public class TaskOpModify extends AbstractTaskOp {
   public static final String NO_MATCHING_CHANGE_REPORT_ARTIFACT = "No Match to Change Report Artifact; ";

   @Override
   public IStatus execute(TaskMetadata metadata, SkynetTransaction transaction) throws OseeCoreException {
      TaskArtifact taskArt = metadata.getTaskArtifact();
      String currentNoteValue = taskArt.getSoleAttributeValueAsString(AtsAttributeTypes.SmaNote, "");
      if (!currentNoteValue.contains(NO_MATCHING_CHANGE_REPORT_ARTIFACT)) {
         // append the flag to the Notes Field
         taskArt.setSoleAttributeFromString(AtsAttributeTypes.SmaNote,
            NO_MATCHING_CHANGE_REPORT_ARTIFACT + currentNoteValue);
         // need to remove the static Id and allow user to delete these
         if (taskArt.getAttributesToStringList(CoreAttributeTypes.StaticId).contains(AUTO_GENERATED_STATIC_ID)) {
            taskArt.deleteSingletonAttributeValue(CoreAttributeTypes.StaticId, AUTO_GENERATED_STATIC_ID);
         }
      }

      taskArt.persist(transaction);

      return generateGenericOkStatus(metadata.getTaskEnum(), taskArt.toStringWithId(),
         metadata.getParentTeamWf().toStringWithId(), "[no changed artifact]");
   }
}
