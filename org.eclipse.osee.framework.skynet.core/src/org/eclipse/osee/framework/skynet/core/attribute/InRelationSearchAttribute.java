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
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;

/**
 * @author Robert A. Fisher
 */
public class InRelationSearchAttribute extends StringAttribute implements ISearchAttribute<InRelationSearch> {
   /**
    * @param name
    */
   public InRelationSearchAttribute(DynamicAttributeDescriptor attributeType, String defaultValue) {
      super(attributeType, defaultValue);
   }

   /**
    * @param value
    */
   public void setSearchPrimitive(InRelationSearch value) {
      setStringData(value.getStorageString());
   }

   public InRelationSearch getSearchPrimitive() {
      return InRelationSearch.getPrimitive(getStringData());
   }
}
