/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author John R. Misinco
 */
public class HandleImport {

   private static final String CLASSNAME = "classname";
   private static final String IMPORT_HANDLER_ID = "ImportHandler";

   public static void handleImport(Collection<URI> resources, Object destination, boolean persistChanges) {
      ExtensionDefinedObjects<ImportHandler> handlerExtensions = new ExtensionDefinedObjects<ImportHandler>(
         Activator.PLUGIN_ID + "." + IMPORT_HANDLER_ID, IMPORT_HANDLER_ID, CLASSNAME);
      List<ImportHandler> handlers = handlerExtensions.getObjects();
      Collections.sort(handlers, new Comparator<ImportHandler>() {

         @Override
         public int compare(ImportHandler o1, ImportHandler o2) {
            Integer left = new Integer(o1.getRank());
            Integer right = new Integer(o2.getRank());
            return left.compareTo(right);
         }

      });
      for (ImportHandler handler : handlers) {
         if (handler.process(resources, destination, persistChanges)) {
            break;
         }
      }
   }
}
