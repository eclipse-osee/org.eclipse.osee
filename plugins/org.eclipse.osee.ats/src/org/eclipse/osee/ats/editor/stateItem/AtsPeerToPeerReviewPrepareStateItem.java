/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.stateItem;

import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewState;
import org.eclipse.osee.ats.artifact.ReviewManager;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewPrepareStateItem extends AtsStateItem {

   public AtsPeerToPeerReviewPrepareStateItem() {
      super(AtsPeerToPeerReviewPrepareStateItem.class.getSimpleName());
   }

   @Override
   public String getDescription() {
      return "If stand-alone review, remove blocking review enablement and required entry.";
   }

   @Override
   public void xWidgetCreated(XWidget widget, FormToolkit toolkit, StateDefinition stateDefinition, Artifact art, XModifiedListener modListener, boolean isEditable) {
      try {
         if (art.isOfType(AtsArtifactTypes.PeerToPeerReview) && //
         stateDefinition.getPageName().equals(PeerToPeerReviewState.Prepare.getPageName()) && //
         ReviewManager.cast(art).getParentAWA() == null && //
         widget.getLabel().equals(AtsAttributeTypes.ReviewBlocks.getUnqualifiedName())) {
            XComboDam decisionComboDam = (XComboDam) widget;
            decisionComboDam.setEnabled(false);
            decisionComboDam.setRequiredEntry(false);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

}
