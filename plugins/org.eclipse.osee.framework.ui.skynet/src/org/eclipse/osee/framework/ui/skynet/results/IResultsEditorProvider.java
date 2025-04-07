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

package org.eclipse.osee.framework.ui.skynet.results;

import java.util.List;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Donald G. Dunne
 */
public interface IResultsEditorProvider {

   public String getEditorName();

   public List<IResultsEditorTab> getResultsEditorTabs();

   default boolean expandAll() {
      return false;
   }

   default public void addTableToolbarItem(ToolBar toolBar) {
      // for extension to add items to toolBar
   }

}
