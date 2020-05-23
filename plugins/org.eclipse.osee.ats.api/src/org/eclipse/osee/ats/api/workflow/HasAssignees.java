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

/**
 * @author Donald G. Dunne
 */
public interface HasAssignees {

   public List<AtsUser> getAssignees();

   public List<AtsUser> getImplementers();

}
