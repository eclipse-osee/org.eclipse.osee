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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test Case for {@link JdbcClient, JdbcServer}
 * 
 * @author Roberto E. Escobar
 */
public class JdbcReadWriteTest {

   private static final String SCHEMA_DEF = //
      "<TableConfig>\n" + //
      "<Table name=\"BOOKS\" schema=\"TEST\" tablespace=\"TEST_DATA\">\n" + //
      "<Column id=\"ID\" defaultValue=\"not null\" type=\"INTEGER\" />\n" + //
      "<Column id=\"TITLE\" defaultValue=\"not null\" limits=\"22\" type=\"VARCHAR\" />\n" + //
      "<Column id=\"AUTHOR\" defaultValue=\"not null\" limits=\"22\" type=\"VARCHAR\" />\n" + //
      "<Constraint schema=\"TEST\" id=\"TEST_BOOKS__I_PK\" type=\"PRIMARY KEY\" appliesTo=\"id\" />\n" + //
      "</Table>\n" + //
      "</TableConfig>";

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

   @Before
   public void setUp() throws IOException {
      File newFile = folder.newFile("hsql.db.read.write.test");

      server = JdbcServerBuilder.hsql(newFile.toURI().toASCIIString())//
      .useRandomPort(true) //
      .build();

      JdbcServerConfig config = server.getConfig();
      dbName = config.getDbName();
      dbPort = config.getDbPort();

      server.start();
   }

   @After
   public void tearDown() {
      server.stop();
   }

   @Test
   public void testCreateReadAndWrite() {
      final JdbcClient client = JdbcClientBuilder.hsql(dbName, dbPort).build();
      client.initSchema(JdbcSchemaOptions.defaultOptions(), newSchema(SCHEMA_DEF));
      client.runBatchUpdate("insert into books (id, title, author) values (?,?,?)", Arrays.asList(DB_DATA));

      List<Book> books = new ArrayList<Book>();
      client.runQuery(newBookProcessor(books), "select * from books");
      assertEquals(4, books.size());

      Iterator<Book> iterator = books.iterator();
      assertBook(iterator.next(), 1, "The Odyssey", "Homer");
      assertBook(iterator.next(), 2, "Pride and Prejudice", "Jane Austen");
      assertBook(iterator.next(), 3, "Romeo and Juliet", "William Shakespeare");
      assertBook(iterator.next(), 4, "The Great Gatsby", "F. Scott Fitzgerald");

      assertEquals("William Shakespeare",
         client.runPreparedQueryFetchObject("", "select author from books where id = ?", 3));

      client.runPreparedUpdate("insert into books (id, title, author) values (?,?,?)", 5, "Dracula", "Bram Stoker");

      assertEquals("Dracula", client.runPreparedQueryFetchObject("", "select title from books where id = ?", 5));

      client.runTransaction(new JdbcTransaction() {

         @Override
         public void handleTxWork(JdbcConnection connection) {
            client.runPreparedUpdate(connection, "insert into books (id, title, author) values (?,?,?)", 6,
               "Lord of the Flies", "William Golding");
         }
      });
      assertEquals("Lord of the Flies",
         client.runPreparedQueryFetchObject("", "select title from books where id = ?", 6));

      Iterator<IVariantData> it = client.runQuery("select * from books").iterator();
      assertVariant(it.next(), "1", "The Odyssey", "Homer");
      assertVariant(it.next(), "2", "Pride and Prejudice", "Jane Austen");
      assertVariant(it.next(), "3", "Romeo and Juliet", "William Shakespeare");
      assertVariant(it.next(), "4", "The Great Gatsby", "F. Scott Fitzgerald");
      assertVariant(it.next(), "5", "Dracula", "Bram Stoker");
      assertVariant(it.next(), "6", "Lord of the Flies", "William Golding");
   }

   private static JdbcProcessor newBookProcessor(final List<Book> books) {
      return new JdbcProcessor() {

         @Override
         public void processNext(JdbcStatement chStmt) {
            books.add(new Book(chStmt.getInt("id"), chStmt.getString("title"), chStmt.getString("author")));
         }
      };
   }

   private static void assertVariant(IVariantData data, String id, String title, String author) {
      assertEquals(id, data.get("ID"));
      assertEquals(title, data.get("TITLE"));
      assertEquals(author, data.get("AUTHOR"));
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

   private static JdbcSchemaResource newSchema(final String schemaDef) {
      return new JdbcSchemaResource() {

         @Override
         public boolean isApplicable(JdbcClientConfig config) {
            return true;
         }

         @Override
         public InputStream getContent() {
            try {
               return new ByteArrayInputStream(schemaDef.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
               throw new OseeCoreException(ex);
            }
         }

         @Override
         public URI getLocation() {
            return null;
         }

      };
   }
}
