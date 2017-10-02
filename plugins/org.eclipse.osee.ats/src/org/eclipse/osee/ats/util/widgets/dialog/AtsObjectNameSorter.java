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
public class AtsObjectNameSorter extends ViewerComparator {

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      if (o1 instanceof IAtsObject && o2 instanceof IAtsObject) {
         return getComparator().compare(((IAtsObject) o1).getName(), ((IAtsObject) o2).getName());
      } else if (o1 instanceof Artifact && o2 instanceof Artifact) {
         return getComparator().compare(((Artifact) o1).getName(), ((Artifact) o2).getName());
      } else if (o1 instanceof String && o2 instanceof String) {
         return getComparator().compare((String) o1, (String) o2);
      }
      return super.compare(viewer, o1, o2);
   }

}