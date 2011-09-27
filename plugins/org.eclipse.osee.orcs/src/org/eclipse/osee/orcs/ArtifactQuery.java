/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.orcs;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public abstract class ArtifactQuery {

   /**
    * Return single artifact or throw exception
    * 
    * @throws ArtifactDoesNotExist if single artifact does not exist
    * @throws MultipleArtifactsExist if multiple found
    */
   public abstract Artifact getArtifactExactlyOne(LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException;

   /**
    * Return single artifact or throw exception
    * 
    * @throws ArtifactDoesNotExist if single artifact does not exist
    * @throws MultipleArtifactsExist if multiple found
    */
   public abstract Artifact getArtifactExactlyOneHistorical(TransactionRecord transactionId, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException;

   /**
    * Return single artifact or null if none found
    * 
    * @throws MultipleArtifactsExist if multiple found <br>
    * <br>
    * replaces: ArtifactQuery...
    */
   public abstract Artifact getArtifactOrNull(LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException;

   /**
    * Return single artifact or null if none found
    * 
    * @throws MultipleArtifactsExist if multiple found <br>
    * <br>
    * replaces: ArtifactQuery...
    */
   public abstract Artifact getArtifactOrNullHistorical(TransactionRecord transactionId, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException;

   /**
    * @param countEstimate estimate of items to be returned; affects query performance
    * @return List of artifact UUIDs or empty list if none found
    */
   public abstract List<Integer> getArtifactUuids(int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException;

   /**
    * @param countEstimate estimate of items to be returned; affects query performance
    * @return List of artifact UUIDs or empty list if none found
    */
   public abstract List<Integer> getArtifactUuidsHistorical(TransactionRecord transactionId, int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException;

   /**
    * @param countEstimate estimate of items to be returned; affects query performance
    * @return List of artifacts or empty list if none found
    */
   public abstract List<Artifact> getArtifactList(int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException;

   /**
    * @param countEstimate estimate of items to be returned; affects query performance
    * @return List of artifacts or empty list if none found
    */
   public abstract List<Artifact> getArtifactListHistorical(TransactionRecord transactionId, int countEstimate, LoadLevel loadLevel, QueryOption... queryOptions) throws OseeCoreException;

   /**
    * Return count of artifacts that would be returned from getArtifactList().size() but should be faster.
    */
   public abstract int getCount() throws OseeCoreException;

   public abstract Callable<?> getCallable();

   /**
    * Allows queries that may result in more Artifacts than are able to be handled at once.
    */
   public abstract Iterable<?> getIterable(int fetchSize);
}