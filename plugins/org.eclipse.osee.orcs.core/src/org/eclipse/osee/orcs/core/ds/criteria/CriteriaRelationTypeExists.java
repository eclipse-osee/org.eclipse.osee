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
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaRelationTypeExists extends Criteria {
   private final IRelationType relationType;

   public CriteriaRelationTypeExists(IRelationType relationType) {
      super();
      this.relationType = relationType;
   }

   public IRelationType getType() {
      return relationType;
   }

   @Override
   public void checkValid(Options options)  {
      Conditions.checkNotNull(getType(), "relation type");
   }

   @Override
   public String toString() {
      return "CriteriaRelationTypeExists [relationType=" + relationType + "]";
   }

}
