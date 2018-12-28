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
package org.eclipse.osee.ats.world;

import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public class AtsWorldEditorItems {

   private static final ExtensionDefinedObjects<IAtsWorldEditorItem> items =
      new ExtensionDefinedObjects<>("org.eclipse.osee.ats.AtsWorldEditorItem", "AtsWorldEditorItem",
         "classname");

   public static List<IAtsWorldEditorItem> getItems() {
      return items.getObjects();
   }
}
