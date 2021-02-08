/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class Library {

   List<Book> books = new ArrayList<Book>();

   public List<Book> getBooks() {
      return books;
   }

   public void setBooks(List<Book> Books) {
      this.books = Books;
   }

   public void addBook(Book book) {
      this.books.add(book);
   }

   @Override
   public String toString() {
      return "Books [Books=" + books + "]";
   }

}
