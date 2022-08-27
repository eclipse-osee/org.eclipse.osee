/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.workdef;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class StateDefinitionViewSorter extends ViewerSorter {

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      StateDefinition def1 = (StateDefinition) e1;
      StateDefinition def2 = (StateDefinition) e2;
      if (def1.getOrdinal() == def2.getOrdinal()) {
         return compareByName(def1, def2);
      } else if (def1.getOrdinal() < def2.getOrdinal()) {
         return -1;
      } else {
         return 1;
      }
   }

   private int compareByName(StateDefinition def1, StateDefinition def2) {
      return getComparator().compare(def1.getName(), def2.getName());
   }
}
