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

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.util.HexUtil;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeSequence;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.db.internal.IdentityManager;

/**
 * @author Roberto E. Escobar
 */
public class IdentityManagerImpl implements IdentityManager {

   private final IOseeDatabaseService dbService;

   public IdentityManagerImpl(IOseeDatabaseService dbService) {
      super();
      this.dbService = dbService;
   }

   private IOseeSequence getSequence() throws OseeDataStoreException {
      return dbService.getSequence();
   }

   @Override
   public int getNextArtifactId() throws OseeCoreException {
      return getSequence().getNextArtifactId();
   }

   @Override
   public int getNextAttributeId() throws OseeCoreException {
      return getSequence().getNextAttributeId();
   }

   @Override
   public int getNextRelationId() throws OseeCoreException {
      return getSequence().getNextRelationId();
   }

   @Override
   public long getNextGammaId() throws OseeCoreException {
      return getSequence().getNextGammaId();
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
   public Long parseToLocalId(String value) throws OseeCoreException {
      return HexUtil.toLong(value);
   }

   @Override
   public void invalidateIds() throws OseeDataStoreException {
      getSequence().clear();
   }

}
