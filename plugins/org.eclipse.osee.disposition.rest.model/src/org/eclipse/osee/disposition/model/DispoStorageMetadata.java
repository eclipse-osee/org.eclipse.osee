/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
