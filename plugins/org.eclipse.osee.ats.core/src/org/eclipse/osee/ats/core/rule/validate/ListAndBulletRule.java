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
package org.eclipse.osee.ats.core.rule.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * @author Megumi Telles
 * @author Donald G. Dunne
 */
public class ListAndBulletRule extends AbstractValidationRule {

   private static final String MORE_ON_FORMATTING =
      "https://apache.msc.az.boeing.com/wiki/lba/index.php/OSEE/ATS/LBA/User_Guide#What_is_.22Validate_Requirement_Changes.22.2C_how_do_I_run_it_and_complete_review.3F";
   private static final Pattern NORMAL_LIST_BULLET_STYLE = Pattern.compile("<w:pPr><w:listPr>.+?</w:listPr></w:pPr>");

   private final WorkType workType;

   public ListAndBulletRule(WorkType workType, AtsApi atsApi) {
      super(atsApi);
      this.workType = workType;
   }

   @Override
   public void validate(ArtifactToken artifact, XResultData results) {
      String wtc =
         atsApi.getAttributeResolver().getSoleAttributeValue(artifact, CoreAttributeTypes.WordTemplateContent, "");

      Matcher match = NORMAL_LIST_BULLET_STYLE.matcher(wtc);
      if (match.find()) {
         results.errorf("%s is not using lists or bullets associated with an expected style.",
            artifact.toStringWithId());
      }
   }

   @Override
   public String getRuleDescription() {
      return "Ensure lists and bullets use an expected style (" + getHyperLink() + ")" + " in the artifact(s)";
   }

   private String getHyperLink() {
      return AHTML.getHyperlink("More on Formatting/Editing", MORE_ON_FORMATTING);
   }

   @Override
   public String getRuleTitle() {
      return String.format("Formatting Check for %s", workType);
   }
}