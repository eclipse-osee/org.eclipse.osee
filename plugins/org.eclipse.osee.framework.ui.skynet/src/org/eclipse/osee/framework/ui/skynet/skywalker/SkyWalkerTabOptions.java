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

package org.eclipse.osee.framework.ui.skynet.skywalker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author Donald G. Dunne
 */
public class SkyWalkerTabOptions {

   private final TabFolder tabFolder;

   public SkyWalkerTabOptions(Composite parent, int style, SkyWalkerOptions options) {
      tabFolder = new TabFolder(parent, SWT.BORDER);

      new SkyWalkerLayoutTabItem(tabFolder, options);
      new SkyWalkerArtTypeTabItem(tabFolder, options);
      new SkyWalkerRelTypeTabItem(tabFolder, options);
      new SkyWalkerShowAttributeTabItem(tabFolder, options);
   }

   public TabFolder getControl() {
      return tabFolder;
   }

}
