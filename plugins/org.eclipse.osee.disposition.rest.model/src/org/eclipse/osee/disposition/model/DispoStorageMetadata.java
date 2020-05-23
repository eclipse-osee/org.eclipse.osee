/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.disposition.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Angel Avila
 */
public class DispoStorageMetadata {

   private final Set<String> idsOfUpdatedItems = new HashSet<>();

   public DispoStorageMetadata() {

   }

   public Set<String> getIdsOfUpdatedItems() {
      return idsOfUpdatedItems;
   }

   public void addIdOfUpdatedItem(String id) {
      idsOfUpdatedItems.add(id);
   }
}
