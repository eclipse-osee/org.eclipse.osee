/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.util;

import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataPostProcessor;
import org.eclipse.osee.orcs.core.ds.DataPostProcessorFactory;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;

/**
 * @author Roberto E. Escobar
 */
public class DataPostProcessorFactoryImpl implements DataPostProcessorFactory<CriteriaAttributeKeywords> {

   private final Log logger;
   private final TaggingEngine taggingEngine;
   private final ExecutorAdmin executorAdmin;

   public DataPostProcessorFactoryImpl(Log logger, TaggingEngine taggingEngine, ExecutorAdmin executorAdmin) {
      super();
      this.logger = logger;
      this.taggingEngine = taggingEngine;
      this.executorAdmin = executorAdmin;
   }

   @Override
   public DataPostProcessor<?> createPostProcessor(CriteriaAttributeKeywords criteria, Options options) {
      DataPostProcessor<?> processor;
      //      if (criteria.getStringOp().isTokenized()) {
      if (criteria.getValues().size() > 1) {
         processor = new SimpleOrPostProcessor(logger, criteria, OptionsUtil.getIncludeDeleted(options));
      } else {
         processor = new TokenQueryPostProcessor(logger, executorAdmin, taggingEngine, criteria, options);
      }
      //      } else {
      //         processor = new AttributeQueryPostProcessor(logger, executorAdmin, taggingEngine, criteria, options);
      //      }
      return processor;
   }
}
