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

package org.eclipse.osee.ats.core.config;

import java.io.Serializable;
import java.util.Comparator;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemSorter implements Comparator<IAtsActionableItem>, Serializable {

   private static final long serialVersionUID = 1L;

   public ActionableItemSorter() {
      super();
   }

   @Override
   public int compare(IAtsActionableItem o1, IAtsActionableItem o2) {
      return o1.getName().compareTo(o2.getName());
   }
}