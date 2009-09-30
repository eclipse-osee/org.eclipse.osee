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
package org.eclipse.osee.framework.skynet.core.types.field;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;

/**
 * @author Roberto E. Escobar
 */
public class AliasesField extends AbstractOseeField<Collection<String>> {

   private final BranchCache cache;
   private final Branch branch;

   public AliasesField(BranchCache cache, Branch branch) {
      super();
      this.branch = branch;
      this.cache = cache;
   }

   @Override
   public Collection<String> get() throws OseeCoreException {
      return cache.getAliases(branch);
   }

   @Override
   public void set(Collection<String> aliases) throws OseeCoreException {
      Collection<String> original = get();
      cache.setAliases(branch, aliases);
      Collection<String> other = get();
      isDirty |= ChangeUtil.isDifferent(original, other);
   }
}
