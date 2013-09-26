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
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeSequence;

/**
 * @author Ryan D. Brooks
 */
public final class AtsIdProvider {

   private static final String SEQ_NAME = "ATS_ID_SEQ";
   private static final String ID_PREFIX = "ATS";
   private static AtsIdProvider instance;
   private IOseeSequence sequence;

   public AtsIdProvider() {
      instance = this;
   }

   public static AtsIdProvider get() {
      return instance;
   }

   public void setDatabaseService(IOseeDatabaseService databaseService) throws OseeDataStoreException {
      sequence = databaseService.getSequence();
   }

   public String getNextId() throws OseeCoreException {
      return String.format("%s%d", ID_PREFIX, sequence.getNextSequence(SEQ_NAME));
   }

}
