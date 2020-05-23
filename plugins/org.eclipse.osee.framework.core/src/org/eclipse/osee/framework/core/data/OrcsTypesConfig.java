/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class OrcsTypesConfig {

   private int currentVersion;
   private List<OrcsTypesVersion> versions = new LinkedList<>();

   public int getCurrentVersion() {
      return currentVersion;
   }

   public void setCurrentVersion(int currentVersion) {
      this.currentVersion = currentVersion;
   }

   public List<OrcsTypesVersion> getVersions() {
      return versions;
   }

   public void setVersions(List<OrcsTypesVersion> versions) {
      this.versions = versions;
   }

   @Override
   public String toString() {
      return "OrcsTypesConfig [currVer=" + currentVersion + ", versions=" + versions + "]";
   }

}
