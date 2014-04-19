/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.template.engine;

import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;

/**
 * @author Ryan D. Brooks
 */
public final class OseeTemplateTestTokens {
   private static final ArrayList<ResourceToken> tokens = new ArrayList<ResourceToken>();

   // @formatter:off
   public static final ResourceToken TestValues_IncludeHtml = createToken(0x40000000000000F0L, "testValues_Include.html");
   public static final ResourceToken TestHeaderPageHtml = createToken(0x40000000000000F1L, "testHeaderPage.html");
   public static final ResourceToken TestValues_KeyValueHtml = createToken(0x40000000000000F2L, "testValues_KeyValue.html");
   public static final ResourceToken RealizePage_MainPageHtml = createToken(0x40000000000000F3L, "realizePage_MainPage.html");
   public static final ResourceToken Header_NoTokenOnFirstLineHtml = createToken(0x40000000000000F4L, "header_NoTokenOnFirstLine.html");
   public static final ResourceToken TestMainPage_WithIncludeFileHtml = createToken(0x40000000000000F5L, "testMainPage_WithIncludeFile.html");
   public static final ResourceToken HeaderHtml = createToken(0x40000000000000F6L, "header.html");
   public static final ResourceToken RealizePage_ValuesHtml = createToken(0x40000000000000F7L, "realizePage_Values.html");
   public static final ResourceToken MyTestCss = createToken(0x40000000000000F8L, "myTest.css");
   public static final ResourceToken RealizePage_ListItems = createToken(0x40000000000000F9L, "realizePage_ListItems.html");
   // @formatter:on

   private static ResourceToken createToken(Long uuid, String name) {
      ResourceToken token = new ClassBasedResourceToken(uuid, name, OseeTemplateTestTokens.class);
      tokens.add(token);
      return token;
   }

   public static void register(IResourceRegistry registry) {
      registry.registerAll(tokens);
   }

   private OseeTemplateTestTokens() {
      // Constants
   }
}