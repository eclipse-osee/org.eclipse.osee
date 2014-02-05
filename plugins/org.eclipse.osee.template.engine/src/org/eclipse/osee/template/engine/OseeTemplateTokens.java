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
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public final class OseeTemplateTokens {
   private static final List<ResourceToken> tokens = new ArrayList<ResourceToken>();

   // @formatter:off
   
   //html
   public static final ResourceToken PageDeclarationHtml = createToken(0x4000000000000000L, "pageDeclaration.html");
   public static final ResourceToken ExceptionHtml = createToken(0x4000000000000001L, "exception.html");
   //js
   public static final ResourceToken OseeCoreJs = createToken(0x4000000000000002L, "oseeCore.js");
   // word xml
   public static final ResourceToken WordXml = createToken(0x4100000000000003L, "WordDocument.xml");
   public static final ResourceToken WordDeclarationXml = createToken(0x4100000000000004L, "WordDeclaration.xml");
   public static final ResourceToken FontsXml = createToken(0x4100000000000005L, "WordFonts.xml");
   public static final ResourceToken ListsXml = createToken(0x4100000000000006L, "WordLists.xml");
   public static final ResourceToken StylesXml = createToken(0x4100000000000007L, "WordStyles.xml");
   public static final ResourceToken DivsXml = createToken(0x4100000000000008L, "WordDivs.xml");
   public static final ResourceToken ShapesXml = createToken(0x4100000000000009L, "WordShape.xml");
   public static final ResourceToken DocPropXml = createToken(0x410000000000000AL, "WordDocPr.xml");
   
   // @formatter:on

   private static ResourceToken createToken(Long uuid, String name) {
      ResourceToken token =
         new ClassBasedResourceToken(uuid, name, OseeTemplateTokens.class, Lib.getExtension(name) + "/");
      tokens.add(token);
      return token;
   }

   public static void register(IResourceRegistry registry) {
      registry.registerAll(tokens);
   }

   private OseeTemplateTokens() {
      // Constants
   }
}