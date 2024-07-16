/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.workflow.hooks;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewPrepareWorkItemHookIde implements IAtsWorkItemHookIde {

   public String getName() {
      return AtsPeerToPeerReviewPrepareWorkItemHookIde.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "If stand-alone review, remove blocking review enablement and required entry.";
   }

   @Override
   public void xWidgetCreated(XWidget widget, FormToolkit toolkit, StateDefinition stateDefinition, Artifact art, boolean isEditable) {
      try {
         if (art.isOfType(AtsArtifactTypes.PeerToPeerReview) && //
            stateDefinition.getName().equals(PeerToPeerReviewState.Prepare.getName()) && //
            ReviewManager.cast(art).getParentAWA() == null && //
            widget.getLabel().equals(AtsAttributeTypes.ReviewBlocks.getUnqualifiedName())) {
            XComboDam decisionComboDam = (XComboDam) widget;
            decisionComboDam.setEnabled(false);
            decisionComboDam.setRequiredEntry(false);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

}
