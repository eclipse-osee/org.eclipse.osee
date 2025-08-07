/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.util;

import org.eclipse.osee.framework.core.data.TransactionToken;

/**
 * @author Donald G Dunne
 */
public interface IAtsChangeSetListener {

   /**
    * Hook to allow additional changes to change set before execute
    */
   default public void changesStoring(IAtsChangeSet changes) {
      // for implementation
   }

   /**
    * Hook to perform operations after change set is executed.
    *
    * @param changes - for reference only, do not use this change set, use changesStored instead
    */
   default public void changesPersisted(IAtsChangeSet changes, TransactionToken tx) {
      // for implementation
   }

}
