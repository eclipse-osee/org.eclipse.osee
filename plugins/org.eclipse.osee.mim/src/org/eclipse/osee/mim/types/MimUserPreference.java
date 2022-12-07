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
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Luciano T. Vaglienti
 */
public class MimUserPreference extends PLGenericDBObject {
   public static final MimUserPreference SENTINEL = new MimUserPreference();
   private boolean isInEditMode = false;
   private boolean hasBranchPref = false;
   private MimUserGlobalPreferences globalPrefs;
   private List<MimUserColumnPreference> columnPreferences = new LinkedList<MimUserColumnPreference>();

   public MimUserPreference(ArtifactReadable artifact, MimUserGlobalPreferences globalPrefs, BranchId branch, boolean hasWriteAccess) {
      super(artifact);
      this.setHasBranchPref(
         this.hasBranchAttribute(artifact.getAttributeValues(CoreAttributeTypes.MimBranchPreferences), branch));
      this.setInEditMode(this.filterBranches(artifact.getAttributeValues(CoreAttributeTypes.MimBranchPreferences),
         branch) && hasWriteAccess);
      this.setColumnPreferences(artifact.getAttributeValues(CoreAttributeTypes.MimColumnPreferences));
      this.setGlobalPrefs(globalPrefs);
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

   public MimUserGlobalPreferences getGlobalPrefs() {
      return globalPrefs;
   }

   public void setGlobalPrefs(MimUserGlobalPreferences globalPrefs) {
      this.globalPrefs = globalPrefs;
   }

   public boolean isHasGlobalPrefs() {
      return false;
   }

   @JsonIgnore
   private boolean filterBranches(List<String> branches, BranchId branch) {
      for (String editValues : branches) {
         if ((editValues.contains(branch.getIdString() + ":true") || (editValues.contains(
            branch.getIdString() + ":false"))) && editValues.contains(":")) {
            return Boolean.parseBoolean(editValues.split(branch.getIdString() + ":")[1].split("\"")[0]);
         }
      }
      return false;
   }

   @JsonIgnore
   private boolean hasBranchAttribute(List<String> branches, BranchId branch) {
      for (String editValues : branches) {
         if (editValues.contains(branch.getIdString())) {
            return true;
         }
      }
      return false;
   }

}
