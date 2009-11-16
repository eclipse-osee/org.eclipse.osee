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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AbstractOseeField;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.ChangeUtil;

/**
 * @author Roberto E. Escobar
 */
public class ChildBranchesField extends AbstractOseeField<Collection<Branch>> {

   private final List<Branch> childBranches;

   public ChildBranchesField() {
      super();
      this.childBranches = new ArrayList<Branch>();
   }

   @Override
   public Collection<Branch> get() throws OseeCoreException {
      return childBranches;
   }

   @Override
   public void set(Collection<Branch> branches) throws OseeCoreException {
      boolean wasDifferent = ChangeUtil.isDifferent(get(), resolve(branches));
      if (wasDifferent) {
         this.childBranches.clear();
         this.childBranches.addAll(branches);
      }
      isDirty |= wasDifferent;
   }

   private Collection<Branch> resolve(Collection<Branch> branches) {

      return branches;
   }
}