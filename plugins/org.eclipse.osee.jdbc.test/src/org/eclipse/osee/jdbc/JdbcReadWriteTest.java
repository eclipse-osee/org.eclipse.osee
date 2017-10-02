/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link JdbcClient, JdbcServer}
 *
 * @author Roberto E. Escobar
 */
public class JdbcReadWriteTest {

   private static final Object[][] DB_DATA = new Object[][] {
      {1, "The Odyssey", "Homer"},
      {2, "Pride and Prejudice", "Jane Austen"},
      {3, "Romeo and Juliet", "William Shakespeare"},
      {4, "The Great Gatsby", "F. Scott Fitzgerald"}};

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   private JdbcServer server;
   private int dbPort;
   private String dbName;
   private JdbcClient client;

   @Before
   public void setUp() throws IOException {
      MockitoAnnotations.initMocks(this);
      File newFile = folder.newFile("hsql.db.read.write.test");

      server = JdbcServerBuilder.hsql(newFile.toURI().toASCIIString())//
         .useRandomPort(true) //
         .build();

      JdbcServerConfig config = server.getConfig();
      dbName = config.getDbName();
      dbPort = config.getDbPort();

      server.start();

      client = JdbcClientBuilder.hsql(dbName, dbPort).build();
   }

   @After
   public void tearDown() {
      server.stop();
   }

   @Test
   public void testCreateReadAndWrite() {
      JdbcMigrationResource migrationResource = new JdbcMigrationResource() {

         @Override
         public boolean isApplicable(JdbcClientConfig config) {
            return true;
         }

         @Override
         public URL getLocation() {
            return getClass().getResource("migration");
         }

         @Override
         public void addPlaceholders(Map<String, String> placeholders) {
            // do nothing
         }
      };

      client.migrate(new JdbcMigrationOptions(true, true), Collections.singleton(migrationResource));
      client.runBatchUpdate("insert into books (id, title, author) values (?,?,?)", Arrays.asList(DB_DATA));

      List<Book> books = new ArrayList<>();
      client.runQuery(stmt -> books.add(new Book(stmt.getInt("id"), stmt.getString("title"), stmt.getString("author"))),
         "select * from books");
      assertEquals(4, books.size());

      Iterator<Book> iterator = books.iterator();
      assertBook(iterator.next(), 1, "The Odyssey", "Homer");
      assertBook(iterator.next(), 2, "Pride and Prejudice", "Jane Austen");
      assertBook(iterator.next(), 3, "Romeo and Juliet", "William Shakespeare");
      assertBook(iterator.next(), 4, "The Great Gatsby", "F. Scott Fitzgerald");

      assertEquals("William Shakespeare", client.fetch("", "select author from books where id = ?", 3));

      client.runPreparedUpdate("insert into books (id, title, author) values (?,?,?)", 5, "Dracula", "Bram Stoker");

      assertEquals("Dracula", client.fetch("", "select title from books where id = ?", 5));

      client.runTransaction(new JdbcTransaction() {

         @Override
         public void handleTxWork(JdbcConnection connection) {
            client.runPreparedUpdate(connection, "insert into books (id, title, author) values (?,?,?)", 6,
               "Lord of the Flies", "William Golding");
         }
      });
      assertEquals("Lord of the Flies", client.fetch("", "select title from books where id = ?", 6));

      Iterator<String[]> expectedRows = Arrays.asList(new String[][] {
         {"1", "The Odyssey", "Homer"},
         {"2", "Pride and Prejudice", "Jane Austen"},
         {"3", "Romeo and Juliet", "William Shakespeare"},
         {"4", "The Great Gatsby", "F. Scott Fitzgerald"},
         {"5", "Dracula", "Bram Stoker"},
         {"6", "Lord of the Flies", "William Golding"}}).iterator();

      client.runQuery(stmt -> {
         String[] expected = expectedRows.next();
         assertEquals(expected[0], stmt.getString("ID"));
         assertEquals(expected[1], stmt.getString("TITLE"));
         assertEquals(expected[2], stmt.getString("AUTHOR"));
      }, "select * from books");
   }

   private static void assertBook(Book book, int id, String title, String author) {
      assertEquals(id, book.getId());
      assertEquals(title, book.getTitle());
      assertEquals(author, book.getAuthor());
   }

   private static final class Book {
      private final int id;
      private final String title;
      private final String author;

      public Book(int id, String title, String author) {
         super();
         this.id = id;
         this.title = title;
         this.author = author;
      }

      public int getId() {
         return id;
      }

      public String getTitle() {
         return title;
      }

      public String getAuthor() {
         return author;
      }
   }

   public static Object[] row(Object... row) {
      return row;
   }

}
