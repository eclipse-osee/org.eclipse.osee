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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.DataPostProcessorFactory;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllArtifacts;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public final class SqlHandlerFactoryUtil {

   private SqlHandlerFactoryUtil() {
      // Static Utility
   }

   public static SqlHandlerFactory createArtifactSqlHandlerFactory(Log logger, IdentityService identityService, TagProcessor tagProcessor, DataPostProcessorFactory<CriteriaAttributeKeywords> postProcessorFactory) {
      Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap =
         new HashMap<Class<? extends Criteria>, Class<? extends SqlHandler<?>>>();

      Map<Class<? extends SqlHandler<?>>, DataPostProcessorFactory<?>> factoryMap =
         new HashMap<Class<? extends SqlHandler<?>>, DataPostProcessorFactory<?>>();

      handleMap.put(CriteriaArtifactGuids.class, ArtifactGuidSqlHandler.class);
      handleMap.put(CriteriaArtifactHrids.class, ArtifactHridsSqlHandler.class);
      handleMap.put(CriteriaArtifactIds.class, ArtifactIdsSqlHandler.class);
      handleMap.put(CriteriaArtifactType.class, ArtifactTypeSqlHandler.class);
      handleMap.put(CriteriaRelatedTo.class, RelatedToSqlHandler.class);
      handleMap.put(CriteriaRelationTypeExists.class, RelationTypeExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeTypeExists.class, AttributeTypeExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeOther.class, AttributeOtherSqlHandler.class);
      handleMap.put(CriteriaAttributeKeywords.class, AttributeTokenSqlHandler.class);
      handleMap.put(CriteriaAllArtifacts.class, AllArtifactsSqlHandler.class);

      factoryMap.put(AttributeTokenSqlHandler.class, postProcessorFactory);

      return new SqlHandlerFactoryImpl(logger, identityService, tagProcessor, handleMap, factoryMap);
   }
}
