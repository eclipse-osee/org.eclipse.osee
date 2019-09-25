/*
 * Created on July 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

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

/**
 * XWidget that provides a label and button. Upon pressing button, user and date will be stored and displayed. Widget
 * must be extended to provide text and attrs to store date and user. Storage will be in storeArt. NOTE: User is stored
 * by User artifact id.
 *
 * @author Donald G. Dunne
 */
public abstract class XAbstractSignDateAndByButton extends XButtonWithLabelDam {

   private final AttributeTypeId signDateAttrType;
   private final AttributeTypeId signByAttrType;
   private boolean isRequiredButton = false;

   public XAbstractSignDateAndByButton(String label, String toolTip, AttributeTypeId signDateAttrType, AttributeTypeId signByAttrUser, KeyedImage keyedImage) {
      super(label, toolTip, ImageManager.getImage(keyedImage));
      this.signDateAttrType = signDateAttrType;
      this.signByAttrType = signByAttrUser;
      addListener();
   }

   public XAbstractSignDateAndByButton(String labelReq, String toolTipReq, AttributeTypeId signDateAttrTypeReq, AttributeTypeId signByAttrUserReq, KeyedImage keyedImageReq, boolean isRequired) {
      this(labelReq, toolTipReq, signDateAttrTypeReq, signByAttrUserReq, keyedImageReq);
      this.isRequiredButton = isRequired;
   }

   protected void addListener() {
      addXModifiedListener(listener);
   }

   @Override
   protected String getResultsText() {
      Date date = getArtifact().getSoleAttributeValue(signDateAttrType, null);
      if (date != null) {
         User user = UserManager.getUserByArtId(
            getArtifact().getSoleAttributeValue(signByAttrType, SystemUser.UnAssigned.getId()));
         labelWidget.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         return String.format("signed by %s on %s", user.getName(), DateUtil.getDateNow(date, DateUtil.MMDDYYHHMM));
      }
      if (this.isRequiredButton) {
         resultsLabelWidget.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      } else {
         resultsLabelWidget.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
      }
      return "Not Yet Signed";
   }

   private final XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         handleSelection();
      }
   };

   protected void handleSelection() {
      try {
         if (!MessageDialog.openConfirm(Displays.getActiveShell(), getLabel(), getSignMessage())) {
            return;
         }
         Job signJob = new Job(getSignMessage()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               setSigned();
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
   protected void refreshLabel() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            resultsLabelWidget.setText(getResultsText());
            resultsLabelWidget.getParent().layout(true);
            resultsLabelWidget.getParent().getParent().layout(true);
         }
      });
   }

   protected void setSigned() {
      SkynetTransaction tx =
         TransactionManager.createTransaction(getArtifact().getBranch(), "Set signed for " + getLabel());
      Artifact storeArt = getArtifact();
      storeArt.setSoleAttributeValue(signByAttrType, UserManager.getUser().getId());
      storeArt.setSoleAttributeValue(signDateAttrType, new Date());
      tx.addArtifact(storeArt);
      tx.execute();
      refreshLabel();
   }

   @Override
   public boolean isEditable() {
      return true;
   }

}
