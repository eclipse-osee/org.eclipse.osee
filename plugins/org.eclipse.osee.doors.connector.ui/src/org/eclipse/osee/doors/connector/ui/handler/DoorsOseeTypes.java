/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.ui.handler;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * Class to create Doors Artifact type
 *
 * @author Chandan Bandemutt
 */
public final class DoorsOseeTypes implements OrcsTypeTokenProvider {
   private static final OrcsTypeTokens tokens = new OrcsTypeTokens();

   /**
    * Doors Artifact type
    */

   // @formatter:off
   public static final NamespaceToken DOORS = NamespaceToken.valueOf(7, "doors", "Namespace for doors system and content management types");

   public static final ArtifactTypeToken DoorsRequirement = ArtifactTypeToken.valueOf(5764607523034243073L, "Doors Requirement");

   public static final AttributeTypeString DoorReqName = tokens.add(AttributeTypeToken.createString(5764607523034243075L, DOORS, "Door Req Name", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DoorReqUrl = tokens.add(AttributeTypeToken.createString(8198L, DOORS, "Door Req URL", MediaType.TEXT_PLAIN, ""));
   // @formatter:on

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokens.registerTypes(tokenService);
   }

}
