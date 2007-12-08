/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.util.db.schemas;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SecurityDatabase {
   private final String SC_GROUP_TABLE = "OSEE_SC_group";
   private final String SC_PERMISSION_TABLE = "OSEE_SC_permission";
   private final String SC_PERSON_TABLE = "OSEE_SC_person";
   private final String SC_POLICY_TABLE = "OSEE_SC_policy";
   private final String SC_POLICY_TO_POLICY_TABLE = "OSEE_SC_policy_to_policy";
   private final String SC_POLICY_ROLE_TABLE = "OSEE_SC_role";
   private final String POLICY_SEQ = "osee_sc_policy_seq";
   private final String PERMISSION_SEQ = "osee_sc_permission_seq";
   private final String GROUP_SEQ = "osee_sc_group_seq";

   private Connection connection;

   public SecurityDatabase(Connection connection) {
      this.connection = connection;
   }

   public void dropAll() throws SQLException {
      dropTables();
      dropSequences();
   }

   public void createAll() throws SQLException {
      createTables();
      createSequences();
   }

   public void dropTables() throws SQLException {

      Statement statement = connection.createStatement();
      try {
         statement.execute("DROP TABLE " + SC_GROUP_TABLE);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + SC_PERMISSION_TABLE);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + SC_PERSON_TABLE);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + SC_POLICY_TABLE);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + SC_POLICY_TO_POLICY_TABLE);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + SC_POLICY_ROLE_TABLE);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      statement.close();
   }

   public void dropSequences() throws SQLException {
      Statement statement = connection.createStatement();
      try {
         statement.execute("DROP SEQUENCE " + POLICY_SEQ);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP SEQUENCE " + PERMISSION_SEQ);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP SEQUENCE " + GROUP_SEQ);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      statement.close();
   }

   public void createSequences() throws SQLException {
      createSequence(POLICY_SEQ);
      createSequence(PERMISSION_SEQ);
      createSequence(GROUP_SEQ);
   }

   public void createTables() throws SQLException {
      createPermissionTable();
      createPersonTable();
      createPolicyTable();
      createPolicyToPolicyTable();
      createRoleTable();
      createGroupTable();
   }

   private void createGroupTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_sc_group " + "(group_id          NUMBER        NULL," + "NAME             VARCHAR2(100) NULL," + "group_description VARCHAR2(200) NULL)");
      statement.close();

   }

   private void createPolicyToPolicyTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_sc_policy_to_policy " + "(parent_id NUMBER NULL,child_id  NUMBER NULL)");
      statement.close();
   }

   private void createPolicyTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_sc_policy " + "(role_id       NUMBER NULL," + "permission_id NUMBER NULL)");
      statement.close();
   }

   private void createPersonTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_sc_person " + "(bems      NUMBER NULL," + "policy_id NUMBER NULL)");
      statement.close();
   }

   private void createRoleTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_sc_role " + "(ROLE        VARCHAR2(50)  NULL," + "role_id     NUMBER        NOT NULL," + "description VARCHAR2(200) NULL," + "isartifact  NUMBER(1,0)   NULL," + "group_id    NUMBER        NULL," + "CONSTRAINT sc_role_pk PRIMARY KEY (role_id))");
      statement.close();
   }

   private void createPermissionTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_sc_permission " + "(permission    VARCHAR2(50) NULL," + "permission_id NUMBER       NOT NULL," + "CONSTRAINT sc_permission_pk PRIMARY KEY (permission_id))");
      statement.close();
   }

   private void createSequence(String sequenceName) throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE SEQUENCE " + sequenceName + " MINVALUE 11" + " MAXVALUE 999999999999999999999999999" + " INCREMENT BY 1" + " NOCYCLE" + " NOORDER" + " NOCACHE");

      statement.close();
   }

   public void resetArtifactPermission() throws SQLException {
      Statement statement = connection.createStatement();
      statement.execute("Delete from " + SC_POLICY_ROLE_TABLE + " where isartifact = 1");
      statement.close();
   }

}
