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
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetImpl implements ResultSet<ReadableArtifact> {

   private final OrcsObjectLoader objectLoader;
   private final SessionContext sessionContext;
   private final QueryContext queryContext;
   private final LoadOptions loadOptions;

   public ResultSetImpl(OrcsObjectLoader objectLoader, SessionContext sessionContext, QueryContext queryContext, LoadOptions loadOptions) {
      super();
      this.objectLoader = objectLoader;
      this.sessionContext = sessionContext;
      this.queryContext = queryContext;
      this.loadOptions = loadOptions;
   }

   @Override
   public ReadableArtifact getOneOrNull() throws OseeCoreException {
      List<ReadableArtifact> result = getList();
      return result.isEmpty() ? null : result.iterator().next();
   }

   @Override
   public ReadableArtifact getExactlyOne() throws OseeCoreException {
      List<ReadableArtifact> result = getList();
      if (result.isEmpty()) {
         throw new ArtifactDoesNotExist("No artifacts found");
      } else if (result.size() > 1) {
         throw new MultipleArtifactsExist("Multiple artifact found - total [%s]", result.size());
      }
      return result.iterator().next();
   }

   @Override
   public List<ReadableArtifact> getList() throws OseeCoreException {
      return objectLoader.load(queryContext, loadOptions, sessionContext);
   }

   @Override
   public Iterable<ReadableArtifact> getIterable(int fetchSize) throws OseeCoreException {
      return getList();
   }
}
