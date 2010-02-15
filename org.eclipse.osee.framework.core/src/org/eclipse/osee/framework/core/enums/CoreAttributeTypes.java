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

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Roberto E. Escobar
 */
public class CoreAttributeTypes extends NamedIdentity implements IAttributeType {
   public static final CoreAttributeTypes Active = new CoreAttributeTypes("AAMFEbImQyR38BY8A5QA", "Active");
   public static final CoreAttributeTypes Annotation = new CoreAttributeTypes("AAMFEcWy0xc4e3tcemQA", "Annotation");
   public static final CoreAttributeTypes EMAIL = new CoreAttributeTypes("AAMFEbaZjEVoecDFCCQA", "Email");
   public static final CoreAttributeTypes FAVORITE_BRANCH =
         new CoreAttributeTypes("AAMFEbMnzS7P92knZKAA", "Favorite Branch");
   public static final CoreAttributeTypes NAME = new CoreAttributeTypes("AAMFEcF1AzV7PKuHmxwA", "Name");
   public static final CoreAttributeTypes NATIVE_CONTENT =
         new CoreAttributeTypes("AAMFEcdBJGBK9nr9TTQA", "Native Content");
   public static final CoreAttributeTypes NATIVE_EXTENSION =
         new CoreAttributeTypes("AAMFEcUbJEERZTnwJzAA", "Extension");
   public static final CoreAttributeTypes PARAGRAPH_NUMBER =
         new CoreAttributeTypes("AAMFEQ3boD3sp6VfArAA", "Imported Paragraph Number");
   public static final CoreAttributeTypes PHONE = new CoreAttributeTypes("AAMFEbUkVSwKu4LSpWAA", "Phone");
   public static final CoreAttributeTypes QUALIFICATION_METHOD =
         new CoreAttributeTypes("AAMFERMRKHkM9k_Rg2QA", "Qualification Method");
   public static final CoreAttributeTypes WHOLE_WORD_CONTENT =
         new CoreAttributeTypes("AAMFEchZmAzZo2tHjVAA", "Whole Word Content");
   public static final CoreAttributeTypes WORD_TEMPLATE_CONTENT =
         new CoreAttributeTypes("AAMFEcfcGS2V3SqQN2wA", "Word Template Content");
   public static final CoreAttributeTypes RELATION_ORDER =
         new CoreAttributeTypes("ABM5kHa9cFsTbI_ooyQA", "Relation Order");
   public static final CoreAttributeTypes SUBSYSTEM = new CoreAttributeTypes("AAMFERJ1GweNukuSd8QA", "Subsystem");
   public static final CoreAttributeTypes TEST_STATUS = new CoreAttributeTypes("AKTJWsjz3EjFV94XNHAA", "Test Status");
   public static final CoreAttributeTypes TEST_PROCEDURE_STATUS =
         new CoreAttributeTypes("AKkUuN2K1ilSHnvIMPQA", "Test Procedure Status");
   public static final CoreAttributeTypes USER_ID = new CoreAttributeTypes("AAMFEbKl8RCQr17bDAQA", "User Id");
   public static final CoreAttributeTypes VERIFICATION_LEVEL =
         new CoreAttributeTypes("AAMFEXRTkyKVIFqcMwQA", "Verification Level");

   private CoreAttributeTypes(String guid, String name) {
      super(guid, name);
   }
}