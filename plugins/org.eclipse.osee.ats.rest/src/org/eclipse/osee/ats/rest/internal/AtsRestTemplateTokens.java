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
package org.eclipse.osee.ats.rest.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;

/**
 * @author John Misinco
 */
public class AtsRestTemplateTokens {
   private static final List<ResourceToken> tokens = new ArrayList<ResourceToken>();

   // @formatter:off
   //html 92F3EF9156EE83E
   public static final ResourceToken AtsConvertHtml = createToken(0x49000000000001L, "AtsConvert.html");
   public static final ResourceToken AtsHeaderIncludeHtml = createToken(0x49000000000002L, "AtsHeaderInclude.html");
   public static final ResourceToken AtsValuesHtml = createToken(0x49000000000003L, "AtsValues.html");
   public static final ResourceToken SimplePageHtml = createToken(0x49000000000004L, "SimplePage.html");
   // @formatter:on

   private static ResourceToken createToken(Long uuid, String name) {
      ResourceToken token = new ClassBasedResourceToken(uuid, name, AtsRestTemplateTokens.class);
      tokens.add(token);
      return token;
   }

   public static void register(IResourceRegistry registry) {
      registry.registerAll(tokens);
   }

   private AtsRestTemplateTokens() {
      // Constants
   }
}
