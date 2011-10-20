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
package org.eclipse.osee.orcs.db.internal.search.util;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;

/**
 * @author Roberto E. Escobar
 */
public class TokenQueryPostProcessor extends AbstractQueryPostProcessor {

   private final TaggingEngine engine;

   public TokenQueryPostProcessor(Log logger, TaggingEngine engine) {
      super(logger);
      this.engine = engine;
   }

   @Override
   protected Tagger getTagger(ReadableAttribute<?> attribute) throws OseeCoreException {
      return engine.getTagger(attribute.getAttributeType());
   }
}
