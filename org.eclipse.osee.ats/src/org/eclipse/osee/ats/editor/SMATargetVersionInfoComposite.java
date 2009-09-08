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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.ReadOnlyHyperlinkListener;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FontManager;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class SMATargetVersionInfoComposite extends Composite {

   private final SMAManager smaMgr;
   private Hyperlink link;
   private Label label;
   private final IManagedForm managedForm;

   public SMATargetVersionInfoComposite(final SMAManager smaMgr, Composite parent, IManagedForm managedForm, XFormToolkit toolkit) throws OseeCoreException {
      super(parent, SWT.NONE);
      this.smaMgr = smaMgr;
      this.managedForm = managedForm;
      setLayout(ALayout.getZeroMarginLayout(2, false));
      setLayoutData(new GridData());

      label = toolkit.createLabel(this, "Target Version: ", SWT.NONE);
      SMAEditor.setLabelFonts(label, FontManager.getDefaultLabelFont());

      if (!smaMgr.isReleased()) {
         link = toolkit.createHyperlink(this, "", SWT.NONE);
         if (smaMgr.getSma().isReadOnly())
            link.addHyperlinkListener(new ReadOnlyHyperlinkListener(smaMgr));
         else
            link.addHyperlinkListener(new IHyperlinkListener() {

               public void linkEntered(HyperlinkEvent e) {
               }

               public void linkExited(HyperlinkEvent e) {
               }

               public void linkActivated(HyperlinkEvent e) {
                  try {
                     if (smaMgr.promptChangeVersion(
                           AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased, false)) {
                        refresh();
                     }
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
      } else {
         label = toolkit.createLabel(this, "", SWT.NONE);
      }
      refresh();
   }

   public void clearFormMessages() {
      if (Widgets.isAccessible(managedForm.getForm())) {
         managedForm.getMessageManager().removeMessage("validation.error", label);
      }
   }

   public void refresh() throws OseeCoreException {
      String str = "";
      // Don't transition without targeted version if so configured
      boolean required =
            smaMgr.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name()) || smaMgr.getWorkPageDefinition().hasWorkRule(
                  AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());

      if (smaMgr.getTargetedForVersion() != null) {
         str = smaMgr.getTargetedForVersion() + "";
      } else {
         str = "<edit>";
         if (required) {
            IMessageManager messageManager = managedForm.getMessageManager();
            if (messageManager != null) {
               messageManager.addMessage("validation.error", "Workflow must be targeted for a version.", null,
                     IMessageProvider.ERROR, label != null ? label : link);
            }
         }
      }
      if (link != null && !link.isDisposed()) {
         link.setText(str);
      } else if (label != null && !label.isDisposed()) {
         label.setText(str);
      }
   }

   @Override
   public String toString() {
      try {
         return "SMATargetVersionInfoComposite for SMA \"" + smaMgr.getSma() + "\"";
      } catch (Exception ex) {
         return "SMATargetVersionInfoComposite " + ex.getLocalizedMessage();
      }
   }

   public String toHTML() throws OseeCoreException {
      return "";
   }

}
