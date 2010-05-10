/*
 * Created on May 5, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change.presenter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.view.EditorSection;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

public class ChangeReportInfoPresenter implements EditorSection.IWidget {

   private static final String LOADING = "<form><p vspace='false'><b>Loading ...</b></p></form>";
   private static final String NO_CHANGES_FOUND = "<b>No changes were found</b><br/>";

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

   public void onLoading() {
      display.setImage(FrameworkImage.DELTAS);
      display.setText(LOADING);
   }

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
            changeData.getCompareType().getHandler().appendTransactionInfo(sb, changeData);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      sb.append("</p>");
      sb.append("</form>");
      return sb.toString();
   }

   private void addInterpretation(StringBuilder sb) {
      sb.append("<b>Description: </b> ");
      try {
         sb.append(changeData.getCompareType().getHandler().getScenarioDescription(changeData));
      } catch (OseeCoreException ex) {
         sb.append(changeData.getCompareType().getHandler().getActionDescription());
      }
      sb.append("");
   }

   private void addAssociated(StringBuilder sb) {
      String message = "";
      Artifact associatedArtifact = changeData.getAssociatedArtifact();
      if (associatedArtifact != null) {
         message = associatedArtifact.getName();
      } else {
         message = "Unkown";
      }
      sb.append(String.format("<b>Associated With: </b> %s<br/>", message));
   }

   public static void addTransactionInfo(StringBuilder sb, TransactionRecord transaction) {
      String author = "Unknown";
      try {
         User user = UserManager.getUserByArtId(transaction.getAuthor());
         author = user.toString();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
      sb.append(String.format("               <b>On: </b> %s<br/>", dateFormat.format(transaction.getTimeStamp())));
      sb.append(String.format("               <b>By: </b> %s<br/>", author));
      sb.append(String.format("               <b>Comment: </b> %s", transaction.getComment()));
   }

   @Override
   public void onCreate(IManagedForm managedForm, Composite parent) {
      display.onCreate(managedForm, parent);
   }
}
