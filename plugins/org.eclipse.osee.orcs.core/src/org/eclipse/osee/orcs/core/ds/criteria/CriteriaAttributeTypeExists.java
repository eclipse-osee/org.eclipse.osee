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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttributeTypeExists extends Criteria {

   private final Collection<? extends IAttributeType> attributeTypes;

   public CriteriaAttributeTypeExists(Collection<? extends IAttributeType> attributeTypes) {
      super();
      this.attributeTypes = attributeTypes;
   }

   public Collection<? extends IAttributeType> getTypes() {
      return attributeTypes;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkNotNullOrEmpty(getTypes(), "attribute types");
   }

   @Override
   public String toString() {
      return "CriteriaAttributeTypeExists [attributeTypes=" + attributeTypes + "]";
   }
}
