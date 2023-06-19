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

package org.eclipse.osee.ats.ide.world;

import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public class AtsWorldEditorItems {

   private static final ExtensionDefinedObjects<IAtsWorldEditorItem> items =
      new ExtensionDefinedObjects<>("org.eclipse.osee.ats.ide.AtsWorldEditorItem", "AtsWorldEditorItem", "classname");

   public static List<IAtsWorldEditorItem> getItems() {
      return items.getObjects();
   }
}
