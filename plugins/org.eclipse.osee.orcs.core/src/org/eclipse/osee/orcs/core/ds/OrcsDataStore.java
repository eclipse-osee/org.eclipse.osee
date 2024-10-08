/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsDataStore {

   OrcsTypesDataStore getTypesDataStore();

   DataModule createDataModule(OrcsTokenService tokenService);

   QueryEngineIndexer getQueryEngineIndexer();

   JdbcService getJdbcService();

}
