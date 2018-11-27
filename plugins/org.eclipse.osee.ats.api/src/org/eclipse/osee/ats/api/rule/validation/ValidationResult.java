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
package org.eclipse.osee.ats.api.rule.validation;

import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Shawn F. Cook
 */
public class ValidationResult {

   private XResultData results;

   public ValidationResult() {
      this.results = new XResultData();
   }

   public boolean didValidationPass() {
      return !results.isErrors();
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

}
