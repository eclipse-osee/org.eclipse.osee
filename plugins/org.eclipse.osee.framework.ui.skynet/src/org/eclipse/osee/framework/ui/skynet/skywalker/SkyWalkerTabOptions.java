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
package org.eclipse.osee.framework.ui.skynet.skywalker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author Donald G. Dunne
 */
public class SkyWalkerTabOptions {

   private TabFolder tabFolder;

   /**
    * @param parent
    * @param style
    */
   public SkyWalkerTabOptions(Composite parent, int style, SkyWalkerOptions options) {
      tabFolder = new TabFolder(parent, SWT.BORDER);

      new SkyWalkerLayoutTabItem(tabFolder, options);
      new SkyWalkerArtTypeTabItem(tabFolder, options);
      new SkyWalkerRelTypeTabItem(tabFolder, options);
      new SkyWalkerShowAttributeTabItem(tabFolder, options);
   }

}
