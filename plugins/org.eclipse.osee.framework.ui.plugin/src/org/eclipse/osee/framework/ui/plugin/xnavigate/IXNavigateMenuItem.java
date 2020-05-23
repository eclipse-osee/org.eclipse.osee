/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Implement this interface to give a chance to add MenuItems to the menu when item is right-clicked.
 *
 * @author Donald G. Dunne
 */
public interface IXNavigateMenuItem {

   void addMenuItems(Menu menu, TreeItem selectedTreeItem);

}
