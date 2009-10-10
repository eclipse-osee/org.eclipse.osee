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
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.skynet.core.IOseeType;

/**
 * @author Roberto E. Escobar
 */
public enum CoreAttributes implements IOseeType {
   FAVORITE_BRANCH("Favorite Branch", "AAMFEbMnzS7P92knZKAA"),
   NAME("Name", "AAMFEcF1AzV7PKuHmxwA"),
   NATIVE_CONTENT("Native Content", "AAMFEcdBJGBK9nr9TTQA"),
   NATIVE_EXTENSION("Extension", "AAMFEcUbJEERZTnwJzAA"),
   WHOLE_WORD_CONTENT("Whole Word Content", "AAMFEchZmAzZo2tHjVAA"),
   WORD_TEMPLATE_CONTENT("Word Template Content", "AAMFEcfcGS2V3SqQN2wA"),
   RELATION_ORDER("Relation Order", "ABM5kHa9cFsTbI_ooyQA"),
   USER_ID("User Id", "AAMFEbKl8RCQr17bDAQA");

   private final String name;
   private final String guid;

   private CoreAttributes(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}