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
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;

/**
 * @author Donald G. Dunne
 */
public interface IAtsPeerReviewRoleManager {

   void addOrUpdateUserRole(UserRole userRole);

   void removeUserRole(UserRole userRole);

   void saveToArtifact(IAtsChangeSet changes);

   boolean validateMinimumForRoleType(ReviewRoleType reviewType);

   List<UserRole> getUserRoles();

   List<AtsUser> getRoleUsers(ReviewRole role);

   List<AtsUser> getRoleUsers(Collection<UserRole> roles);

   UserRoleError validateRoleTypeMinimums(StateDefinition fromStateDef, IAtsPeerReviewRoleManager roleMgr);

   List<UserRole> getUserRoles(ReviewRole role);

   WorkDefinition getWorkDefinition();

}