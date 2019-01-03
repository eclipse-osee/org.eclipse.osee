/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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