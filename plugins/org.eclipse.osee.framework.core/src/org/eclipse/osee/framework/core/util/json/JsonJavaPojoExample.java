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

import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;

/**
 * Example of taking simple java POJO objects, searializing to json, then desearializing back to POJO. Right-click run
 * as Java Application to see example.
 *
 * @author Donald G. Dunne
 */
public class JsonJavaPojoExample {

   public static void main(String[] args) {

      // Setup demo library to store
      Library library = new Library();
      library.addBook(new Book("Book 1", CoreBranches.COMMON));
      library.addBook(new Book("Book 2", CoreBranches.COMMON));
      library.addBook(new Book("Book 3", CoreBranches.COMMON));

      // This string can now be stored to filesystem or database
      String jsonToStore = JsonUtil.toJson(library);
      XConsoleLogger.out(jsonToStore + "\n\n");

      // Take string and turn back into a new Library object
      Library newLibrary = JsonUtil.readValue(jsonToStore, Library.class);
      XConsoleLogger.out(newLibrary + "\n\n");

      // You can now use java getter/setter methods to access data like any java classes
      for (Book book : newLibrary.getBooks()) {
         System.out.println(book);
      }
   }

}
