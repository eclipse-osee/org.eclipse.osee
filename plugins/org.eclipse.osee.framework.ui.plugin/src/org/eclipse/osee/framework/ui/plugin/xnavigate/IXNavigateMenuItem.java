/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
