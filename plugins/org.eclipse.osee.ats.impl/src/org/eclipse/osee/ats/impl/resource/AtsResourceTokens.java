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

package org.eclipse.osee.ats.impl.resource;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.template.engine.OseeTemplateTokens;

/**
 * @author Ryan D. Brooks
 */
public final class AtsResourceTokens {
   private static final List<ResourceToken> tokens = new ArrayList<ResourceToken>();

   // @formatter:off
   public static final ResourceToken AtsValuesHtml = createToken(0x400000000000000EL, "atsValues.html");
   public static final ResourceToken AtsActionHtml = createToken(0x400000000000000FL, "atsAction.html");
   public static final ResourceToken AtsHtml = createToken(0x4000000000000010L, "ats.html");
   public static final ResourceToken AtsNewActionHtml = createToken(0x4000000000000011L, "atsNewAction.html");
   public static final ResourceToken AtsNewActionValuesHtml = createToken(0x4000000000000012L, "atsNewActionValues.html");
   public static final ResourceToken AtsCoreCss = createToken(0x4000000000000013L, "atsCore.css");
   public static final ResourceToken TransitionHtml = createToken(0x4000000000000014L, "atsTransition.html");
   public static final ResourceToken AtsSearchHtml = createToken(0x4000000000000015L, "atsSearch.html");
   // @formatter:on

   private static ResourceToken createToken(Long uuid, String name) {
      ResourceToken token = new ClassBasedResourceToken(uuid, name, AtsResourceTokens.class, "html/");
      tokens.add(token);
      return token;
   }

   public static void register(IResourceRegistry registry) {
      OseeTemplateTokens.register(registry);
      registry.registerAll(tokens);
   }

   private AtsResourceTokens() {
      // Constants
   }
}