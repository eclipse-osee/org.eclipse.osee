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

import static org.eclipse.osee.disposition.rest.DispoTypeTokenProvider.dispo;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralData;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;

/**
 * @author Angel Avila
 */
public interface CoverageOseeTypes {

   // @formatter:off
   // Attribute Types
   AttributeTypeString Assignees = dispo.createString(1152921504606847233L, "coverage.Assignees", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CoverageItem = dispo.createString(1152921504606847236L, "coverage.Coverage Item", MediaType.TEXT_XML, "");
   AttributeTypeString CoverageOptions = dispo.createString(1152921504606847229L, "coverage.Coverage Options", MediaType.TEXT_PLAIN, "");
   AttributeTypeString FileContents = dispo.createString(1152921504606847230L, "coverage.File Contents", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Location = dispo.createString(1152921504606847235L, "coverage.Location", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Namespace = dispo.createString(1152921504606847237L, "coverage.Namespace", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Notes = dispo.createString(1152921504606847228L, "coverage.Notes", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Order = dispo.createString(1152921504606847234L, "coverage.Order", MediaType.TEXT_PLAIN, "");
   AttributeTypeString UnitTestTable = dispo.createString(1152921504606847867L, "coverage.UnitTestTable", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkProductPcrGuid = dispo.createString(1152921504606847232L, "coverage.WorkProductPcrGuid", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WorkProductTaskGuid = dispo.createString(1152921504606847231L, "coverage.WorkProductTaskGuid", MediaType.TEXT_PLAIN, "");

   //Artifact Types
   ArtifactTypeToken AbstractCoverageUnit = dispo.add(dispo.artifactType(76L, "Abstract Coverage Unit", true, GeneralData)
      .zeroOrOne(Assignees, "")
      .any(CoverageItem, "")
      .zeroOrOne(FileContents, "")
      .zeroOrOne(Location, "")
      .zeroOrOne(Namespace, "")
      .zeroOrOne(Notes, "")
      .zeroOrOne(Order, ""));
   ArtifactTypeToken CoverageFolder = dispo.add(dispo.artifactType(77L, "Coverage Folder", false, AbstractCoverageUnit));
   ArtifactTypeToken CoveragePackage = dispo.add(dispo.artifactType(75L, "Coverage Package", false, GeneralData)
      .exactlyOne(Active, "true")
      .zeroOrOne(CoverageOptions, "")
      .zeroOrOne(UnitTestTable, "")
      .any(WorkProductPcrGuid, ""));
   ArtifactTypeToken CoverageUnit = dispo.add(dispo.artifactType(78L, "Coverage Unit", false, AbstractCoverageUnit)
      .zeroOrOne(WorkProductTaskGuid, ""));
   //@formatter:on

}