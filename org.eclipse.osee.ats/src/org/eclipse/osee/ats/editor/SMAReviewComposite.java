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
package org.eclipse.osee.ats.editor;

import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.editor.SMAManager.TransitionOption;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SMAReviewComposite extends Composite {

   private final SMAManager smaMgr;
   private final String forStateName;

   public SMAReviewComposite(final SMAManager smaMgr, Composite parent, XFormToolkit toolkit, String forStateName) throws OseeCoreException {
      super(parent, SWT.NONE);
      this.smaMgr = smaMgr;
      this.forStateName = forStateName;
      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      Label label = new Label(parent, SWT.NONE);
      label.setText("\"" + smaMgr.getStateMgr().getCurrentStateName() + "\" State Reviews: ");
      // If ATS Admin, allow right-click to auto-complete reviews
      if (AtsPlugin.isAtsAdmin() && !AtsPlugin.isProductionDb()) {
         label.addListener(SWT.MouseUp, new Listener() {
            /* (non-Javadoc)
                         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
                         */
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Auto Complete Reviews",
                        "ATS Admin\n\nAuto Complete Reviews?")) {
                     return;
                  }
                  try {
                     SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
                     for (ReviewSMArtifact revArt : smaMgr.getReviewManager().getReviewsFromCurrentState()) {
                        if (!revArt.getSmaMgr().isCancelledOrCompleted()) {
                           revArt.getSmaMgr().transitionToCompleted("", transaction,
                                 TransitionOption.OverrideTransitionValidityCheck, TransitionOption.Persist);
                        }
                     }
                     transaction.execute();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      }

      Composite workComp = toolkit.createContainer(parent, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      GridData gd = new GridData();
      gd.horizontalIndent = 20;
      workComp.setLayoutData(gd);

      // workComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
      for (ReviewSMArtifact revArt : smaMgr.getReviewManager().getReviews(forStateName)) {
         createReviewHyperlink(workComp, toolkit, 2, revArt);
      }

   }

   @Override
   public String toString() {
      try {
         return "SMAReviewComposite for SMA \"" + smaMgr.getSma() + "\"";
      } catch (Exception ex) {
         return "SMAReviewComposite " + ex.getLocalizedMessage();
      }
   }

   public void disposeReviewComposite() {
   }

   public static String toHTML(final SMAManager smaMgr, String forStateName) throws OseeCoreException {
      if (smaMgr.getReviewManager().getReviews(forStateName).size() == 0) return "";
      StringBuffer html = new StringBuffer();
      try {
         html.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT,
               "\"" + smaMgr.getStateMgr().getCurrentStateName() + "\" State Reviews"));
         html.append(AHTML.startBorderTable(100, Overview.normalColor, ""));
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Review Type", "Title", "ID"}));
         for (ReviewSMArtifact art : smaMgr.getReviewManager().getReviews(forStateName)) {
            html.append(AHTML.addRowMultiColumnTable(new String[] {art.getArtifactTypeName(), art.getDescriptiveName(),
                  art.getHumanReadableId()}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Review Exception - " + ex.getLocalizedMessage();
      }
      return html.toString();
   }

   public String toHTML() throws OseeCoreException {
      return SMAReviewComposite.toHTML(smaMgr, forStateName);
   }

   private static void createReviewHyperlink(Composite comp, XFormToolkit toolkit, final int horizontalSpan, final ReviewSMArtifact revArt) throws OseeCoreException {

      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
      workComp.setLayout(ALayout.getZeroMarginLayout(3, false));

      Label imageLabel = new Label(workComp, SWT.NONE);
      Label strLabel = new Label(workComp, SWT.NONE);
      if (revArt.isBlocking() && !revArt.getSmaMgr().isCancelledOrCompleted()) {
         imageLabel.setImage(ArtifactAnnotation.Type.Error.getImage());
         strLabel.setText("Blocking [" + revArt.getArtifactTypeName() + "] must be completed: ");
         strLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      } else if (!revArt.getSmaMgr().isCancelledOrCompleted()) {
         imageLabel.setImage(ArtifactAnnotation.Type.Warning.getImage());
         strLabel.setText("Open [" + revArt.getArtifactTypeName() + "] exists: ");
      } else {
         strLabel.setText(revArt.getSmaMgr().getStateMgr().getCurrentStateName() + " [" + revArt.getArtifactTypeName() + "] exists: ");
      }

      String str = "[" + revArt.getDescriptiveName() + "]";
      Hyperlink hyperLabel =
            toolkit.createHyperlink(workComp, ((str.length() > 300) ? Strings.truncate(str, 300) + "..." : str),
                  SWT.NONE);
      hyperLabel.setToolTipText("select to open review");
      hyperLabel.addListener(SWT.MouseUp, new Listener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         public void handleEvent(Event event) {
            SMAEditor.editArtifact(revArt);
         }
      });
   }

}
