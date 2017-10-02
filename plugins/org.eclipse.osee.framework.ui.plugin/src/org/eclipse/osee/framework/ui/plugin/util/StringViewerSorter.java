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
package org.eclipse.osee.framework.ui.plugin.util;

import java.text.Collator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * @author Donald G. Dunne
 */
public class StringViewerSorter extends ViewerComparator {

   public StringViewerSorter() {
      // do nothing
   }

   public StringViewerSorter(Collator collator) {
      super(collator);
   }

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
	  String s1 = e1 != null ? e1.toString() : null;
	  String s2 = e2 != null ? e2.toString() : null;
      return getComparator().compare(s1, s2);
   }
}
