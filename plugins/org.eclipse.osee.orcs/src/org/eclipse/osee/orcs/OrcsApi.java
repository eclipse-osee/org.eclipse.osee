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
package org.eclipse.osee.orcs;

import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.orcs.search.QueryFacade;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public interface OrcsApi {

   QueryFactory getQueryFactory(ApplicationContext context);

   QueryFacade getQueryFacade(ApplicationContext context);

   Graph getGraph(ApplicationContext context);

   DataStoreTypeCache getDataStoreTypeCache();

   BranchCache getBranchCache();

}
