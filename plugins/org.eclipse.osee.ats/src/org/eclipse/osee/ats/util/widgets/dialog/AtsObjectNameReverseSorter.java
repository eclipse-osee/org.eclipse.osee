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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Default sorter for artifacts. Sorts on descriptive name
 * 
 * @author Donald G. Dunne
 */
public class AtsObjectNameReverseSorter extends ViewerComparator {

   /**
    * Default sorter for artifacts. Sorts on descriptive name
    */
   public AtsObjectNameReverseSorter() {
      super();
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      if (o1 instanceof IAtsObject && o2 instanceof IAtsObject) {
         return getComparator().compare(((IAtsObject) o2).getName(), ((IAtsObject) o1).getName());
      } else if (o1 instanceof Artifact && o2 instanceof Artifact) {
         return getComparator().compare(((Artifact) o2).getName(), ((Artifact) o1).getName());
      } else if (o1 instanceof String && o2 instanceof String) {
         return getComparator().compare((String) o2, (String) o1);
      }
      return super.compare(viewer, o2, o1);
   }

}