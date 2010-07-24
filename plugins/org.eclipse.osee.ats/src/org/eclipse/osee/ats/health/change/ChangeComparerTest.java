/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.health.change;

/**
 * @author Jeff C. Phillips
 */
public class ChangeComparerTest {

   public static void main(String[] args) {
      String content = "<artId>12535</artId>";
      System.out.println(Integer.parseInt(content.substring(content.indexOf("<artId>") + 7, content.indexOf("</artId>"))));
   }

}
