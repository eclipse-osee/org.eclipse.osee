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
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.OseeRunMode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Checks mode based on OSEESiteConfig file
 * 
 * @author Jeff C. Phillips
 */
public class ModeChecker {

   public ModeChecker() {
      super();
   }

   /**
    * Changes composite's color if running development mode.
    * 
    * @param parent
    */
   public static void check(Composite parent) {

      if (ConfigUtil.getConfigFactory().getOseeConfig().getRunMode().equals(OseeRunMode.Development)) parent.setBackground(Display.getDefault().getSystemColor(
            SWT.COLOR_BLUE));
   }
}
