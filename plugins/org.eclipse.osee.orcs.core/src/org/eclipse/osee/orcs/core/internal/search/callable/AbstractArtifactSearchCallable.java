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
package org.eclipse.osee.orcs.core.internal.search.callable;

import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;
import org.eclipse.osee.orcs.core.internal.search.QueryCollector;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractArtifactSearchCallable<T> extends AbstractSearchCallable<T> {

   protected final ArtifactLoaderFactory objectLoader;

   public AbstractArtifactSearchCallable(Log logger, QueryEngine queryEngine, QueryCollector collector, ArtifactLoaderFactory objectLoader, OrcsSession session, LoadLevel loadLevel, QueryData queryData, AttributeTypes types) {
      super(logger, queryEngine, collector, session, loadLevel, queryData, types);
      this.objectLoader = objectLoader;
   }

}
