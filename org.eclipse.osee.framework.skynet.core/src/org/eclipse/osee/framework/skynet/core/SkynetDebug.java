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
package org.eclipse.osee.framework.skynet.core;

import org.eclipse.osee.framework.ui.plugin.util.Debug;

/**
 * @author Donald G. Dunne
 */
public class SkynetDebug extends Debug {

   protected String getPluginName() {
      return "AtsPlugin";
   }

   /**
    * @param debugOn
    * @param timeStampOn
    * @param nameSpace
    */
   public SkynetDebug(boolean debugOn, boolean timeStampOn, String nameSpace) {
      super(debugOn, timeStampOn, nameSpace);
   }

   /**
    * @param debugOn
    * @param nameSpace
    */
   public SkynetDebug(boolean debugOn, String nameSpace) {
      super(debugOn, nameSpace);
   }
}
