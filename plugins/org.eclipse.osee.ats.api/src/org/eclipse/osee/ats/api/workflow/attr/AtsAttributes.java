/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.workflow.attr;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributes {

   List<AtsAttribute> attrs = new LinkedList<>();

   public void add(String id, String name, String url) {
      attrs.add(new AtsAttribute(id, name, url));
   }

   public List<AtsAttribute> getAttrs() {
      return attrs;
   }

   public void setAttrs(List<AtsAttribute> attrs) {
      this.attrs = attrs;
   }
}
