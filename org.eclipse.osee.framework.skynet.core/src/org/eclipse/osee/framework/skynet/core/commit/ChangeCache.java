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
package org.eclipse.osee.framework.skynet.core.commit;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChangeCache {
   private final IChangeDataAccessor changeDataAccessor;
   private final IChangeFactory factory;

   public ChangeCache(IChangeDataAccessor changeDataAccessor, IChangeFactory factory) {
      this.changeDataAccessor = changeDataAccessor;
      this.factory = factory;
   }

   public Collection<OseeChange> getRawChangeData(IProgressMonitor monitor, IChangeLocator locator) throws Exception {
      Collection<OseeChange> changeData = new ArrayList<OseeChange>();
      changeDataAccessor.loadChangeData(monitor, factory, locator, changeData);
      return changeData;
   }
}
