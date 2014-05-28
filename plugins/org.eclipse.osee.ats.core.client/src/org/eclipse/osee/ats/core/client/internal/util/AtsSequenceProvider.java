/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.util;

import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseService;

/**
 * @author Donald G. Dunne
 */
public class AtsSequenceProvider implements ISequenceProvider {

   private final IOseeDatabaseService databaseService;

   public AtsSequenceProvider(IOseeDatabaseService databaseService) {
      this.databaseService = databaseService;
   }

   @Override
   public long getNext(String name) {
      return databaseService.getSequence().getNextSequence(name);
   }
}
