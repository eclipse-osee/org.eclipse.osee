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

package org.eclipse.osee.define.report;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.template.engine.OseeTemplateTokens;

/**
 * @author Ryan D. Brooks
 */
public final class OseeDefineResourceTokens {
   private static final List<ResourceToken> tokens = new ArrayList<ResourceToken>();

   // @formatter:off

   public static final ResourceToken SystemSafetyReportHtml =  createToken(0x45AFE00000000001L, "systemSafetyReport.html");
   public static final ResourceToken SRSTraceReportHtml =      createToken(0x45AFE00000000002L, "SRSTraceReport.html");
   public static final ResourceToken RequirementReportHtml =   createToken(0x45AFE00000000003L, "requirementReport.html");

   // @formatter:on

   private static ResourceToken createToken(Long uuid, String name) {
      ResourceToken token = new ClassBasedResourceToken(uuid, name, OseeDefineResourceTokens.class, "html/");
      tokens.add(token);
      return token;
   }

   public static void register(IResourceRegistry registry) {
      OseeTemplateTokens.register(registry);
      registry.registerAll(tokens);
   }

   private OseeDefineResourceTokens() {
      // Constants
   }
}