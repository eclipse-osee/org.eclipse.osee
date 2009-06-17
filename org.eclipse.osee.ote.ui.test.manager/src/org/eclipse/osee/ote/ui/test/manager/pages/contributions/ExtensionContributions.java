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
package org.eclipse.osee.ote.ui.test.manager.pages.contributions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;

/**
 * @author Roberto E. Escobar
 */
public class ExtensionContributions {

   private static String ADVANCED_PAGE_EXTENSION_ELEMENT = "AdvancedPageContribution";
   private static String ADVANCED_PAGE_EXTENSION_ID =
         TestManagerPlugin.PLUGIN_ID + "." + ADVANCED_PAGE_EXTENSION_ELEMENT;
   private static String EXTENSION_ATTRIBUTE = "classname";

   private ExtensionDefinedObjects<IAdvancedPageContribution> advancedPageContributions;

   public ExtensionContributions() {
      this.advancedPageContributions =
            new ExtensionDefinedObjects<IAdvancedPageContribution>(ADVANCED_PAGE_EXTENSION_ID,
                  ADVANCED_PAGE_EXTENSION_ELEMENT, EXTENSION_ATTRIBUTE);
   }

   public List<IAdvancedPageContribution> getAdvancedPageContributions() {
      List<IAdvancedPageContribution> toReturn = new ArrayList<IAdvancedPageContribution>();
      try {
         toReturn = advancedPageContributions.getObjects();
      } catch (Exception ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex.getMessage(), ex);
      }
      return toReturn;
   }
}
