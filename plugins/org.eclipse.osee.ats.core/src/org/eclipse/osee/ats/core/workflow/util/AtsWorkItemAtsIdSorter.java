/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.core.workflow.util;

import java.util.Comparator;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * Default sorter for ATS Work Items
 *
 * @author Donald G. Dunne
 */
public class AtsWorkItemAtsIdSorter implements Comparator<IAtsWorkItem> {

   @Override
   public int compare(IAtsWorkItem o1, IAtsWorkItem o2) {
      return (o1.getAtsId().compareTo(o2.getAtsId()));
   }

}