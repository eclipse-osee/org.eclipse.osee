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

package org.eclipse.osee.ats.ide.workflow.review;

import java.util.List;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsReviewHook;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public final class ReviewProviders {

   private static List<IAtsReviewHook> reviewProvider;

   private ReviewProviders() {
      // private constructor
   }

   /*
    * due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   public synchronized static List<IAtsReviewHook> getAtsReviewProviders() {
      if (reviewProvider == null) {

         ExtensionDefinedObjects<IAtsReviewHook> objects = new ExtensionDefinedObjects<>(
            Activator.PLUGIN_ID + ".AtsReviewProvider", "AtsReviewProvider", "classname", true);
         reviewProvider = objects.getObjects();

      }
      return reviewProvider;
   }

}
