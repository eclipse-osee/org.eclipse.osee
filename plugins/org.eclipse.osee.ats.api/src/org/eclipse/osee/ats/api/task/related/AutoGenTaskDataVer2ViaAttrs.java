/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.related;

import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * Version 2 of Auto Task Generation uses task attributes and parent TWF to resolve fields
 *
 * @author Donald G. Dunne
 */
public class AutoGenTaskDataVer2ViaAttrs implements IAutoGenTaskData {

   String addDetails;
   boolean deleted = false;
   protected final IAtsTask task;
   AutoGenVersion autoGenVer;

   public AutoGenTaskDataVer2ViaAttrs(IAtsTask task) {
      this.task = task;
   }

   @Override
   public WorkType getWorkType() {
      return WorkType.valueOfOrNone(
         task.getAtsApi().getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.WorkType, ""));
   }

   @Override
   public String getRelatedArtName() {
      return task.getAtsApi().getAttributeResolver().getSoleAttributeValue(task,
         AtsAttributeTypes.TaskToChangedArtifactName, "");
   }

   @Override
   public ArtifactId getRelatedArtId() {
      return task.getAtsApi().getAttributeResolver().getSoleAttributeValue(task,
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
   }

   @Override
   public String getAddDetails() {
      return "";
   }

   @Override
   public boolean isDeleted() {
      return task.getAtsApi().getAttributeResolver().getSoleAttributeValue(task,
         AtsAttributeTypes.TaskToChangedArtifactDeleted, false);
   }

   @Override
   public IAtsTask getTask() {
      return task;
   }

   @Override
   public AutoGenVersion getAutoGenVer() {
      return AutoGenVersion.Ver2;
   }

}
