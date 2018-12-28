/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.handlers;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaArtifact;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaAttribute;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaRelation;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public final class LoaderSqlHandlerFactoryUtil {

   private LoaderSqlHandlerFactoryUtil() {
      // Static Utility
   }

   public static SqlHandlerFactory createHandlerFactory(Log logger, IdentityLocator identityService) {
      Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap =
         new HashMap<>();

      // Query
      handleMap.put(CriteriaArtifact.class, ArtifactSqlHandler.class);
      handleMap.put(CriteriaAttribute.class, AttributeSqlHandler.class);
      handleMap.put(CriteriaRelation.class, RelationSqlHandler.class);

      return new SqlHandlerFactoryImpl(logger, identityService, null, handleMap);
   }

}
