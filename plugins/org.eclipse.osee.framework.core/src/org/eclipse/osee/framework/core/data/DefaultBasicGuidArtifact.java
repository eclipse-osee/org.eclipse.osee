/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * @author Donald G. Dunne
 */
public class DefaultBasicGuidArtifact implements Identity, IBasicGuidArtifact {

   private static final long serialVersionUID = -4997763989583925345L;
   private final String branchGuid;
   private final String artTypeGuid;
   private final String guid;

   public DefaultBasicGuidArtifact(String branchGuid, String artTypeGuid, String guid) {
      this.branchGuid = branchGuid;
      this.artTypeGuid = artTypeGuid;
      this.guid = guid;
   }

   public String getBranchGuid() {
      return branchGuid;
   }

   public String getArtTypeGuid() {
      return artTypeGuid;
   }

   public String toString() {
      return String.format("[%s]", guid);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = prime * ((artTypeGuid == null) ? 0 : artTypeGuid.hashCode());
      result = prime * result + ((branchGuid == null) ? 0 : branchGuid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      DefaultBasicGuidArtifact other = (DefaultBasicGuidArtifact) obj;
      if (artTypeGuid == null) {
         if (other.artTypeGuid != null) return false;
      } else if (!artTypeGuid.equals(other.artTypeGuid)) return false;
      if (branchGuid == null) {
         if (other.branchGuid != null) return false;
      } else if (!branchGuid.equals(other.branchGuid)) return false;
      if (guid == null) {
         if (other.guid != null) return false;
      } else if (!guid.equals(other.guid)) return false;
      return true;
   }

   public String getGuid() {
      return guid;
   }

}