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

public class TTEDatabase {
   private final String TTE_CLASSROOMS_TABLE = "OSEE_TTE_classrooms";
   private final String TTE_COURSE_TABLE = "OSEE_TTE_course";
   private final String TTE_SESSION_TABLE = "OSEE_TTE_session";
   private final String TTE_USER_TABLE = "OSEE_TTE_user";
   private final String TTE_USERSESSIONS_TABLE = "OSEE_TTE_usersessions";
   private final String TTE_SESSION_COUNTER = "osee_tte_session_counter";
   private final String TTE_NUMBER_STUDENTS_PK = "osee_tte_pkg";

   private Connection connection;

   public TTEDatabase(Connection connection) {
      this.connection = connection;
   }

   public void dropAll() throws SQLException {
      dropTables();
      dropSequences();
      //      dropPackages();    
   }

   public void createAll() throws SQLException {
      createTables();
      createSequences();
      //      createPackages();
   }

   @SuppressWarnings("unused")
   private void createPackages() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE OR REPLACE package body osee_tte_pkg as" + "function number_of_students(" + "n_session_id    in osee_tte_session.session_id%type )" + "return number" + "is" + "type weak_rc is ref cursor;" + "session_rc weak_rc;" + "l_count    number;" + "begin" + "open session_rc for" + "select count(*) from osee_tte_usersessions" + "where session_id = n_session_id;" + "fetch session_rc into l_count;" + "if ( session_rc%notfound ) then" + "l_count := 0;" + "end if;" + "close session_rc;" + "return l_count;" + "end number_of_students;" + "end osee_tte_pkg;");
      statement.close();
   }

   public void dropTables() throws SQLException {

      Statement statement = connection.createStatement();
      try {
         statement.execute("DROP TABLE " + TTE_CLASSROOMS_TABLE + " purge");
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + TTE_COURSE_TABLE + " purge");
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + TTE_SESSION_TABLE + " purge");
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + TTE_USER_TABLE + " purge");
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      try {
         statement.execute("DROP TABLE " + TTE_USERSESSIONS_TABLE + " purge");
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      statement.close();
   }

   public void dropSequences() throws SQLException {
      Statement statement = connection.createStatement();
      try {
         statement.execute("DROP SEQUENCE " + TTE_SESSION_COUNTER);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      statement.close();
   }

   public void dropPackages() throws SQLException {
      Statement statement = connection.createStatement();
      try {
         statement.execute("DROP PACKAGE " + TTE_NUMBER_STUDENTS_PK);
      } catch (SQLException ex) {
         System.out.println(ex.getMessage());
      }
      statement.close();
   }

   public void createSequences() throws SQLException {
      createSequence(TTE_SESSION_COUNTER);
   }

   public void createTables() throws SQLException {
      createClassroomTable();
      createCourseTable();
      createUserTable();
      createUserSessionsTable();
      createSessionTable();
   }

   private void createUserSessionsTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_tte_usersessions" + " (user_id    VARCHAR2(25) NOT NULL," + "session_id VARCHAR2(25) NOT NULL)");
      statement.close();
   }

   private void createUserTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_tte_user " + "(user_id   VARCHAR2(25) NOT NULL," + "privilege NUMBER(1,0)  NOT NULL," + "CONSTRAINT tte_user_pk PRIMARY KEY  (user_id))");
      statement.close();
   }

   private void createCourseTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_tte_course " + "(course_number      VARCHAR2(25)   NOT NULL," + "course_name        VARCHAR2(100)  NOT NULL," + "course_description VARCHAR2(1500) NOT NULL," + "deleted            NUMBER(1,0)    NULL, " + "CONSTRAINT tte_course_pk PRIMARY KEY (course_number)," + "CONSTRAINT tte_name UNIQUE (course_name))");
      statement.close();
   }

   private void createSessionTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_tte_session " + "(course_number   VARCHAR2(25)  NOT NULL," + "date_of_session DATE          NOT NULL," + "location        VARCHAR2(25)  NOT NULL," + "number_of_seats NUMBER(3,0)   NOT NULL," + "instructor_id   VARCHAR2(25)  NOT NULL," + "status          NUMBER(1,0)   NOT NULL," + "verified        NUMBER(1,0)   NOT NULL," + "is_complete     NUMBER(1,0)   NOT NULL," + "start_time      VARCHAR2(100) NULL," + "session_id      NUMBER        NULL)");
      statement.close();
   }

   private void createClassroomTable() throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE TABLE osee_tte_classrooms " + "(room_id   VARCHAR2(25)  NOT NULL," + "max_size  NUMBER(3,0)   NOT NULL," + "computer  VARCHAR2(3)   NULL," + "projector VARCHAR2(3)   NULL," + "display   VARCHAR2(100) NULL," + "phone     VARCHAR2(15)  NULL, " + "CONSTRAINT tte_classrooms_pk PRIMARY KEY (room_id))");
      statement.close();
   }

   private void createSequence(String sequenceName) throws SQLException {
      Statement statement = connection.createStatement();

      statement.execute("CREATE SEQUENCE " + sequenceName + " MINVALUE 1" + " MAXVALUE 999999999999999999999999999" + " INCREMENT BY 1" + " NOCYCLE" + " NOORDER" + " NOCACHE");

      statement.close();
   }
}
