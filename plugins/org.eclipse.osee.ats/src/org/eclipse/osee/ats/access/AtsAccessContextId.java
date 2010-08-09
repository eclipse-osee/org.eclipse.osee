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
package org.eclipse.osee.ats.access;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsAccessContextId extends NamedIdentity implements AccessContextId {

   public static Map<String, AtsAccessContextId> guidToIds = new HashMap<String, AtsAccessContextId>();

   protected AtsAccessContextId(String guid, String name) {
      super(guid, name);
      if (guidToIds.containsKey(guidToIds)) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, String.format(
            "Duplicate AtsAccessContextIds with guid [%s] named [%s] and [%s]", guid, name,
            guidToIds.get(guid).getName()));
      } else {
         guidToIds.put(guid, this);
      }
   }

   @Override
   public String toString() {
      return String.format("%s - %s", getName(), getGuid());
   }
}
