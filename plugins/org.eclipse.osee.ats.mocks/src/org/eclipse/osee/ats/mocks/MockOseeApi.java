/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.mocks;

import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OseeApi;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author John Misinco
 */
public class MockOseeApi implements OseeApi {

   @Override
   public QueryFactory getQueryFactory(ApplicationContext context) {
      return null;
   }

   @Override
   public BranchCache getBranchCache() {
      return null;
   }

}
