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

package org.eclipse.osee.orcs.db.internal.search.indexer;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public interface IndexerCallableFactory {

   Callable<?> createIndexerTaskCallable(OrcsSession session, OrcsTokenService tokenService, IndexerCollector collector, Long queryId);

}
