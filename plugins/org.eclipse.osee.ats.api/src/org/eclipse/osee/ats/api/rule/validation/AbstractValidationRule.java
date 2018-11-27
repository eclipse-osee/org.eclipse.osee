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

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractValidationRule {

   protected final AtsApi atsApi;

   public AbstractValidationRule(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * @param artifact to validate as a collection
    */
   public void validateAll(Collection<ArtifactToken> artifacts, XResultData results) {
      // do nothing
   }

   /**
    * @param artifact to validate individually
    */
   public abstract void validate(ArtifactToken artifact, XResultData results);

   public abstract String getRuleDescription();

   public abstract String getRuleTitle();
}
