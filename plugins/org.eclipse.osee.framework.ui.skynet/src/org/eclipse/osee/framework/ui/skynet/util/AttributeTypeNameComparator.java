/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

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
      return ((AttributeTypeToken) o1).getName().compareTo(((AttributeTypeToken) o2).getName());
   }

}