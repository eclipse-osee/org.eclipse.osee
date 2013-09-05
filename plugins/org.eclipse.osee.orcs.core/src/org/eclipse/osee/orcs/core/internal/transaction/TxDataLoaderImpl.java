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
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;
import org.eclipse.osee.orcs.data.ArtifactId;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class TxDataLoaderImpl implements TxDataLoader {

   public TxDataLoaderImpl() {
      super();
   }

   @SuppressWarnings("unused")
   @Override
   public ResultSet<Artifact> loadArtifacts(OrcsSession session, IOseeBranch branch, Collection<ArtifactId> artifactIds) throws OseeCoreException {
      throw new UnsupportedOperationException("Late loading of artifacts is not supported at this time.");
   }

}