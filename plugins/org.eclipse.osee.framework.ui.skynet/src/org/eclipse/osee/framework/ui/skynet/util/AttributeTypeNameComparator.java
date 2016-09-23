/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.IAttributeType;

/**
 * Default sorter for artifacts. Sorts on descriptive name
 *
 * @author Donald G. Dunne
 */
public class AttributeTypeNameComparator extends ViewerComparator {

   /**
    * Default sorter for attributes. Sorts on descriptive name
    */
   public AttributeTypeNameComparator() {
      super();
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      return ((IAttributeType) o1).getName().compareTo(((IAttributeType) o2).getName());
   }

}