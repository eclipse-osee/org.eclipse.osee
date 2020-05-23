/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Default sorter for artifacts. Sorts on descript ive name
 */
public class ArtifactNameSorter extends ViewerComparator {

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      if (o1 instanceof Artifact && o2 instanceof Artifact) {
         return getComparator().compare(((Artifact) o1).getName(), ((Artifact) o2).getName());
      } else if (o1 instanceof String && o2 instanceof String) {
         return getComparator().compare((String) o1, (String) o2);
      }
      return super.compare(viewer, o1, o2);
   }

}