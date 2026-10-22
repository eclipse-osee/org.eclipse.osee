/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewOptions;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Decision widget for a Decision Review. Selectable values are read from the review's Decision Review Options (set in
 * the Prepare state), so no hook is needed to populate them.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXDecisionRevWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetIdAts.XXDecisionRevWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXDecisionRevWidget() {
      super(ID, AtsAttributeTypes.Decision.getUnqualifiedName());
      set(XOption.VAL_SINGLE_SEL);
      setAttributeType(AtsAttributeTypes.Decision);
      setAsProviderFor(AtsAttributeTypes.Decision);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<String> getSelectable() {
      if (getArtifact() != null && getArtifact().isOfType(AtsArtifactTypes.DecisionReview)) {
         DecisionReviewOptions options =
            new DecisionReviewOptions((IAtsDecisionReview) getArtifact(), AtsApiService.get());
         return Named.getNames(options.getDecisionOptions());
      }
      return Collections.emptyList();
   }

}
