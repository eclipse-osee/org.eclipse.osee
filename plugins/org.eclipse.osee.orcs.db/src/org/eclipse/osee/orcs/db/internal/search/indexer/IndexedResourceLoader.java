/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.indexer;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.orcs.core.ds.IndexedResource;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;

/**
 * @author Roberto E. Escobar
 */
public interface IndexedResourceLoader {

   void loadSource(OrcsDataHandler<IndexedResource> handler, Long tagQueueQueryId, OrcsTokenService tokenService);

}
