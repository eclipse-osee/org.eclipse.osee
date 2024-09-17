/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Comparator when no sorting is desired, use order as supplied
 *
 * @author Donald G. Dunne
 */
public class NoOpViewerComparator extends ViewerComparator {

   public static NoOpViewerComparator instance = new NoOpViewerComparator();

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      return 0;
   }

}
