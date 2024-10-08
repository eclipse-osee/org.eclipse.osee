/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

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
         return getComparator().compare(((Named) o1).getName(), ((Named) o2).getName());
      } else if (o1 instanceof String && o2 instanceof String) {
         return getComparator().compare((String) o1, (String) o2);
      }
      return super.compare(viewer, o1, o2);
   }

}