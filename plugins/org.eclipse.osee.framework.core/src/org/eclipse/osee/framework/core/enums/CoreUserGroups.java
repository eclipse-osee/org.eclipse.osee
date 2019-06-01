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
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class CoreUserGroups {

   public static final IUserGroupArtifactToken Everyone = UserGroupArtifactToken.valueOf(48656L, "Everyone");
   public static final IUserGroupArtifactToken OseeAccessAdmin =
      UserGroupArtifactToken.valueOf(8033605L, "OseeAccessAdmin");
   public static final IUserGroupArtifactToken OseeAdmin = UserGroupArtifactToken.valueOf(52247L, "OseeAdmin");
   public static final IUserGroupArtifactToken OseeDeveloper =
      UserGroupArtifactToken.valueOf(464565465L, "OseeDeveloper");

}
