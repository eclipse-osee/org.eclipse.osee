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
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.internal.MasterLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("unused")
public class ResultSetImpl implements ResultSet<ReadableArtifact> {

   // TODO implements ResultSetFactory tie in with ArtifactLoader
   private final QueryEngine queryEngine;
   private final CriteriaSet criteriaSet;
   private final QueryOptions options;
   private final MasterLoader loader;
   private final SessionContext sessionContext;
   private final LoadOptions loadOptions;

   public ResultSetImpl(MasterLoader loader, QueryEngine queryEngine, CriteriaSet criteriaSet, QueryOptions options, SessionContext sessionContext, LoadOptions loadOptions) {
      super();
      this.queryEngine = queryEngine;
      this.criteriaSet = criteriaSet;
      this.options = options;
      this.loader = loader;
      this.sessionContext = sessionContext;
      this.loadOptions = loadOptions;
   }

   @Override
   public ReadableArtifact getOneOrNull() throws OseeCoreException {
      // SearchCallable call = queryEngine.search(criteriaSet, options);
      // LoaderCallable call = loader.load(search);
      return null;
   }

   @Override
   public ReadableArtifact getExactlyOne() throws OseeCoreException {
      return null;
   }

   @Override
   public List<ReadableArtifact> getList() throws OseeCoreException {
      Object query = queryEngine.create(sessionContext.getSessionId(), criteriaSet, options);//TODO
      List<ReadableArtifact> arts = loader.load(query, loadOptions, sessionContext);
      return arts;
   }

   @Override
   public Iterable<ReadableArtifact> getIterable(int fetchSize) throws OseeCoreException {
      return getList();
   }

   @Override
   public int getCount() throws OseeCoreException {
      return getList().size();//TODO run queryCount instead
   }

}
