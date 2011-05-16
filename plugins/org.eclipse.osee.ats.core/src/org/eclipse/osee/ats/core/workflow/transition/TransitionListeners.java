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
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public final class TransitionListeners {

   private static List<ITransitionListener> listeners;

   private TransitionListeners() {
      // private constructor
   }

   /*
    * due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   public synchronized static List<ITransitionListener> getListeners() {
      if (listeners == null) {
         ExtensionDefinedObjects<ITransitionListener> objects =
            new ExtensionDefinedObjects<ITransitionListener>("org.eclipse.osee.ats.core.AtsTransitionListener",
               "AtsTransitionListener", "classname");
         listeners = objects.getObjects();
      }
      return listeners;
   }

}
