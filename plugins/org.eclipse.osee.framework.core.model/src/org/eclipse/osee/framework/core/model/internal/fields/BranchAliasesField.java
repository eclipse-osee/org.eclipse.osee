/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.model.internal.fields;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Roberto E. Escobar
 */
public class BranchAliasesField extends CollectionField<String> {

   public BranchAliasesField(Collection<String> aliases) {
      super(aliases);
   }

   @Override
   protected Collection<String> checkInput(Collection<String> input) {
      Collection<String> items = new HashSet<>();
      for (String alias : input) {
         items.add(alias.toLowerCase());
      }
      return items;
   }
}
