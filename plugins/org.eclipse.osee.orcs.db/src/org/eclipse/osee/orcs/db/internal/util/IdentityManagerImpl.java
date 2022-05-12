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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.db.internal.IdentityManager;

/**
 * @author Roberto E. Escobar
 */
public class IdentityManagerImpl implements IdentityManager {

   private final JdbcClient client;
   public static boolean USE_LONG_IDS = ArtifactToken.USE_LONG_IDS;

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
   public ArtifactId getNextArtifactId() {
      if (USE_LONG_IDS) {
         return ArtifactId.valueOf(Lib.generateUuid());
      } else {
         return ArtifactId.valueOf(client.getNextSequence(OseeData.ART_ID_SEQ, true));
      }
   }

   @Override
   public AttributeId getNextAttributeId() {
      if (USE_LONG_IDS) {
         return AttributeId.valueOf(Lib.generateUuid());
      } else {
         return AttributeId.valueOf(client.getNextSequence(OseeData.ATTR_ID_SEQ, true));
      }
   }

   @Override
   public RelationId getNextRelationId() {
      if (USE_LONG_IDS) {
         return RelationId.valueOf(Lib.generateUuid());
      } else {
         return RelationId.valueOf(client.getNextSequence(OseeData.REL_LINK_ID_SEQ, true));
      }
   }

   @Override
   public GammaId getNextGammaId() {
      if (USE_LONG_IDS) {
         return GammaId.valueOf(Lib.generateUuid());
      } else {
         return GammaId.valueOf(client.getNextSequence(OseeData.GAMMA_ID_SEQ, true));
      }
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