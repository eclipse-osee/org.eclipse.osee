/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsObjects {

   List<JaxAtsObject> atsObjects = new LinkedList<>();

   public List<JaxAtsObject> getAtsObjects() {
      return atsObjects;
   }

   public void setAtsObjects(List<JaxAtsObject> atsObjects) {
      this.atsObjects = atsObjects;
   }

   public static JaxAtsObject create(IAtsObject atsObject) {
      JaxAtsObject obj = new JaxAtsObject();
      obj.setName(atsObject.getName());
      obj.setId(atsObject.getId());
      return obj;
   }
}
