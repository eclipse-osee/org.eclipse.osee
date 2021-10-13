/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class MimUserPreference extends PLGenericDBObject {
   private boolean isInEditMode = false;
   private boolean hasBranchPref = false;
   private List<MimUserColumnPreference> columnPreferences = new LinkedList<MimUserColumnPreference>();

   public MimUserPreference(ArtifactReadable artifact, BranchId branch, boolean hasWriteAccess) {
      super(artifact);
      this.setHasBranchPref(
         this.hasBranchAttribute(artifact.getAttributeValues(CoreAttributeTypes.MimBranchPreferences), branch));
      this.setInEditMode(this.filterBranches(artifact.getAttributeValues(CoreAttributeTypes.MimBranchPreferences),
         branch) && hasWriteAccess);
      this.setColumnPreferences(artifact.getAttributeValues(CoreAttributeTypes.MimColumnPreferences));
   }

   public MimUserPreference(ArtifactReadable artifact, BranchId branch) {
      this(artifact, branch, false);
   }

   public MimUserPreference() {
      super();
   }

   /**
    * @return the isInEditMode
    */
   public boolean isInEditMode() {
      return isInEditMode;
   }

   /**
    * @param isInEditMode the isInEditMode to set
    */
   public void setInEditMode(boolean isInEditMode) {
      this.isInEditMode = isInEditMode;
   }

   /**
    * @return the columnPreferences
    */
   public List<MimUserColumnPreference> getColumnPreferences() {
      return columnPreferences;
   }

   /**
    * @param columnPreferences the columnPreferences to set
    */
   public void setColumnPreferences(List<String> columnPreferences) {
      this.columnPreferences = new LinkedList<MimUserColumnPreference>();
      for (String preference : columnPreferences) {
         if (preference.contains(":")) {
            String[] preferences = preference.split(":");
            this.columnPreferences.add(new MimUserColumnPreference(preferences[0].split("\"")[1],
               Boolean.parseBoolean(preferences[1].split("\"")[0])));
         }
      }
   }

   /**
    * @return the hasBranchPref
    */
   public boolean isHasBranchPref() {
      return hasBranchPref;
   }

   /**
    * @param hasBranchPref the hasBranchPref to set
    */
   public void setHasBranchPref(boolean hasBranchPref) {
      this.hasBranchPref = hasBranchPref;
   }

   @JsonIgnore
   private boolean filterBranches(List<String> branches, BranchId branch) {
      for (String editValues : branches) {
         if (editValues.contains(branch.getId().toString())) {
            return Boolean.parseBoolean(editValues.split(branch.getId().toString() + ":")[1].split("\"")[0]);
         }
      }
      return false;
   }

   @JsonIgnore
   private boolean hasBranchAttribute(List<String> branches, BranchId branch) {
      for (String editValues : branches) {
         if (editValues.contains(branch.getId().toString())) {
            return true;
         }
      }
      return false;
   }

}
