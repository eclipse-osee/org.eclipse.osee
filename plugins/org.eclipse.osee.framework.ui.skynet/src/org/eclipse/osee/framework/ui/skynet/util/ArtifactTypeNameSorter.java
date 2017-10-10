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
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.data.IArtifactType;

/**
 * Default sorter for artifacts. Sorts on descriptive name
 */
@SuppressWarnings("deprecation")
public class ArtifactTypeNameSorter extends ViewerSorter {

   /**
    * Default sorter for artifacts. Sorts on descriptive name
    */
   public ArtifactTypeNameSorter() {
      super();
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {

      return getComparator().compare(((IArtifactType) o1).getName(), ((IArtifactType) o2).getName());
   }

}