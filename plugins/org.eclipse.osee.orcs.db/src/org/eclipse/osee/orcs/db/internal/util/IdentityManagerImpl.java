/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.util;

import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.TransactionId;
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
   public GammaId getNextGammaId() {
      return GammaId.valueOf(client.getNextSequence(OseeData.GAMMA_ID_SEQ, true));
   }

   @Override
   public String getUniqueGuid(String guid) {
      String toReturn = guid;
      if (toReturn == null) {
         toReturn = GUID.create();
      }
      return toReturn;
   }
}