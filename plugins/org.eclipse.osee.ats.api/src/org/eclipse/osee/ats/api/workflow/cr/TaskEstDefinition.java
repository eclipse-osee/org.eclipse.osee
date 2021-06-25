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
package org.eclipse.osee.ats.api.workflow.cr;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class TaskEstDefinition extends NamedIdBase {

   private List<ArtifactId> assigneeAccountIds = new ArrayList<>();
   private String description;
   private boolean checked = false;
   private boolean manual = false;
   private ArtifactToken actionableItem = ArtifactToken.SENTINEL;

   public TaskEstDefinition(Long id, String name, String description, List<ArtifactId> assigneeAccountIds, ArtifactToken actionableItem) {
      super(id, name);
      this.description = description;
      if (assigneeAccountIds != null && !assigneeAccountIds.isEmpty()) {
         this.assigneeAccountIds = assigneeAccountIds;
      }
      this.actionableItem = actionableItem;
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

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   public boolean isManual() {
      return manual;
   }

   public void setManual(boolean manual) {
      this.manual = manual;
   }

   public ArtifactToken getActionableItem() {
      return actionableItem;
   }

   public void setActionableItem(ArtifactToken actionableItem) {
      this.actionableItem = actionableItem;
   }

}
