/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
