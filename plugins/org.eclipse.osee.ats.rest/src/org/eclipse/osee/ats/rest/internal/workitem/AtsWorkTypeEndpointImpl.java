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

package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.workflow.AtsWorkTypeEndpoint;

/**
 * @author Ryan T. Baldwin
 */
public final class AtsWorkTypeEndpointImpl implements AtsWorkTypeEndpoint {

   public AtsWorkTypeEndpointImpl() {
   }

   @Override
   public Collection<WorkType> get() {
      return WorkType.All.values().stream().filter(f -> f != null).map(o -> (WorkType) o).sorted(
         new Comparator<WorkType>() {
            @Override
            public int compare(WorkType o1, WorkType o2) {
               return o1.getHumanReadableName().compareTo(o2.getHumanReadableName());
            }
         }).collect(Collectors.toList());
   }

}
