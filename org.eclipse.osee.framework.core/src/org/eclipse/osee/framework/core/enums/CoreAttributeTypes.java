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
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.IOseeType;

/**
 * @author Roberto E. Escobar
 */
public enum CoreAttributeTypes implements IOseeType {
   FAVORITE_BRANCH("Favorite Branch", "AAMFEbMnzS7P92knZKAA"),
   NAME("Name", "AAMFEcF1AzV7PKuHmxwA"),
   NATIVE_CONTENT("Native Content", "AAMFEcdBJGBK9nr9TTQA"),
   NATIVE_EXTENSION("Extension", "AAMFEcUbJEERZTnwJzAA"),
   PARAGRAPH_NUMBER("Imported Paragraph Number", "AAMFEQ3boD3sp6VfArAA"),
   WHOLE_WORD_CONTENT("Whole Word Content", "AAMFEchZmAzZo2tHjVAA"),
   WORD_TEMPLATE_CONTENT("Word Template Content", "AAMFEcfcGS2V3SqQN2wA"),
   RELATION_ORDER("Relation Order", "ABM5kHa9cFsTbI_ooyQA"),
   SUBSYSTEM("Subsystem", "AAMFERJ1GweNukuSd8QA"),
   TEST_STATUS("Test Status", "AKTJWsjz3EjFV94XNHAA"),
   USER_ID("User Id", "AAMFEbKl8RCQr17bDAQA"),
   VERIFICATION_LEVEL("Verification Level", "AAMFEXRTkyKVIFqcMwQA");

   private final String name;
   private final String guid;

   private CoreAttributeTypes(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return name;
   }

   public String getGuid() {
      return guid;
   }
}