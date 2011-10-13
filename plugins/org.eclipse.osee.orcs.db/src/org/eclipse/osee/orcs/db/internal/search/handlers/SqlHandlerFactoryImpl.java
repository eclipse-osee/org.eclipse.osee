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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeyword;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.internal.search.SqlHandler;
import org.eclipse.osee.orcs.db.internal.search.SqlHandlerFactory;

/**
 * @author Roberto E. Escobar
 */
public class SqlHandlerFactoryImpl implements SqlHandlerFactory {

   private static final SqlHandlerComparator comparator = new SqlHandlerComparator();
   private final Map<Class<? extends Criteria>, Class<? extends SqlHandler>> handleMap =
      new HashMap<Class<? extends Criteria>, Class<? extends SqlHandler>>();

   private final IdentityService idService;

   public SqlHandlerFactoryImpl(IdentityService idService) {
      this.idService = idService;

      handleMap.put(CriteriaArtifactGuids.class, ArtifactGuidSqlHandler.class);
      handleMap.put(CriteriaArtifactHrids.class, ArtifactHridsSqlHandler.class);
      handleMap.put(CriteriaArtifactIds.class, ArtifactIdsSqlHandler.class);
      handleMap.put(CriteriaArtifactType.class, ArtifactTypeSqlHandler.class);
      handleMap.put(CriteriaRelationTypeExists.class, RelationTypeExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeTypeExists.class, AttributeTypeExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeOther.class, AttributeOtherSqlHandler.class);
      handleMap.put(CriteriaAttributeKeyword.class, AttributeTokenSqlHandler.class);
   }

   @Override
   public List<SqlHandler> createHandlers(CriteriaSet criteriaSet) throws OseeCoreException {
      List<SqlHandler> handlers = new ArrayList<SqlHandler>();
      for (Criteria criteria : criteriaSet) {
         Class<? extends Criteria> key = criteria.getClass();
         Class<? extends SqlHandler> item = handleMap.get(key);
         try {
            SqlHandler handler = item.newInstance();
            handler.setData(criteria);
            handler.setIdentityService(idService);
            handlers.add(handler);
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      Collections.sort(handlers, comparator);
      return handlers;
   }
}
