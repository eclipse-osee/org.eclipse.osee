/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

/**
 * XWidget that provides a label and button. Upon pressing button, users will be stored and displayed. Widget must be
 * extended to provide text and attrs to store users. Storage will be in storeArt. NOTE: User is stored by User artifact
 * id.
 *
 * @author Vaibhav Patel
 */
public abstract class XAbstractSignOffByButton extends XButtonWithLabelDam {

   protected final AttributeTypeId signByAttrType;

   public XAbstractSignOffByButton(String label, String toolTip, AttributeTypeId signByAttrType, KeyedImage keyedImage) {
      super(label, toolTip, ImageManager.getImage(keyedImage));
      this.signByAttrType = signByAttrType;
      addListener();
   }

   protected void addListener() {
      addXModifiedListener(listener);
   }

   @Override
   public String getResultsText() {
      return getText(getArtifact(), signByAttrType, true);
   }

   public static String getText(Artifact artifact, AttributeTypeId signByAttrType, boolean inline) {
      List<Long> userArtIds = artifact.getAttributeValues(signByAttrType);
      String signedBy = "";
      for (Long userArtId : userArtIds) {
         User user = UserManager.getUserByArtId(userArtId);
         if (user != null) {
            signedBy = signedBy + user.getName() + (inline ? "; " : "\n");
         }
      }
      return inline ? Strings.truncate(signedBy, 50, true) : signedBy;
   }

   private final XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         handleSelection();
      }
   };

   public void displaySignedUsersDialog() {
      int opt = MessageDialog.open(3, Displays.getActiveShell(), getLabel(),
         getText(getArtifact(), signByAttrType, false), SWT.NONE, new String[] {"Ok"});
      if (opt == 0) {
         return;
      }
   }

   public void handleSelection() {
      try {
         int res = MessageDialog.open(3, Displays.getActiveShell(), "Review Checklist Sign Off", getSignMessage(),
            SWT.NONE, new String[] {"Sign", "Un-sign", "View Signers", "Cancel"});
         if (res == 3) {
            return;
         } else if (res == 2) {
            displaySignedUsersDialog();
         }

         Job signJob = new Job(getSignMessage()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               if (res == 0) {
                  setSigned(artifact, signByAttrType, getLabel(), true);
                  refresh();
               } else if (res == 1) {
                  setSigned(artifact, signByAttrType, getLabel(), false);
                  refresh();
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(signJob, false, Job.SHORT, null);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   };

   protected String getSignMessage() {
      return "Please review checklist document before signing off.";
   }

   public static void setSigned(Artifact artifact, AttributeTypeId signByAttrType, String label, boolean signed) {
      SkynetTransaction tx = TransactionManager.createTransaction(artifact.getBranch(),
         (signed ? "Set signed " : "Set un-signed ") + label);
      List<Long> userArtIds = artifact.getAttributeValues(signByAttrType);
      if (signed) {
         userArtIds.add(UserManager.getUser().getId());
      } else {
         userArtIds.remove(UserManager.getUser().getId());
      }
      artifact.setAttributeFromValues(signByAttrType, userArtIds);
      tx.addArtifact(artifact);
      tx.execute();
   }

   public void setSigned() {
      XAbstractSignOffByButton.setSigned(getArtifact(), signByAttrType, getLabel(), true);
   }

   public void setUnsigned() {
      XAbstractSignOffByButton.setSigned(getArtifact(), signByAttrType, getLabel(), false);
   }

   @Override
   public boolean isEditable() {
      return true;
   }

   @Override
   public Label getControl() {
      return labelWidget;
   }

}
