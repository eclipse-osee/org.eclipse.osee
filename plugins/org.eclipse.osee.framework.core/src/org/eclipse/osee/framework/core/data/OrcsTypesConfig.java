/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
