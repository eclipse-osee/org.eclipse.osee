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
package org.eclipse.osee.ats.util.widgets.dialog;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * Default sorter for artifacts. Sorts on descriptive name
 *
 * @author Donald G. Dunne
 */
public class AtsObjectNameSorter extends ViewerComparator {

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      if (o1 instanceof Named && o2 instanceof Named) {
         return getComparator().compare(((Named) o2).getName(), ((Named) o1).getName());
      } else if (o1 instanceof String && o2 instanceof String) {
         return getComparator().compare((String) o1, (String) o2);
      }
      return super.compare(viewer, o1, o2);
   }

}