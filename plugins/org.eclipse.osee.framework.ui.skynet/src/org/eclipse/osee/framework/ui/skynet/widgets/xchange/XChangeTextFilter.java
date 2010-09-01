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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.Collection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;

/**
 * @author Donald G. Dunne
 */
public class XChangeTextFilter extends XViewerTextFilter {

   private boolean showDocumentOrderFilter;

   public XChangeTextFilter(ChangeXViewer changeXViewer) {
      super(changeXViewer);
   }

   public boolean isShowDocumentOrderFilter() {
      return showDocumentOrderFilter;
   }

   public void setShowDocumentOrderFilter(boolean showDocumentOrderFilter) {
      this.showDocumentOrderFilter = showDocumentOrderFilter;
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      boolean accept = false;
      if (isShowDocumentOrderFilter()) {
         //Do nothing
         if (parentElement instanceof Collection<?>) {
            accept = true;
         }
      } else {
         accept = super.select(viewer, parentElement, element);
      }
      return accept;
   }
}
