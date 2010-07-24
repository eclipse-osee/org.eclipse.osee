/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact.cache;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class HistoricalArtifactCache extends AbstractArtifactCache {

   public HistoricalArtifactCache(int initialCapacity) {
      super(initialCapacity);
   }

   @Override
   protected Integer getKey2(Artifact artifact) {
      return artifact.getTransactionNumber();
   }

   public Artifact getById(Integer artId, Integer transactionNumber) {
      return asArtifact(getObjectById(artId, transactionNumber));
   }

   public Artifact getByGuid(String artGuid, Integer transactionNumber) {
      return asArtifact(getObjectByGuid(artGuid, transactionNumber));
   }
}