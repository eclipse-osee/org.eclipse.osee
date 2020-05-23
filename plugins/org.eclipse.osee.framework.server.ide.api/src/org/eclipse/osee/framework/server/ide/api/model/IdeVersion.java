/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.framework.server.ide.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class IdeVersion {

   private List<String> versions;

   public List<String> getVersions() {
      if (versions == null) {
         versions = new ArrayList<>();
      }
      return versions;
   }

   public void addVersion(String version) {
      if (!getVersions().contains(version)) {
         getVersions().add(version);
      }
   }

   public void setVersions(List<String> versions) {
      this.versions = versions;
   }

   @Override
   public String toString() {
      return "IdeVersion [versions=" + versions + "]";
   }
}
