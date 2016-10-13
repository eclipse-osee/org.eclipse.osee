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
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.OseeTypeDefinition;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.framework.core.dsl.OseeDslResourceUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.AttributeModifier;

public class OseeTypeModifier implements AttributeModifier {

   @Override
   public InputStream modifyForSave(Artifact owner, File file) throws OseeCoreException {
      String value = null;
      try {
         value = Lib.fileToString(file);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(OseeTypeDefinition, COMMON);
      StringBuilder combinedSheets = new StringBuilder();
      for (Artifact art : artifacts) {
         String sheetData;
         if (art.equals(owner)) {
            sheetData = value;
         } else {
            sheetData = art.getSoleAttributeValueAsString(CoreAttributeTypes.UriGeneralStringData, "");
         }
         combinedSheets.append(sheetData.replaceAll("import\\s+\"", "// import \""));
      }
      OseeDsl oseeDsl = null;
      try {
         oseeDsl = OseeDslResourceUtil.loadModel("osee:/TypeModel.osee", combinedSheets.toString()).getModel();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      Set<Long> uuids = new HashSet<>();

      if (oseeDsl != null) {
         for (EObject object : oseeDsl.eContents()) {
            if (object instanceof OseeType) {
               addUuid(uuids, (OseeType) object);
            }
         }
      }

      Conditions.checkExpressionFailOnTrue(uuids.contains(0L), "Uuid of 0L is not allowed");

      InputStream inputStream = null;
      try {
         inputStream = Lib.stringToInputStream(value);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return inputStream;
   }

   private void addUuid(Set<Long> set, OseeType type) throws OseeCoreException {
      Long uuid = Long.valueOf(type.getId());
      boolean wasAdded = set.add(uuid);
      Conditions.checkExpressionFailOnTrue(!wasAdded, "Duplicate uuid found: [0x%X]", uuid);
   }
}