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

package org.eclipse.osee.orcs.db.internal.loader.handlers;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.orcs.core.ds.Criteria;
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

   public static SqlHandlerFactory createHandlerFactory() {
      Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap = new HashMap<>();

      // Query
      handleMap.put(CriteriaArtifact.class, ArtifactSqlHandler.class);
      handleMap.put(CriteriaAttribute.class, AttributeSqlHandler.class);
      handleMap.put(CriteriaRelation.class, RelationSqlHandler.class);

      return new SqlHandlerFactoryImpl(null, handleMap);
   }
}