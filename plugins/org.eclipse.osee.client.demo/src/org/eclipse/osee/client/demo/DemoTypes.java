/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.demo;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Roberto E. Escobar
 */
public final class DemoTypes implements OrcsTypeTokenProvider {
   private static final OrcsTypeTokens tokens = new OrcsTypeTokens();

   // @formatter:off
   public static final NamespaceToken DEMO = NamespaceToken.valueOf(15, "demo", "Namespace for demo system and content management types");

   public static final ArtifactTypeToken DemoTestRequirement = ArtifactTypeToken.valueOf(86, "Demo Artifact With Selective Partition");

   public static final ArtifactTypeToken DemoDslArtifact = ArtifactTypeToken.valueOf(204526342635554L, "Demo DSL Artifact");
   public static final AttributeTypeString DemoDslAttribute = tokens.add(AttributeTypeToken.createStringNoTag(1153126013769613777L, DEMO, "AxRbLlj+c2ZHjBeumfgA", MediaType.TEXT_PLAIN, ""));
   // @formatter:on

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokens.registerTypes(tokenService);
   }

   private DemoTypes() {
      // Constants
   }
}