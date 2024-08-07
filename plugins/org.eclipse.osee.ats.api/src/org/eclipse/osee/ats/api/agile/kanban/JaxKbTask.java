/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.agile.kanban;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public class JaxKbTask {

   private String name;
   // ngDraggable and Agile web requires guid for tasks, don't change this id
   private String guid;
   private Map<String, String> attributeMap = new HashMap<>();
   private String branchGuid;
   private boolean canEdit;
   private String artifactType;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public Map<String, String> getAttributeMap() {
      return attributeMap;
   }

   public void setAttributeMap(Map<String, String> attributeMap) {
      this.attributeMap = attributeMap;
   }

   public String getBranchGuid() {
      return branchGuid;
   }

   public void setBranchGuid(String branchGuid) {
      this.branchGuid = branchGuid;
   }

   public boolean isCanEdit() {
      return canEdit;
   }

   public void setCanEdit(boolean canEdit) {
      this.canEdit = canEdit;
   }

   public String getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(String artifactType) {
      this.artifactType = artifactType;
   }

}
