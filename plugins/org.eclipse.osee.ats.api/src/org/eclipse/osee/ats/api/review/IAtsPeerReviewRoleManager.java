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

package org.eclipse.osee.ats.api.review;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;

/**
 * @author Donald G. Dunne
 */
public interface IAtsPeerReviewRoleManager {

   List<UserRole> getUserRoles();

   List<UserRole> getUserRoles(Role role);

   List<AtsUser> getRoleUsers(Role role);

   List<AtsUser> getRoleUsers(Collection<UserRole> roles);

   void addOrUpdateUserRole(UserRole userRole);

   void removeUserRole(UserRole userRole);

   void saveToArtifact(IAtsChangeSet changes);

}