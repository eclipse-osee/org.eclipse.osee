/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.mocks;

import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

/**
 * @author Roberto E. Escobar
 */
public class MockResourceNameResolver implements ResourceNameResolver {

   private String storageName;
   private String internalFileName;

   public MockResourceNameResolver(String storageName, String internalFileName) {
      super();
      this.storageName = storageName;
      this.internalFileName = internalFileName;
   }

   @Override
   public String getStorageName() {
      return storageName;
   }

   @Override
   public String getInternalFileName() {
      return internalFileName;
   }

   public void setStorageName(String storageName) {
      this.storageName = storageName;
   }

   public void setInternalFileName(String internalFileName) {
      this.internalFileName = internalFileName;
   }

}
