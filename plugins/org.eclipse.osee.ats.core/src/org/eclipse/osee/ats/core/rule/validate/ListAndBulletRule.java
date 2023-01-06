/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.core.rule.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Megumi Telles
 * @author Donald G. Dunne
 */
public class ListAndBulletRule extends AbstractValidationRule {

   private static final Pattern NORMAL_LIST_BULLET_STYLE = Pattern.compile("<w:pPr><w:listPr>.+?</w:listPr></w:pPr>");

   private final WorkType workType;

   private final String formattingInstructionUrl;

   public ListAndBulletRule(WorkType workType, AtsApi atsApi, String formattingInstructionUrl) {
      super(atsApi);
      this.workType = workType;
      this.formattingInstructionUrl = formattingInstructionUrl;
   }

   @Override
   public void validate(ArtifactToken artifact, XResultData rd) {
      String wtc =
         atsApi.getAttributeResolver().getSoleAttributeValue(artifact, CoreAttributeTypes.WordTemplateContent, "");

      Matcher match = NORMAL_LIST_BULLET_STYLE.matcher(wtc);
      if (match.find()) {
         String errStr = "is not using lists or bullets associated with an expected style.";
         logError(artifact, errStr, rd);
      }
   }

   @Override
   public String getRuleDescription() {
      return "Ensure lists and bullets use an expected style in the artifact(s)";
   }

   @Override
   public String getRuleTitle() {
      return String.format("Formatting Check for %s", workType);
   }
}