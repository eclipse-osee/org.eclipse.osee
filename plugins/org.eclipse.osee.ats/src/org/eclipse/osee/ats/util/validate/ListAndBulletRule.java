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
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Megumi Telles
 */
public class ListAndBulletRule extends AbstractValidationRule {

   private static final String MORE_ON_FORMATTING =
      "https://apache.msc.az.boeing.com/wiki/lba/index.php/OSEE/ATS/LBA/User_Guide#What_is_.22Validate_Requirement_Changes.22.2C_how_do_I_run_it_and_complete_review.3F";
   private static final Pattern NORMAL_LIST_BULLET_STYLE = Pattern.compile("<w:pPr><w:listPr>.+?</w:listPr></w:pPr>");

   private final WorkType workType;

   public ListAndBulletRule(WorkType workType) {
      this.workType = workType;
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor)  {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;
      String wtc = artToValidate.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, "");

      Matcher match = NORMAL_LIST_BULLET_STYLE.matcher(wtc);
      if (match.find()) {
         errorMessages.add(ValidationReportOperation.getRequirementHyperlink(
            artToValidate) + " (" + artToValidate.getGammaId() + ")" + " is not using lists or bullets associated with an expected style." + " See " + getHyperLink());
         validationPassed = false;
      }

      return new ValidationResult(errorMessages, validationPassed);
   }

   @Override
   public String getRuleDescription() {
      return "<b>Formatting Check: </b>" + "Ensure lists and bullets use an expected style (" + getHyperLink() + ")" + " in the artifact(s)";
   }

   private String getHyperLink() {
      return XResultDataUI.getHyperlinkUrlInternal("More on Formatting/Editing", MORE_ON_FORMATTING);
   }

   @Override
   public String getRuleTitle() {
      return String.format("Formatting Check for :", workType);
   }
}