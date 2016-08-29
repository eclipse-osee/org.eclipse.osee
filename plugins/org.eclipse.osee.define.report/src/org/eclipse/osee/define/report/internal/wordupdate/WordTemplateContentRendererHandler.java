/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal.wordupdate;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.define.report.api.ReportConstants;
import org.eclipse.osee.define.report.api.WordTemplateContentData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Morgan E. Cook
 */
public class WordTemplateContentRendererHandler {

   public static final String PGNUMTYPE_START_1 = "<w:pgNumType [^>]*w:start=\"1\"/>";
   private OrcsApi orcsApi;

   public WordTemplateContentRendererHandler(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public Pair<String, Set<String>> renderWordML(WordTemplateContentData wtcData) {
      ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(wtcData.getBranchId()).andUuid(
         wtcData.getArtId()).includeDeletedArtifacts().includeDeletedAttributes().getResults().getAtMostOneOrNull();

      if (artifact != null) {
         Set<String> unknownGuids = new HashSet<>();

         String data = artifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, DeletionFlag.INCLUDE_DELETED, null);

         if (data == null && wtcData.getIsEdit()) {
            data = orcsApi.getOrcsTypes().getAttributeTypes().getDefaultValue(CoreAttributeTypes.WordTemplateContent);
         }

         if (data != null) {
            //Change the BinData Id so images do not get overridden by the other images
            data = WordUtilities.reassignBinDataID(data);

            LinkType link = wtcData.getLinkType() != null ? LinkType.valueOf(wtcData.getLinkType()) : null;

            data = WordMlLinkHandler.link(orcsApi.getQueryFactory(), link, artifact, data, wtcData.getTxId(),
               wtcData.getSessionId(), wtcData.getSessionId(), unknownGuids);
            data = WordUtilities.reassignBookMarkID(data);

            // remove any existing footers and replace with the current one
            // first try to remove footer for extra paragraphs
            if (wtcData.getIsEdit()) {
               data = data.replaceAll(ReportConstants.ENTIRE_FTR_EXTRA_PARA, "");
            }

            // if no extra paragraphs have been added this will replace the normal footer
            data = data.replaceAll(ReportConstants.ENTIRE_FTR, "");
            data = data.replaceAll(ReportConstants.NO_DATA_RIGHTS, "");

            if (wtcData.getIsEdit() && !data.contains("<w:tbl>")) {
               int lastIndex = data.lastIndexOf("<w:p wsp:rsidR=");

               if (lastIndex != -1) {
                  // temp should equal <w:p wsp:rsidR ..</w:p> ...
                  String temp = data.substring(lastIndex);
                  temp = temp.replaceAll("<w:p wsp:rsidR=\".*?\" wsp:rsidRDefault=\".*?\"></w:p>", "");
                  data = data.substring(0, lastIndex) + temp;
               }
            }

            data = data.concat(wtcData.getFooter());

            if (!wtcData.getIsEdit()) {
               data = data.replaceAll(PGNUMTYPE_START_1, "");
            }

            return new Pair<String, Set<String>>(data, unknownGuids);
         }
      }

      return null;
   }
}
