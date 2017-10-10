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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class ToStringViewerSorter extends ViewerSorter {

   private final boolean showDashedFirst;

   public ToStringViewerSorter() {
      this(false);
   }

   /**
    * @param dashedFirst sorts entries with "--" first in list. Supports entries like "--clear--" or "--all--".
    */
   public ToStringViewerSorter(boolean showDashedFirst) {
      super();
      this.showDashedFirst = showDashedFirst;
   }

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      String s1 = e1 instanceof Named ? ((Named) e1).getName() : e1.toString();
      String s2 = e2 instanceof Named ? ((Named) e2).getName() : e2.toString();
      if (showDashedFirst) {
         if (s1.startsWith("-") && !s2.startsWith("-")) {
            return -1;
         }
         if (!s1.startsWith("-") && s2.startsWith("-")) {
            return 1;
         }
      }
      return getComparator().compare(s1, s2);
   }

}
