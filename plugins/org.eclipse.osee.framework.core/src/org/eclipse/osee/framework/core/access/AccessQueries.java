/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access;

/**
 * @author Donald G. Dunne
 */
public class AccessQueries {

   public static final String INSERT_INTO_ARTIFACT_ACL =
      "INSERT INTO OSEE_ARTIFACT_ACL (art_id, permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?, ?)";

   public static final String UPDATE_ARTIFACT_ACL =
      "UPDATE OSEE_ARTIFACT_ACL SET permission_id = ? WHERE privilege_entity_id =? AND art_id = ? AND branch_id = ?";

   public static final String GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST =
      "SELECT aac1.*, art1.art_type_id FROM osee_artifact art1, osee_artifact_acl aac1 WHERE art1.art_id = aac1.privilege_entity_id";
   public static final String GET_ALL_BRANCH_ACCESS_CONTROL_LIST =
      "SELECT bac1.*, art1.art_type_id FROM osee_artifact art1, osee_branch_acl bac1 WHERE art1.art_id = bac1.privilege_entity_id";

   public static final String DELETE_ARTIFACT_ACL_FROM_BRANCH = "DELETE FROM OSEE_ARTIFACT_ACL WHERE  branch_id =?";
   public static final String DELETE_BRANCH_ACL_FROM_BRANCH = "DELETE FROM OSEE_BRANCH_ACL WHERE branch_id =?";

   public static final String USER_GROUP_MEMBERS =
      "SELECT b_art_id FROM osee_relation_link WHERE a_art_id = ? AND rel_link_type_id = ? ORDER BY b_art_id";

   private AccessQueries() {
   }

}
