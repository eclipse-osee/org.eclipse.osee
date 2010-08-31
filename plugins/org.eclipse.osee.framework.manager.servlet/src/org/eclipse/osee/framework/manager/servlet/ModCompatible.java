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
package org.eclipse.osee.framework.manager.servlet;

import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ArtifactTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.ArtifactTypeCacheUpdateResponse.ArtifactTypeRow;
import org.eclipse.osee.framework.core.message.AttributeTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.BranchCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.BranchRow;
import org.eclipse.osee.framework.core.message.OseeEnumTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.RelationTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.RelationTypeCacheUpdateResponse.RelationTypeRow;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public final class ModCompatible {

   private ModCompatible() {
      // Utility Class
   }

   public static boolean is_0_9_2_Compatible(String clientVersion) {
      boolean result = false;
      if (Strings.isValid(clientVersion)) {
         String toCheck = clientVersion.toLowerCase();
         if (!toCheck.startsWith("0.9.0") && !toCheck.startsWith("0.9.1")) {
            result = true;
         }
      }
      return result;
   }

   public static String getClientVersion(ISessionManager manager, String sessionId) throws OseeCoreException {
      String clientVersion = null;
      if (Strings.isValid(sessionId)) {
         ISession session = manager.getSessionById(sessionId);
         if (session != null) {
            clientVersion = session.getClientVersion();
         }
      }
      return clientVersion;
   }

   private static StorageState getCompatibleState(StorageState state) {
      StorageState toReturn = state;
      if (state == StorageState.PURGED) {
         toReturn = StorageState.DELETED;
      } else if (state == StorageState.LOADED) {
         toReturn = StorageState.MODIFIED;
      }
      return toReturn;
   }

   public static void makeSendCompatible(boolean isCompatible, Object response) {
      if (!isCompatible) {
         if (response instanceof BranchCacheUpdateResponse) {
            BranchCacheUpdateResponse data = (BranchCacheUpdateResponse) response;
            for (BranchRow row : data.getBranchRows()) {
               row.setStorageState(getCompatibleState(row.getStorageState()));
            }
         } else if (response instanceof ArtifactTypeCacheUpdateResponse) {
            ArtifactTypeCacheUpdateResponse data = (ArtifactTypeCacheUpdateResponse) response;
            for (ArtifactTypeRow row : data.getArtTypeRows()) {
               row.setStorageState(getCompatibleState(row.getStorageState()));
            }

         } else if (response instanceof AttributeTypeCacheUpdateResponse) {
            AttributeTypeCacheUpdateResponse data = (AttributeTypeCacheUpdateResponse) response;
            for (AttributeType row : data.getAttrTypeRows()) {
               row.setStorageState(getCompatibleState(row.getStorageState()));
            }
         } else if (response instanceof RelationTypeCacheUpdateResponse) {
            RelationTypeCacheUpdateResponse data = (RelationTypeCacheUpdateResponse) response;
            for (RelationTypeRow row : data.getRelationTypeRows()) {
               row.setStorageState(getCompatibleState(row.getStorageState()));
            }

         } else if (response instanceof OseeEnumTypeCacheUpdateResponse) {
            OseeEnumTypeCacheUpdateResponse data = (OseeEnumTypeCacheUpdateResponse) response;
            for (String[] types : data.getEnumTypeRows()) {
               types[1] = getCompatibleState(StorageState.valueOf(types[1])).name();
            }
         }
      }
   }
}
