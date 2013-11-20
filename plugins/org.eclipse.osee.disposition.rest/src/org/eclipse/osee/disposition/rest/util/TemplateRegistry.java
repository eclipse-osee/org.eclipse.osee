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
package org.eclipse.osee.disposition.rest.util;

import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.template.engine.OseeTemplateTokens;

/**
 * @author Angel Avila
 */
public final class TemplateRegistry {
   private static final ArrayList<ResourceToken> tokens = new ArrayList<ResourceToken>();

   // @formatter:off
   public static final ResourceToken DispositionHtml = createToken(0x4000000000000FFL, "disposition.html");
   // @formatter:on

   private static ResourceToken createToken(Long uuid, String name) {
      ResourceToken token = new ClassBasedResourceToken(uuid, name, TemplateRegistry.class, "html/");
      tokens.add(token);
      return token;
   }

   public static IResourceRegistry newRegistry() {
      IResourceRegistry registry = new ResourceRegistry();
      return addTokens(registry);
   }

   public static IResourceRegistry addTokens(IResourceRegistry registry) {
      OseeTemplateTokens.register(registry);
      registry.registerAll(tokens);
      return registry;
   }
}