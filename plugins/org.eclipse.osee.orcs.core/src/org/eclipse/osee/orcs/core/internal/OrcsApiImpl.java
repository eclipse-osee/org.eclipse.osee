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
package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OseeApi;
import org.eclipse.osee.orcs.core.internal.search.QueryFactoryImpl;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsApiImpl implements OseeApi {

   private final QueryFactoryImpl queryFactory = new QueryFactoryImpl();

   @Override
   public QueryFactory getQueryFactory(ApplicationContext context) {
      return queryFactory;
   }

}
