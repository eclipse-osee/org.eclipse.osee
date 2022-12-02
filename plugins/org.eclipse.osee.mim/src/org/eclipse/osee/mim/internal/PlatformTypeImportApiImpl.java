/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim.internal;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookReader;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookWriter.WorkbookFormat;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimImportApi;
import org.eclipse.osee.mim.types.InterfaceEnumOrdinalType;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.mim.types.PlatformTypeImportToken;

/**
 * @author Ryan T. Baldwin
 */
public class PlatformTypeImportApiImpl implements MimImportApi {

   private final ExcelWorkbookReader reader;
   private final MimApi mimApi;
   private MimImportSummary summary;

   private Long id = 1L;

   public PlatformTypeImportApiImpl(InputStream inputStream, MimApi mimApi) {
      this.reader = new ExcelWorkbookReader(inputStream, WorkbookFormat.XLSX);
      this.mimApi = mimApi;
   }

   @Override
   public MimImportSummary getSummary() {
      summary = new MimImportSummary();

      reader.setActiveSheet(0);

      Map<String, PlatformTypeImportToken> platformTypes = new HashMap<>();
      List<String> usedNames = new LinkedList<>();

      int rowIndex = 1;
      while (reader.rowExists(rowIndex)) {
         String name = reader.getCellStringValue(rowIndex, 1);
         int bitSize = (int) reader.getCellNumericValue(rowIndex, 2);
         String logicalType = reader.getCellStringValue(rowIndex, 3);
         String minVal = reader.getCellStringValue(rowIndex, 4);
         String maxVal = reader.getCellStringValue(rowIndex, 5);
         String defaultVal = reader.getCellStringValue(rowIndex, 6);
         String units = reader.getCellStringValue(rowIndex, 7);
         String validRange = reader.getCellStringValue(rowIndex, 14);
         String description = reader.getCellStringValue(rowIndex, 15);
         String suggestedName = reader.getCellStringValue(rowIndex, 16);

         if (logicalType.equals("boolean")) {
            // Skip booleans. If one does not exist it should be created manually, or one will be created during an ICD import if needed.
         } else if (logicalType.equals("enumeration")) {
            PlatformTypeImportToken pType = new PlatformTypeImportToken(id, name, logicalType, bitSize + "", minVal,
               maxVal, units, description, defaultVal, validRange);
            incrementId();
            summary.getPlatformTypes().add(pType);

            InterfaceEnumerationSet enumSet = new InterfaceEnumerationSet(id, name);
            enumSet.setDescription(description);
            enumSet.setApplicability(ApplicabilityToken.BASE);
            incrementId();
            summary.getEnumSets().add(enumSet);
            summary.getPlatformTypeEnumSetRelations().put(pType.getIdString(),
               new LinkedList<>(Arrays.asList(enumSet.getIdString())));

            String[] enumerations = description.split("\n");
            for (String enumeration : enumerations) {
               String[] split = enumeration.split(" = ");
               if (enumeration.isEmpty() || split.length < 2) {
                  continue;
               }
               InterfaceEnumeration enumToken = new InterfaceEnumeration(id, split[1]);
               long ordinal = split[0].matches("^0x.*") ? Long.decode(split[0].trim()) : Long.parseLong(split[0]);
               InterfaceEnumOrdinalType ordinalType =
                  split[0].matches("^0x.*") ? InterfaceEnumOrdinalType.HEX : InterfaceEnumOrdinalType.LONG;
               enumToken.setOrdinal(ordinal);
               enumToken.setApplicability(ApplicabilityToken.BASE);
               enumToken.setOrdinalType(ordinalType);
               incrementId();
               summary.getEnums().add(enumToken);
               List<String> rels =
                  summary.getEnumSetEnumRelations().getOrDefault(enumSet.getIdString(), new LinkedList<>());
               rels.add(enumToken.getIdString());
               summary.getEnumSetEnumRelations().put(enumSet.getIdString(), rels);
            }
         } else {
            String[] range = validRange.replace("---", "").replace("--", "").split(" - ");
            if (range.length == 1) {
               range = range[0].split(" to ");
            }
            if (range.length == 2) {
               validRange = range[0] + " to " + range[1];
            } else {
               validRange = range[0].trim();
            }

            String ident = mimApi.getInterfacePlatformTypeApi().getUniqueIdentifier(logicalType, minVal, maxVal,
               validRange, units, bitSize);
            if (!suggestedName.isEmpty() && !platformTypes.containsKey(ident)) {
               if (usedNames.contains(suggestedName)) {
                  suggestedName += "_" + id;
               }
               PlatformTypeImportToken pType = new PlatformTypeImportToken(id, suggestedName, logicalType, bitSize + "",
                  minVal, maxVal, units, description, defaultVal, validRange);
               incrementId();
               summary.getPlatformTypes().add(pType);
               platformTypes.put(ident, pType);
               usedNames.add(suggestedName);
            }
         }

         rowIndex++;
      }

      return summary;
   }

   private void incrementId() {
      id++;
   }

}
