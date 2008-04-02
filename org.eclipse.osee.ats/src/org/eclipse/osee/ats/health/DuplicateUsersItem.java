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
package org.eclipse.osee.ats.health;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.xpath.XPathExpressionException;
import lba.framework.ui.skynet.util.MsaUser;
import lba.framework.ui.skynet.util.MsaUserDb;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.skynet.core.EveryoneGroup;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactStaticIdSearch;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.AttributeContentProvider;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAutoRunAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;

/**
 * @author Donald G. Dunne
 */
public class DuplicateUsersItem extends XNavigateItemAutoRunAction implements IAutoRunTask {
   BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   AccessControlManager accessControlManager = AccessControlManager.getInstance();
   ArtifactPersistenceManager artifactPersistenceManager = ArtifactPersistenceManager.getInstance();
   AttributeContentProvider myAttributeContentProvider = new AttributeContentProvider(Arrays.asList(new String[] {}));
   public static String OSEE_AUTORUN_USER_RELATIONS_CHECKED = "osee.autorun.userRelationsChecked";

   /**
    * @param parent
    */
   public DuplicateUsersItem(XNavigateItem parent) {
      super(parent, "Report Duplicate Users in DB");
   }

   public DuplicateUsersItem() {
      this(null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            XResultData rd = new XResultData(AtsPlugin.getLogger());
            runIt(monitor, rd);
            rd.report(getName());
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private void runIt(IProgressMonitor monitor, XResultData myXResultData) throws Exception {
      MsaUserDb myMsaUserDb = MsaUserDb.getInstance();
      Document myReportDocument = Jaxp.newDocument();
      Element parentDivElement = myReportDocument.createElement("div");
      myReportDocument.appendChild(parentDivElement);
      Xml.appendNewElementWithTextAndAttributes(parentDivElement, "style",
            "td ,th,caption {font-family: arial;font-size: 75%;} td.red {font-family: arial;color:red}",
            new String[][] {{"type", "text/css"}});
      Element[] duplicatesElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "Duplicate User IDs", new String[][] {
                  {"HRID1", "33"}, {"UserName1", "100"}, {"HRID2", "33"}, {"UserName2", "100"}});
      Element[] phoneWarningsElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "Phone Warnings", new String[][] {
                  {"UserName", "70"}, {"HRID", "33"}, {"Phone", "100"}});
      Element[] activeSetCorrectlyElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "Active Set Incorrectly", new String[][] {
                  {"UserName", "70"}, {"HRID", "33"}, {"Active Via 'User.isActive()'", "33"},
                  {"Active Via MsaUserDb", "33"}});
      Element[] usersNotInMsaUserDbElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "Users Not In MsaUserDb", new String[][] {
                  {"UserName", "70"}, {"HRID", "33"}});
      Element[] userIDNotNumericElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "User ID Is Not Numeric", new String[][] {
                  {"User.getUserId()", "70"}, {"User.getName()", "70"}, {"HRID", "33"}, {"Active", "33"}});
      Element[] emailWarningsElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "Email Warnings", new String[][] {
                  {"UserName", "70"}, {"HRID", "33"}, {"Email", "100"}});
      Element[] artifactAttributeWarningElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "Artifact Attribute Warning", new String[][] {
                  {"User Name", "30"}, {"HRID", "10"}, {"Warning", "170"}});
      Element[] notMemberOfEveryoneGroupElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "Not A Member Of Everyone Group",
                  new String[][] { {"User Name", "60"}, {"HRID", "28"}});
      Element[] inactiveUserHasRelationsElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "Inactive User(s) Have ATS Relations ",
                  new String[][] { {"User Name", "60"}, {"HRID", "28"}, {"# of ATS Relations", "28"}});
      Element[] allUsersElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "All Users", new String[][] {
                  {"User.getUserId()", "70"}, {"User.getName()", "70"}, {"HRID", "33"}, {"User.isActive()", "33"},
                  {"MsaUserDb Active", "33"}, {"Active Relation Count", "33"}});
      Element[] userNameStyleWarningElements =
            Xml.makeDivElementAndTableElement(parentDivElement, "User Name Style Is Suspect", new String[][] {
                  {"User.getUserId()", "70"}, {"User.getName()", "70"}, {"HRID", "33"}});
      Artifact myArtifactEveryoneGroup = EveryoneGroup.getInstance().getEveryoneGroup();
      Map<String, User> userIDMap = new TreeMap<String, User>();
      Map<String, String> relationMap = new TreeMap<String, String>();
      Map<String, Artifact> userNameMap = new TreeMap<String, Artifact>();
      Collection<Artifact> userArtifacts =
            artifactPersistenceManager.getArtifactsFromSubtypeName("User", branchPersistenceManager.getAtsBranch());
      Artifact[] artifacts = userArtifacts.toArray(new Artifact[userArtifacts.size()]);
      for (int i = 0; i < artifacts.length; i++) {
         userNameMap.put(artifacts[i].getDescriptiveName() + i, artifacts[i]);
      }
      int countOfNewInactives = 0;
      for (String keyString : userNameMap.keySet()) {
         Artifact userArtifact = userNameMap.get(keyString);
         User aUser = (User) userArtifact;
         String aUserID = aUser.getUserId();
         String aUserName = aUser.getName();
         String myMsaUserActiveString = "?";
         String myUserClassActiveString = "Inactive";
         if (aUser.isActive()) myUserClassActiveString = "Active";
         MsaUser myMsaUser = myMsaUserDb.getUserByName(aUserName);
         if (myMsaUser != null) {
            myMsaUserActiveString = myMsaUser.getValue(MsaUserDb.Msa_User_Column.active).toLowerCase();
         }
         myMsaUserActiveString = myMsaUserActiveString.replaceAll("yes", "Active").replaceAll("no", "Inactive");
         relationMap.clear();
         ArrayList<IRelationLink> myRelationLinks = userArtifact.getRelations(myArtifactEveryoneGroup);
         for (IRelationLink relationLink : myRelationLinks) {
            Artifact myArtifactA = relationLink.getArtifactA();
            String myArtifactADescriptiveName = myArtifactA.getDescriptiveName();
            relationMap.put(myArtifactADescriptiveName, myArtifactADescriptiveName);
         }
         if (!relationMap.containsKey("Everyone")) {
            Xml.makeTableRow(notMemberOfEveryoneGroupElements, new String[] {userArtifact.getDescriptiveName(),
                  userArtifact.getHumanReadableId()});
         }
         Collection<Artifact> myRelatedToAtsObjectSearchResults = null;
         String countOfRelations = "?";
         if (myMsaUserActiveString.equals("Inactive")) {
            String staticIDAttributeValue =
                  userArtifact.getSoleStringAttributeValue(ArtifactStaticIdSearch.STATIC_ID_ATTRIBUTE);
            if (!staticIDAttributeValue.equals(OSEE_AUTORUN_USER_RELATIONS_CHECKED)) {
               UserRelatedToAtsObjectSearch myUserRelatedToAtsObjectSearch =
                     new UserRelatedToAtsObjectSearch("User search", aUser, true, LoadView.None);
               try {
                  myRelatedToAtsObjectSearchResults = myUserRelatedToAtsObjectSearch.performSearchGetResults();
               } catch (Exception e) {
                  e.printStackTrace();
               }
               if (myRelatedToAtsObjectSearchResults != null) {
                  countOfRelations = "" + myRelatedToAtsObjectSearchResults.size();
                  countOfNewInactives++;
                  if (myRelatedToAtsObjectSearchResults.size() > 0) {
                     Xml.makeTableRow(inactiveUserHasRelationsElements, new String[][] { {aUser.getName()},
                           {userArtifact.getHumanReadableId()},
                           {"" + myRelatedToAtsObjectSearchResults.size(), "style", "color:red"}});
                  }
               }
            }
         }
         if (myMsaUserActiveString.equals("Active")) {
            if (aUserName.indexOf(",") == -1) {
               Xml.makeTableRow(userNameStyleWarningElements, new String[] {aUser.getUserId(), aUser.getName(),
                     userArtifact.getHumanReadableId()});
            }
            if (!aUserID.matches("\\d{1,9}")) {
               Xml.makeTableRow(userIDNotNumericElements, new String[] {aUser.getUserId(), aUser.getName(),
                     userArtifact.getHumanReadableId(), myMsaUserActiveString});
            }
            Collection<DynamicAttributeManager> myDynamicAttributeManagers =
                  myAttributeContentProvider.populateAttributeTypes(userArtifact);
            for (DynamicAttributeManager dynamicAttributeManager : myDynamicAttributeManagers) {
               Attribute myAttribute = null;
               DynamicAttributeDescriptor myDynamicAttributeDescriptor = dynamicAttributeManager.getAttributeType();
               String name = myDynamicAttributeDescriptor.getName();
               try {
                  myAttribute = dynamicAttributeManager.getSoleAttribute();
               } catch (Exception e) {
                  e.printStackTrace();
                  if (!e.getMessage().equals("null")) {
                     Xml.makeTableRow(artifactAttributeWarningElements, new String[][] {
                           {userArtifact.getDescriptiveName()}, {userArtifact.getHumanReadableId()},
                           {e.getMessage(), "style", "color:red"}});
                  }
               }
               if (myAttribute != null) {
                  String contents = myAttribute.getRawStringValue();
                  if (contents != null) {
                     if (contents.equals("")) contents = " ";
                     if (name.indexOf("Email") > -1) {
                        if (!contents.matches(".*[.].*@boeing.com")) {
                           Xml.makeTableRow(emailWarningsElements, new String[][] {
                                 {userArtifact.getDescriptiveName()}, {userArtifact.getHumanReadableId()},
                                 {contents, "style", "color:red"}});
                        }
                     }
                     if (name.indexOf("Phone") > -1) {
                        if (!contents.trim().matches("\\d{3,3}([ ]|-|\\s{0,})\\d{3,3}([ ]|-|\\s{0,})\\d{4,4}") || contents.trim().matches(
                              "\\d{3,3}([ ]|-|\\s{0,})981([ ]|-|\\s{0,})\\d{4,4}")) {
                           Xml.makeTableRow(phoneWarningsElements, new String[][] {
                                 {userArtifact.getDescriptiveName()}, {userArtifact.getHumanReadableId()},
                                 {contents, "style", "color:red"}});
                        }
                     }
                  }
               }
            }
         }
         if (userIDMap.containsKey(aUserID)) {
            Xml.makeTableRow(duplicatesElements, new String[] {userArtifact.getHumanReadableId(),
                  userArtifact.getDescriptiveName(), userIDMap.get(aUserID).getHumanReadableId(),
                  userIDMap.get(aUserID).getDescriptiveName()});
         }
         userIDMap.put(aUserID, (User) userArtifact);
         if (myMsaUserActiveString.equals("?")) {
            Xml.makeTableRow(usersNotInMsaUserDbElements, new String[] {aUser.getName(),
                  userArtifact.getHumanReadableId()});
         } else if (!((myUserClassActiveString.equals(myMsaUserActiveString)))) {
            Xml.makeTableRow(activeSetCorrectlyElements, new String[] {aUser.getName(),
                  userArtifact.getHumanReadableId(), myUserClassActiveString, myMsaUserActiveString});
         }
         Xml.makeTableRow(allUsersElements, new String[] {aUser.getUserId(), aUser.getName(),
               userArtifact.getHumanReadableId(), myUserClassActiveString, myMsaUserActiveString, countOfRelations});
      }
      Object[][] errorElementsAndTexts =
            {
                  {inactiveUserHasRelationsElements[2], "Error", "Inactive User(s) Have ATS Relations.",
                        "Inactive User(s) Have ATS Relations"},
                  {activeSetCorrectlyElements[2], "Error", "User(s) Whose 'Active' Is Not Set Correctly.",
                        "Users With 'Active' Set Incorrecly"},
                  {usersNotInMsaUserDbElements[2], "Error", "User(s) Not Present In MsaUserDb.",
                        "User(s) Not Present In MsaUserDb"},
                  {duplicatesElements[2], "Error", "Duplicate User IDs were found.", "Duplicate User IDs were found"},
                  {notMemberOfEveryoneGroupElements[2], "Error", "User(s) Not Member Of Everyone Group were found.",
                        "Users Were Not Members of the Everyone Group"},
                  {phoneWarningsElements[2], "Warning", "Phone Number Warnings Were Found.",
                        "Users Had Suspect  Phone Number."},
                  {userIDNotNumericElements[2], "Warning", "User ID(s) Were Not Numeric.", "User IDs were Not Numeric"},
                  {emailWarningsElements[2], "Warning", "Email Warnings Were Found.",
                        "Users Had Suspicious Email Addresses"},
                  {artifactAttributeWarningElements[2], "Warning", "Artifact Attribute Warnings were found.",
                        "Artifact Attribute Warnings Were Found"},
                  {userNameStyleWarningElements[2], "Warning",
                        "Users Whose Name Wasn't Formatted Properly were found.", "Users Had Atypical Names"},
                  {allUsersElements[2], "", "All Users.", "Users Examined"}};
      summarizeResultData(myXResultData, parentDivElement, errorElementsAndTexts);
      OutputFormat myOutputFormat = Jaxp.getCompactFormat(parentDivElement.getOwnerDocument());
      myOutputFormat.setOmitDocumentType(true);
      myOutputFormat.setOmitXMLDeclaration(true);
      String resultXHTML = Jaxp.xmlToString(parentDivElement.getOwnerDocument(), myOutputFormat);
      myXResultData.addRaw(resultXHTML);
      myXResultData.log("Completed processing " + artifacts.length + " artifacts.");
   }

   public static final Element summarizeResultData(XResultData myXResultData, Element parentDivElement, Object[][] errorElementsAndTexts) throws XPathExpressionException {
      for (int i = 0; i < errorElementsAndTexts.length; i++) {
         Object[] errorElementsAndText = errorElementsAndTexts[i];
         Element theElement = (Element) errorElementsAndText[0];
         String errorText = (String) errorElementsAndText[2];
         String normalText = (String) errorElementsAndText[3];
         Node[] tableRows = Xml.selectNodeList(theElement, "descendant::tr");
         if (tableRows.length > 1) {
            Xml.appendNewElementWithText(parentDivElement, "br", "");
            Xml.appendNewElementWithText(parentDivElement, "hr", "");
            Xml.appendNewElementWithTextAndAttributes(parentDivElement, "a", null, new String[][] {{"name",
                  "anchor" + i}});
            Xml.appendNewElementWithTextAndAttributes(parentDivElement, "xml", "MyText", new String[][] {
                  {"style", "display:none;"}, {"id", "SomeID"}});//A test to see that embedded xml will not ruin the html rendering.
            parentDivElement.appendChild(theElement);
            String errorWarningText =
                  "" + (tableRows.length - 1) + " " + errorText + "<a href=\"#anchor" + i + "\" > (See Table Below.)</a>";
            if (((String) errorElementsAndText[1]).equals("Error")) {
               myXResultData.logError(errorWarningText);
            } else if (((String) errorElementsAndText[1]).equals("Warning")) {
               myXResultData.logWarning(errorWarningText);
            } else {
               myXResultData.log(errorWarningText);
            }
         } else {
            myXResultData.log("" + (tableRows.length - 1) + " " + normalText);
         }
      }
      return parentDivElement;
   }

   /**
    * @param duplicatesDivElement
    * @return
    */

   public String get24HourStartTime() {
      return "23:15";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#getCategory()
    */
   public String getCategory() {
      return "OSEE ATS";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#getDescription()
    */
   public String getDescription() {
      return "Ensure there are no duplicpate users in the DB.";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#getRunDb()
    */
   public RunDb getRunDb() {
      return IAutoRunTask.RunDb.Production_Db;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#getTaskType()
    */
   public TaskType getTaskType() {
      return IAutoRunTask.TaskType.Db_Health;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask#startTasks(org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData)
    */
   public void startTasks(XResultData resultData) throws Exception {
      runIt(null, resultData);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.util.IAutoRunTask#getNotificationEmailAddresses()
    */
   public String[] getNotificationEmailAddresses() {
      return new String[] {"donald.g.dunne@boeing.com, ryan.d.brooks@boeing.com"};
   }

}
