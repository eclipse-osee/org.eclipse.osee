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
package org.eclipse.osee.ats.workflow.transition;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.workflow.review.DecisionReviewDefinitionManager;
import org.eclipse.osee.ats.workflow.review.PeerReviewDefinitionManager;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public final class TransitionListeners {

   private static List<ITransitionListener> listeners;
   private static boolean loaded = false;

   private TransitionListeners() {
      // private constructor
   }

   /**
    * Add listener for notification. This is not the recommended method of listening, use
    * org.eclipse.osee.ats.core.AtsTransitionListener extension point. <br/>
    * Public method available in TransitionManager
    */
   public static void addListener(ITransitionListener listener) {
      listeners.add(listener);
   }

   public static void removeListener(ITransitionListener listener) {
      listeners.remove(listener);
   }

   /**
    * Load listeners from extensions. <br/>
    * <br/>
    * Due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   public synchronized static List<ITransitionListener> getListeners() {
      if (!loaded) {
         listeners = new ArrayList<>();
         listeners.add(new DecisionReviewDefinitionManager());
         listeners.add(new PeerReviewDefinitionManager());

         ExtensionDefinedObjects<ITransitionListener> objects = new ExtensionDefinedObjects<ITransitionListener>(
            "org.eclipse.osee.ats.AtsTransitionListener", "AtsTransitionListener", "classname");
         listeners.addAll(objects.getObjects());
         loaded = true;
      }
      return listeners;
   }

}
