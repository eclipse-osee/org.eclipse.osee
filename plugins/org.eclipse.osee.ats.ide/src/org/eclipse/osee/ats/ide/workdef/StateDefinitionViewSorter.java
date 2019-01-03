/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workdef;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class StateDefinitionViewSorter extends ViewerSorter {

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      IAtsStateDefinition def1 = (IAtsStateDefinition) e1;
      IAtsStateDefinition def2 = (IAtsStateDefinition) e2;
      if (def1.getOrdinal() == def2.getOrdinal()) {
         return compareByName(def1, def2);
      } else if (def1.getOrdinal() < def2.getOrdinal()) {
         return -1;
      } else {
         return 1;
      }
   }

   private int compareByName(IAtsStateDefinition def1, IAtsStateDefinition def2) {
      return getComparator().compare(def1.getName(), def2.getName());
   }
}
