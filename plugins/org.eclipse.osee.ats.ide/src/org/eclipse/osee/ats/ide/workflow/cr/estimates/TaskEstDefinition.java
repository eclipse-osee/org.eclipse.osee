/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.estimates;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class TaskEstDefinition extends NamedIdBase {

   private List<ArtifactId> assigneeAccountIds = new ArrayList<>();
   private String description;
   private IAtsTask task;
   private boolean checked = false;
   private boolean manual = false;

   public TaskEstDefinition(Long id, String name, String description, List<ArtifactId> assigneeAccountIds) {
      super(id, name);
      this.description = description;
      this.assigneeAccountIds = assigneeAccountIds;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<ArtifactId> getAssigneeAccountIds() {
      return assigneeAccountIds;
   }

   public void setAssigneeAccountIds(List<ArtifactId> assigneeAccountIds) {
      this.assigneeAccountIds = assigneeAccountIds;
   }

   public IAtsTask getTask() {
      return task;
   }

   public void setTask(IAtsTask task) {
      this.task = task;
   }

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   public boolean hasTask() {
      return task != null;
   }

   public boolean isManual() {
      return manual;
   }

   public void setManual(boolean manual) {
      this.manual = manual;
   }

}
