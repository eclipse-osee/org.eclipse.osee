/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.api.task.track;

/**
 * @author Donald G. Dunne
 */
public class TaskTrackItem {

   String title;
   String assigneesArtIds;
   String description;
   String supportingAtsId;
   String defaultAssigneesArtIds;

   public TaskTrackItem() {
      // for jax-rs
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getAssigneesArtIds() {
      return assigneesArtIds;
   }

   public void setAssigneesArtIds(String assigneesArtIds) {
      this.assigneesArtIds = assigneesArtIds;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getSupportingAtsId() {
      return supportingAtsId;
   }

   public void setSupportingAtsId(String supportingAtsId) {
      this.supportingAtsId = supportingAtsId;
   }

   public String getDefaultAssigneesArtIds() {
      return defaultAssigneesArtIds;
   }

   public void setDefaultAssigneesArtIds(String defaultAssigneesArtIds) {
      this.defaultAssigneesArtIds = defaultAssigneesArtIds;
   }

}
