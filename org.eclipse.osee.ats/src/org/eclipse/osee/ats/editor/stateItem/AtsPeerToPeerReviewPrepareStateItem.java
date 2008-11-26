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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboBooleanDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewPrepareStateItem extends AtsStateItem {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   @Override
   public String getId() {
      return "osee.ats.peerToPeerReview.Prepare";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.AtsStateItem#xWidgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget,
    *      org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.ats.workflow.AtsWorkPage,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact,
    *      org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void xWidgetCreated(XWidget widget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.xWidgetCreated(widget, toolkit, page, art, modListener, isEditable);
      try {
         if ((art instanceof ReviewSMArtifact) && ((ReviewSMArtifact) art).getParentSMA() == null) {
            if (widget.getLabel().equals(ATSAttributes.BLOCKING_REVIEW_ATTRIBUTE.getDisplayName())) {
               XComboBooleanDam decisionComboDam = (XComboBooleanDam) widget;
               decisionComboDam.setEnabled(false);
               decisionComboDam.setRequiredEntry(false);
            } else if (widget.getLabel().equals(ATSAttributes.REVIEW_BLOCKS_ATTRIBUTE.getDisplayName())) {
               XComboDam decisionComboDam = (XComboDam) widget;
               decisionComboDam.setEnabled(false);
               decisionComboDam.setRequiredEntry(false);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() throws OseeCoreException {
      return "AtsPeerToPeerReviewPrepareStateItem - If stand-alone review, remove blocking review enablement and required entry.";
   }

}
