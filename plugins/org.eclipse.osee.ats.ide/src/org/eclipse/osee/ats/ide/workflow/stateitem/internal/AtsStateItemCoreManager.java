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
package org.eclipse.osee.ats.ide.workflow.stateitem.internal;

import java.util.List;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.stateitem.IAtsStateItemCore;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Megumi Telles
 */
public class AtsStateItemCoreManager {

   private static final ExtensionDefinedObjects<IAtsStateItemCore> extensionDefinedObjects =
      new ExtensionDefinedObjects<>(Activator.PLUGIN_ID + ".AtsStateItemCore", "AtsStateItemCore",
         "classname", true);

   public static List<IAtsStateItemCore> getStateItems() {
      return extensionDefinedObjects.getObjects();
   }

}
