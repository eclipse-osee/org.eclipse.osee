/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor.DataRightsProvider;

/**
 * @author John Misinco
 */
public class ArtifactDataRightsProvider implements DataRightsProvider {

   private static final String NEW_PAGE_TEMPLATE =
      "<w:p><w:pPr><w:spacing w:after=\"0\"/><w:sectPr>%s</w:sectPr></w:pPr></w:p>";
   private static final String SAME_PAGE_TEMPLATE = "<w:sectPr>%s</w:sectPr>";
   private static final String GENERIC_FOOTER =
      "<w:ftr w:type=\"odd\"><w:p><w:pPr><w:pStyle w:val=\"Footer\"/></w:pPr><w:r><w:t>%s</w:t></w:r></w:p></w:ftr>";
   private static final IArtifactToken MAPPING_ARTIFACT = TokenFactory.createArtifactToken("AOkJ_kFNbEXCS7UjmfwA",
      "DataRightsFooters", CoreArtifactTypes.GeneralData);

   private Map<String, String> dataRightsToFooters;

   @Override
   public String getDataClassificationFooter(String classification, FooterOption option) {
      if (dataRightsToFooters == null) {
         initialize();
      }

      String key = classification;
      String footer = null;
      if (!Strings.isValid(key)) {
         key = "DEFAULT";
      }

      footer = dataRightsToFooters.get(key);
      if (!Strings.isValid(footer)) {
         String text = String.format("FOOTER NOT DEFINED FOR [%s]", key);
         footer = String.format(GENERIC_FOOTER, text);
      }

      switch (option) {
         case NEW_PAGE:
            footer = String.format(NEW_PAGE_TEMPLATE, footer);
            break;
         case SAME_PAGE:
            footer = String.format(SAME_PAGE_TEMPLATE, footer);
            break;
         case FOOTER_ONLY:
         default:
            // do nothing, return footer only
      }

      return footer;
   }

   private void initialize() {
      dataRightsToFooters = new HashMap<String, String>();
      Artifact art =
         ArtifactQuery.getOrCreate(MAPPING_ARTIFACT.getGuid(), MAPPING_ARTIFACT.getArtifactType(), CoreBranches.COMMON);
      List<String> footers = art.getAttributesToStringList(CoreAttributeTypes.GeneralStringData);
      for (String footer : footers) {
         String[] enumToFooter = footer.split("\\n", 2);
         if (enumToFooter.length == 2) {
            dataRightsToFooters.put(enumToFooter[0].trim(), enumToFooter[1].trim());
         }
      }
   }

}
