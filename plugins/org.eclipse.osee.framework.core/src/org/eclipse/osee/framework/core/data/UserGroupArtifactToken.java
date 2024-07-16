/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.data.ArtifactToken.ArtifactTokenImpl;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;

/**
 * @author Donald G. Dunne
 */
public class UserGroupArtifactToken extends ArtifactTokenImpl implements IUserGroupArtifactToken {

   public UserGroupArtifactToken(Long id, String name) {
      super(id, name, CoreBranches.COMMON, CoreArtifactTypes.UserGroup);
   }

   public static IUserGroupArtifactToken valueOf(Long id, String name) {
      return new UserGroupArtifactToken(id, name);
   }
}