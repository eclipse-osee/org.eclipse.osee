/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;

/**
 * @author Donald G. Dunne
 */
public class LayoutItem extends AbstractWorkDefItem implements IAtsLayoutItem {

   public LayoutItem(String name) {
      super(Long.valueOf(name.hashCode()), name);
   }

}
