/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaBranchChildOf extends Criteria {

   private final IOseeBranch parent;

   public CriteriaBranchChildOf(IOseeBranch parent) {
      super();
      this.parent = parent;
   }

   public IOseeBranch getParent() {
      return parent;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      Conditions.checkNotNull(getParent(), "Parent branch");
   }

   @Override
   public String toString() {
      return "CriteriaBranchChildOf [parent=" + parent + "]";
   }

}
