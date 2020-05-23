/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.api.program.IAtsProgram;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class AtsProgramViewerSorter extends ViewerSorter {

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      return getComparator().compare(((IAtsProgram) e1).getName(), ((IAtsProgram) e2).getName());
   }
}