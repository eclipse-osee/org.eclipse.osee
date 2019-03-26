/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.data.ArtifactToken.ArtifactTokenImpl;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class UserGroupArtifactToken extends ArtifactTokenImpl implements IUserGroupArtifactToken {

   public UserGroupArtifactToken(Long id, String name) {
      super(id, GUID.create(), name, CoreBranches.COMMON, CoreArtifactTypes.UserGroup);
   }

   public static IUserGroupArtifactToken valueOf(Long id, String name) {
      return new UserGroupArtifactToken(id, name);
   }

}
