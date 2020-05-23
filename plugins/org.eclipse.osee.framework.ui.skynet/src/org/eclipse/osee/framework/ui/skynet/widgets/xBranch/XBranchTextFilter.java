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

package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.ArrayList;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Donald G. Dunne
 */
public class XBranchTextFilter extends XViewerTextFilter {

   public XBranchTextFilter(XViewer viewer) {
      super(viewer);
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (element instanceof TransactionRecord) {
         return true;
      }
      if (element instanceof ArrayList<?>) {
         return true;
      }
      return super.select(viewer, parentElement, element);
   }

}
