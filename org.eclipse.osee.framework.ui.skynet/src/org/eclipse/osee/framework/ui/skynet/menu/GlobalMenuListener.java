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
package org.eclipse.osee.framework.ui.skynet.menu;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu.GlobalMenuItem;

/**
 * @author Donald G. Dunne
 */
public interface GlobalMenuListener {

   /**
    * Called prior to performing the menu event. May or may not be called in display thread.
    * 
    * @param item
    * @param artifacts
    * @return Result.False if action should NOT be performed Result.getText will be displayed as a popup to the user
    */
   public Result actioning(GlobalMenuItem item, Collection<Artifact> artifacts);

   /**
    * Called after performing the menu event.
    * 
    * @param item
    * @param artifacts
    */
   public void actioned(GlobalMenuItem item, Collection<Artifact> artifacts);

}
