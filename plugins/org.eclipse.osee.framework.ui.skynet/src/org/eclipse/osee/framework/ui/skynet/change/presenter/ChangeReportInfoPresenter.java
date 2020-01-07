/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change.presenter;

import java.text.DateFormat;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.view.EditorSection;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

public class ChangeReportInfoPresenter implements EditorSection.IWidget {

   private static final String LOADING = "<form><p vspace='false'><b>Loading ...</b></p></form>";
   private static final String NO_CHANGES_FOUND = "<b>No changes were found</b><br/>";
   public static final String lineEndStr = " - ";
   // private String lineEndStr = "<br/>";

   public static interface Display {
      void setImage(KeyedImage image);

      void setText(String value);

      void onCreate(IManagedForm managedForm, Composite parent);
   }

   private final Display display;
   private final ChangeUiData changeData;

   public ChangeReportInfoPresenter(Display display, ChangeUiData changeData) {
      this.display = display;
      this.changeData = changeData;
   }

   @Override
   public void onLoading() {
      display.setImage(FrameworkImage.DELTAS);
      display.setText(LOADING);
   }

   @Override
   public void onUpdate() {
      display.setImage(getKeyedImage());
      display.setText(createInfoPage());
   }

   private KeyedImage getKeyedImage() {
      KeyedImage imageKey = FrameworkImage.DELTAS;
      if (changeData.areBranchesValid()) {
         imageKey = changeData.getCompareType().getHandler().getScenarioImage(changeData);
      }
      return imageKey;
   }

   public String createInfoPage() {
      StringBuilder sb = new StringBuilder();
      sb.append("<form>");
      sb.append("<p>");
      if (!changeData.isLoaded()) {
         sb.append("<b>Cleared on shut down. Press refresh to reload</b><br/><br/>");
      }
      addInterpretation(sb);

      if (!changeData.areBranchesValid()) {
         sb.append("<br/><b>The branch has been updated from parent and cannot be refreshed.</b><br/>");
         sb.append("<b>Please close down and re-open this change report</b>");
      } else {
         sb.append("<br/>");
         if (changeData.isLoaded() && changeData.getChanges().isEmpty()) {
            sb.append(NO_CHANGES_FOUND);
            sb.append("<br/>");
         }
         sb.append("<br/>");
         addAssociated(sb);
         try {
            changeData.getCompareType().getHandler().appendTransactionInfoHtml(sb, changeData);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      sb.append("</p>");
      sb.append("</form>");
      return sb.toString();
   }

   private void addInterpretation(StringBuilder sb) {
      sb.append("<b>Description: </b> ");
      try {
         sb.append(changeData.getCompareType().getHandler().getScenarioDescriptionHtml(changeData));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         sb.append(changeData.getCompareType().getHandler().getActionDescription());
      }
      sb.append("");
   }

   private void addAssociated(StringBuilder sb) {
      String message;
      Artifact associatedArtifact = changeData.getAssociatedArtifact();
      if (associatedArtifact != null) {
         message = AXml.textToXml(associatedArtifact.getName());
      } else {
         message = "Unkown";
      }
      sb.append(String.format("<b>Associated With: </b> %s%s", message, lineEndStr));
   }

   public static void addTransactionInfo(StringBuilder sb, TransactionId tx) {
      TransactionRecord transaction = TransactionManager.getTransaction(tx);
      String author;
      try {
         User user = UserManager.getUserByArtId(transaction.getAuthor());
         author = user.toString();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         author = "Unknown";
      }
      DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
      sb.append(String.format("               <b>On: </b> %s%s", dateFormat.format(transaction.getTimeStamp()),
         ChangeReportInfoPresenter.lineEndStr));
      sb.append(String.format("               <b>By: </b> %s%s", AXml.textToXml(author),
         ChangeReportInfoPresenter.lineEndStr));
      sb.append(String.format("               <b>Comment: </b> %s", AXml.textToXml(transaction.getComment())));
   }

   @Override
   public void onCreate(IManagedForm managedForm, Composite parent) {
      display.onCreate(managedForm, parent);
   }
}
