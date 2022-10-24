/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.define.rest.importing.parsers;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUtilities;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Readers;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David W. Miller
 */
public class WordTemplateAttributeUpdater {

   public XResultData replaceAttribute(OrcsApi orcsApi, BranchId branch, XResultData results, String source, ArtifactId artToUpdate) {
      ArtifactReadable art = orcsApi.getQueryFactory().fromBranch(branch).andId(artToUpdate).asArtifact();
      AttributeId attrId = art.getSoleAttributeId(CoreAttributeTypes.WordTemplateContent);
      if (art.isValid()) {
         String newWordTemplateContent = parseContentFromFile(source, results);
         if (results.isErrors()) {
            return results;
         } else {
            TransactionBuilder transaction =
               orcsApi.getTransactionFactory().createTransaction(branch, "Replace word graphic");
            transaction.setAttributeById(art, attrId, newWordTemplateContent);
            transaction.commit();
         }

      } else {
         results.errorf("Invalid artifact id provided: %s", artToUpdate);
      }
      return results;
   }

   private String parseContentFromFile(String source, XResultData results) {
      StringBuilder content = new StringBuilder(2000);
      try (BufferedReader reader = Files.newBufferedReader(Paths.get(source))) {

         if (Readers.forward(reader, WordUtilities.BODY_START) == null) {
            results.errorf("no start of body tag for file %s", source.toString());
         }
         Readers.xmlForward(reader, content, "w:body");

      } catch (Exception ex) {
         results.errorf("exception in xml reader: %s", ex.toString());
         return "";
      }
      return content.substring(0, content.length() - 9);
   }
}