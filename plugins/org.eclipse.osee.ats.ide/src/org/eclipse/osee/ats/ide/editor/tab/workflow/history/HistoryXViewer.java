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

package org.eclipse.osee.ats.ide.editor.tab.workflow.history;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class HistoryXViewer extends XViewer {

   public HistoryXViewer(Composite parent, int style) {
      super(parent, style, new HistoryXViewerFactory());
   }

   @Override
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
   }

}
