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

   // @formatter:off
   public static final CoreAttributeTypes Access_Context_Id = new CoreAttributeTypes("AAFAgR3B7AN_g0NPCLAA", "Access Context Id");
   public static final CoreAttributeTypes Active = new CoreAttributeTypes("AAMFEbImQyR38BY8A5QA", "Active");
   public static final CoreAttributeTypes Annotation = new CoreAttributeTypes("AAMFEcWy0xc4e3tcemQA", "Annotation");
   public static final CoreAttributeTypes ContentURL = new CoreAttributeTypes("AAMFEcIP+U+ML_gzH7AA", "Content URL");
   public static final CoreAttributeTypes DEFAULT_MAIL_SERVER = new CoreAttributeTypes("ABMuIC3FejpGilonfAgA", "osee.config.Default Mail Server");
   public static final CoreAttributeTypes EMAIL = new CoreAttributeTypes("AAMFEbaZjEVoecDFCCQA", "Email");
   public static final CoreAttributeTypes FAVORITE_BRANCH = new CoreAttributeTypes("AAMFEbMnzS7P92knZKAA", "Favorite Branch");
   public static final CoreAttributeTypes GENERAL_STRING_DATA = new CoreAttributeTypes("AAMFEca+MB5ssx+Ax5wA", "General String Data");
   public static final CoreAttributeTypes NAME = new CoreAttributeTypes("AAMFEcF1AzV7PKuHmxwA", "Name");
   public static final CoreAttributeTypes NATIVE_CONTENT = new CoreAttributeTypes("AAMFEcdBJGBK9nr9TTQA", "Native Content");
   public static final CoreAttributeTypes NATIVE_EXTENSION = new CoreAttributeTypes("AAMFEcUbJEERZTnwJzAA", "Extension");
   public static final CoreAttributeTypes PARAGRAPH_NUMBER = new CoreAttributeTypes("AAMFEQ3boD3sp6VfArAA", "Imported Paragraph Number");
   public static final CoreAttributeTypes PHONE = new CoreAttributeTypes("AAMFEbUkVSwKu4LSpWAA", "Phone");
   public static final CoreAttributeTypes QUALIFICATION_METHOD = new CoreAttributeTypes("AAMFERMRKHkM9k_Rg2QA", "Qualification Method");
   public static final CoreAttributeTypes WHOLE_WORD_CONTENT = new CoreAttributeTypes("AAMFEchZmAzZo2tHjVAA", "Whole Word Content");
   public static final CoreAttributeTypes WORD_TEMPLATE_CONTENT = new CoreAttributeTypes("AAMFEcfcGS2V3SqQN2wA", "Word Template Content");
   public static final CoreAttributeTypes WORD_OLE_DATA = new CoreAttributeTypes("AAMFEcP2rmoCzqmzJxQA", "Word Ole Data");
   public static final CoreAttributeTypes RELATION_ORDER = new CoreAttributeTypes("ABM5kHa9cFsTbI_ooyQA", "Relation Order");
   public static final CoreAttributeTypes SUBSYSTEM =   new CoreAttributeTypes("AAMFERJ1GweNukuSd8QA", "Subsystem");
   public static final CoreAttributeTypes TEST_STATUS = new CoreAttributeTypes("AKTJWsjz3EjFV94XNHAA", "Test Status");
   public static final CoreAttributeTypes TEST_PROCEDURE_STATUS = new CoreAttributeTypes("AKkUuN2K1ilSHnvIMPQA", "Test Procedure Status");
   public static final CoreAttributeTypes USER_ID = new CoreAttributeTypes("AAMFEbKl8RCQr17bDAQA", "User Id");
   public static final CoreAttributeTypes VERIFICATION_LEVEL = new CoreAttributeTypes("AAMFEXRTkyKVIFqcMwQA", "Verification Level");
   public static final CoreAttributeTypes STATIC_ID = new CoreAttributeTypes("AAMFEcY5DUbWyuIpZVwA", "Static Id");
   public static final CoreAttributeTypes PARTITION = new CoreAttributeTypes("AAMFERHj9w6pmoLBCaQA", "Partition");
   public static final CoreAttributeTypes SAFETY_CRITICALITY = new CoreAttributeTypes("AAMFERg99R51HIz45HAA", "Safety Criticality");
   public static final CoreAttributeTypes Description = new CoreAttributeTypes("AAMFEcK+kyOhG8GYvPgA", "Description");
   // @formatter:on

   private CoreAttributeTypes(String guid, String name) {
      super(guid, name);
   }
}