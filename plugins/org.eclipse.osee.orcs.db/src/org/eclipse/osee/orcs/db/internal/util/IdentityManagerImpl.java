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
package org.eclipse.osee.orcs.db.internal.util;

import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.db.internal.IdentityManager;

/**
 * @author Roberto E. Escobar
 */
public class IdentityManagerImpl implements IdentityManager {

   private final JdbcClient client;

   public IdentityManagerImpl(JdbcClient client) {
      super();
      this.client = client;
   }

   @Override
   public TransactionId getNextTransactionId() {
      //keep transaction id's sequential in the face of concurrent transaction by multiple users
      return TransactionId.valueOf(client.getNextSequence(OseeData.TRANSACTION_ID_SEQ, false));
   }

   @Override
   public int getNextArtifactId() {
      return (int) client.getNextSequence(OseeData.ART_ID_SEQ, true);
   }

   @Override
   public int getNextAttributeId() {
      return (int) client.getNextSequence(OseeData.ATTR_ID_SEQ, true);
   }

   @Override
   public int getNextRelationId() {
      return (int) client.getNextSequence(OseeData.REL_LINK_ID_SEQ, true);
   }

   @Override
   public long getNextGammaId() {
      return client.getNextSequence(OseeData.GAMMA_ID_SEQ, true);
   }

   @Override
   public String getUniqueGuid(String guid) {
      String toReturn = guid;
      if (toReturn == null) {
         toReturn = GUID.create();
      }
      return toReturn;
   }

   @Override
   public Long parseToLocalId(String value)  {
      return Long.valueOf(value);
   }

   @Override
   public void invalidateIds() throws OseeDataStoreException {
      client.invalidateSequences();
   }

}
