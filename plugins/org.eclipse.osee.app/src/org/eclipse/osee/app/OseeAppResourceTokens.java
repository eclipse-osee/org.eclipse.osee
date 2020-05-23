/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.app;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.template.engine.OseeTemplateTokens;

/**
 * @author Ryan D. Brooks
 */
public final class OseeAppResourceTokens {
   private static final List<ResourceToken> tokens = new ArrayList<>();

   // @formatter:off
   public static final ResourceToken OseeAppHtml = createToken(0x405EE00000000003L, "oseeApplet.html");
   public static final ResourceToken OseeAppCss = createToken(0x405EE00000000005L, "oseeApplet.css");
   public static final ResourceToken OseeAppJs = createToken(0x405EE00000000006L, "oseeApplet.js");
   public static final ResourceToken RefreshCacheValuesHtml = createToken(0x405EE00000000007L, "refreshCacheValues.html");
   public static final ResourceToken RefreshCacheWidgetsHtml = createToken(0x405EE00000000008L, "refreshCacheWidgets.html");
   // @formatter:on

   private static ResourceToken createToken(Long uuid, String name) {
      ResourceToken token = new ClassBasedResourceToken(uuid, name, OseeAppResourceTokens.class, "html/");
      tokens.add(token);
      return token;
   }

   public static void register(IResourceRegistry registry) {
      OseeTemplateTokens.register(registry);
      registry.registerAll(tokens);
   }

   private OseeAppResourceTokens() {
      // Constants
   }
}