/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.internal.fields;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class BranchAliasesField extends CollectionField<String> {

   public BranchAliasesField(Collection<String> aliases) {
      super(aliases);
   }

   @Override
   protected Collection<String> checkInput(Collection<String> input) throws OseeCoreException {
      Collection<String> items = new HashSet<String>();
      for (String alias : input) {
         items.add(alias.toLowerCase());
      }
      return items;
   }
}
