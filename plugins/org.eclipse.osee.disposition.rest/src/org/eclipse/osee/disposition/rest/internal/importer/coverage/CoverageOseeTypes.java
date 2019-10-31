/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.importer.coverage;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.disposition.rest.DispoOseeTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Angel Avila
 */
public final class CoverageOseeTypes implements OrcsTypeTokenProvider {
   private static final OrcsTypeTokens tokens = new OrcsTypeTokens();

   // @formatter:off
   public static final ArtifactTypeToken CoveragePackage = ArtifactTypeToken.valueOf(75L, "Coverage Package");
   public static final ArtifactTypeToken CoverageUnit = ArtifactTypeToken.valueOf(78L, "Coverage Unit");
   public static final ArtifactTypeToken CoverageFolder = ArtifactTypeToken.valueOf(77L, "Coverage Folder");

   // Attributes
   public static final AttributeTypeString Assignees = tokens.add(AttributeTypeToken.createString(1152921504606847233L, DispoOseeTypes.DISPO, "coverage.Assignees", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString CoverageItem = tokens.add(AttributeTypeToken.createString(1152921504606847236L, DispoOseeTypes.DISPO, "coverage.Coverage Item", MediaType.TEXT_XML, ""));
   public static final AttributeTypeString CoverageOptions = tokens.add(AttributeTypeToken.createString(1152921504606847229L, DispoOseeTypes.DISPO, "coverage.Coverage Options", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString FileContents = tokens.add(AttributeTypeToken.createString(1152921504606847230L, DispoOseeTypes.DISPO, "coverage.File Contents", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Location = tokens.add(AttributeTypeToken.createString(1152921504606847235L, DispoOseeTypes.DISPO, "coverage.Location", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Namespace = tokens.add(AttributeTypeToken.createString(1152921504606847237L, DispoOseeTypes.DISPO, "coverage.Namespace", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Notes = tokens.add(AttributeTypeToken.createString(1152921504606847228L, DispoOseeTypes.DISPO, "coverage.Notes", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString Order = tokens.add(AttributeTypeToken.createString(1152921504606847234L, DispoOseeTypes.DISPO, "coverage.Order", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString UnitTestTable = tokens.add(AttributeTypeToken.createString(1152921504606847867L, DispoOseeTypes.DISPO, "coverage.UnitTestTable", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkProductPcrGuid = tokens.add(AttributeTypeToken.createString(1152921504606847232L, DispoOseeTypes.DISPO, "coverage.WorkProductPcrGuid", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString WorkProductTaskGuid = tokens.add(AttributeTypeToken.createString(1152921504606847231L, DispoOseeTypes.DISPO, "coverage.WorkProductTaskGuid", MediaType.TEXT_PLAIN, ""));
   //@formatter:on

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokens.registerTypes(tokenService);
   }
}
