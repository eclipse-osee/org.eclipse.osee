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
package org.eclipse.osee.orcs.core.internal.search;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetImpl implements ResultSet<ReadableArtifact> {

   private final QueryEngine queryEngine;
   private final CriteriaSet criteriaSet;
   private final QueryOptions options;

   public ResultSetImpl(QueryEngine queryEngine, CriteriaSet criteriaSet, QueryOptions options) {
      super();
      this.queryEngine = queryEngine;
      this.criteriaSet = criteriaSet;
      this.options = options;
   }

   @Override
   public ReadableArtifact getOneOrNull() throws OseeCoreException {
      // SearchCallable callable = queryEngine.search(criteriaSet, options);
      // LoaderCallable callable = loader.load(search);
      // ArtifactFactory
      return null;
   }

   @Override
   public ReadableArtifact getExactlyOne() throws OseeCoreException {
      return null;
   }

   @Override
   public List<ReadableArtifact> getList() throws OseeCoreException {
      return null;
   }

   @Override
   public Iterable<ReadableArtifact> getIterable(int fetchSize) throws OseeCoreException {
      return null;
   }

}
