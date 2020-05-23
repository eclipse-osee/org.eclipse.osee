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

package org.eclipse.osee.framework.ui.skynet.menu;

import java.util.Collection;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu.GlobalMenuItem;

/**
 * @author Donald G. Dunne
 */
public interface GlobalMenuListener {

   /**
    * Called prior to performing the menu event. May or may not be called in display thread.
    * 
    * @return Result.False if action should NOT be performed Result.getText will be displayed as a popup to the user
    */
   public Result actioning(GlobalMenuItem item, Collection<Artifact> artifacts);

   /**
    * Called after performing the menu event.
    */
   public void actioned(GlobalMenuItem item, Collection<Artifact> artifacts);

}
