/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
public interface WorkState extends Named {

   void setHoursSpent(double hoursSpent);

   void setPercentComplete(int percentComplete);

   List<AtsUser> getAssignees();

   double getHoursSpent();

   int getPercentComplete();

   void addAssignee(AtsUser steve);

   void setAssignees(List<? extends AtsUser> users);

   void setName(String name);

   void removeAssignee(AtsUser assignee);

}
