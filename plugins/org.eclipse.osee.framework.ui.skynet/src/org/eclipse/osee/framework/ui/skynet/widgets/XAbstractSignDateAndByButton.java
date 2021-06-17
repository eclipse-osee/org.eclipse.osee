/*********************************************************************
 * Copyright (c) 2009 Boeing
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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
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
 * XWidget that provides a label and button. Upon pressing button, user and date will be stored and displayed. Widget
 * must be extended to provide text and attrs to store date and user. Storage will be in storeArt. NOTE: User is stored
 * by User artifact id.
 *
 * @author Donald G. Dunne
 */
public abstract class XAbstractSignDateAndByButton extends XButtonWithLabelDam {

   public static final String NOT_YET_SIGNED = "Not Yet Signed";
   protected final AttributeTypeId signDateAttrType;
   protected final AttributeTypeId signByAttrType;
   protected boolean doSign = false;
   protected boolean required = false;

   public XAbstractSignDateAndByButton(String label, String toolTip, AttributeTypeId signDateAttrType, AttributeTypeId signByAttrType, KeyedImage keyedImage) {
      super(label, toolTip, ImageManager.getImage(keyedImage));
      this.signDateAttrType = signDateAttrType;
      this.signByAttrType = signByAttrType;
      addListener();
   }

   public XAbstractSignDateAndByButton(String labelReq, String toolTipReq, AttributeTypeId signDateAttrTypeReq, AttributeTypeId signByAttrUserReq, KeyedImage keyedImageReq, boolean required) {
      this(labelReq, toolTipReq, signDateAttrTypeReq, signByAttrUserReq, keyedImageReq);
      this.required = required;
   }

   protected void addListener() {
      addXModifiedListener(listener);
   }

   @Override
   public String getResultsText() {
      return getText(getArtifact(), signDateAttrType, signByAttrType);
   }

   public static String getText(Artifact artifact, AttributeTypeId signDateAttrType, AttributeTypeId signByAttrType) {
      Date date = artifact.getSoleAttributeValue(signDateAttrType, null);
      if (date != null) {
         User user =
            UserManager.getUserByArtId(artifact.getSoleAttributeValue(signByAttrType, SystemUser.UnAssigned.getId()));
         return String.format("signed by %s on %s", user.getName(), DateUtil.getDateNow(date, DateUtil.MMDDYYHHMM));
      }
      return NOT_YET_SIGNED;
   }

   private final XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         handleSelection();
      }
   };

   public void handleSelection() {
      try {
         // Ok --> 0, Cancel --> 1, Clear --> 2
         int res = MessageDialog.open(3, Displays.getActiveShell(), getLabel(), getSignMessage(), SWT.NONE,
            new String[] {"Ok", "Cancel", "Clear"});
         if (res == 1) {
            doSign = false;
            return;
         } else {
            doSign = true;
         }
         Job signJob = new Job(getSignMessage()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               if (res == 2) {
                  if (userHasPermission()) {
                     setSigned(artifact, signDateAttrType, signByAttrType, getLabel(), false);
                     refreshLabel();
                  }
               } else if (res == 0) {
                  if (userHasPermission()) {
                     setSigned(artifact, signDateAttrType, signByAttrType, getLabel(), true);
                     refreshLabel();
                  }
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
      return "Sign " + getLabel() + "?";
   }

   @Override
   public void refreshLabel() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            resultsLabelWidget.setText(getResultsText());
            resultsLabelWidget.getParent().layout(true);
            resultsLabelWidget.getParent().getParent().layout(true);
         }
      });
   }

   public boolean userHasPermission() {
      return true;
   }

   public static void setSigned(Artifact artifact, AttributeTypeId signDateAttrType, AttributeTypeId signByAttrType, String label, boolean signed) {
      setSigned(Collections.singleton(artifact), signDateAttrType, signByAttrType, label, signed);
   }

   public static void setSigned(Collection<Artifact> artifacts, AttributeTypeId signDateAttrType, AttributeTypeId signByAttrType, String label, boolean signed) {
      SkynetTransaction tx =
         TransactionManager.createTransaction(artifacts.iterator().next().getBranch(), "Set signed for " + label);
      for (Artifact art : artifacts) {
         if (signed) {
            art.setSoleAttributeValue(signByAttrType, UserManager.getUser().getId());
            art.setSoleAttributeValue(signDateAttrType, new Date());
         } else {
            art.deleteSoleAttribute(signByAttrType);
            art.deleteSoleAttribute(signDateAttrType);
         }
         tx.addArtifact(art);
      }
      tx.execute();
   }

   @Override
   public boolean isEditable() {
      return true;
   }

   public boolean doSign() {
      return doSign;
   }

   public AttributeTypeId getSignDateAttrType() {
      return signDateAttrType;
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry()) {
         Date date = getArtifact().getSoleAttributeValue(signDateAttrType, null);
         if (date == null) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be signed");
         }
      }
      return Status.OK_STATUS;
   }

   public void setUnsigned() {
      XAbstractSignDateAndByButton.setSigned(getArtifact(), signDateAttrType, signByAttrType, getLabel(), false);
   }

   public void setSigned() {
      XAbstractSignDateAndByButton.setSigned(getArtifact(), signDateAttrType, signByAttrType, getLabel(), true);
   }

   @Override
   public Label getControl() {
      return labelWidget;
   }

}
