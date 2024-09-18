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

package org.eclipse.osee.ats.ide.util.widgets.signby;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeType2Widget;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeTypeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonWithLabelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * XWidget that provides a label and button. Upon pressing button, user and date will be stored and displayed. Widget
 * must be extended to provide text and attrs to store date and user. Storage will be in storeArt. NOTE: User is stored
 * by User artifact id.<br/>
 * <br/>
 * NOTE: This class should NOT be extended. Instead, use SignbywidgetDefinition in Work Definitions. If new capabilities
 * are needed, they should be incorporated into WidgetBuilder and SignByAndDateWidgetDefinition
 *
 * @author Donald G. Dunne
 */
public class XAbstractSignByAndDateButton extends XButtonWithLabelDam implements AttributeTypeWidget, AttributeType2Widget {

   protected AttributeTypeToken dateAttrType;
   protected AttributeTypeToken byAttrType;

   public XAbstractSignByAndDateButton(AttributeTypeToken attrType1, AttributeTypeToken attrType2) {
      super((attrType1.isDate() ? attrType2.getUnqualifiedName() : attrType1.getUnqualifiedName()), "Sign or Clear",
         ImageManager.getImage(FrameworkImage.RUN_EXC));
      setAttributeType(attrType1);
      setAttributeType2(attrType2);
      this.dateAttrType = attrType1.isDate() ? attrType1 : attrType2;
      this.byAttrType = attrType1.isDate() ? attrType2 : attrType1;
      addListener();
   }

   protected void addListener() {
      addXModifiedListener(listener);
   }

   @Override
   public String getResultsText() {
      Date date = artifact.getSoleAttributeValue(dateAttrType, null);
      if (date != null) {
         User user =
            UserManager.getUserByArtId(artifact.getSoleAttributeValue(byAttrType, SystemUser.UnAssigned.getId()));
         if (user != null) {
            return String.format("Signed by %s on %s", user.getName(), DateUtil.getDateNow(date, DateUtil.MMDDYYHHMM));
         }
      }
      return "";
   }

   private final XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         handleSelection();
      }
   };

   public void handleSelection() {
      try {

         XResultData result = isAuthorizedToChange();
         if (result.isErrors()) {
            XResultDataUI.report(result, "Unable to Sign");
            return;
         }
         // Ok --> 0, Cancel --> 1, Clear --> 2
         int res = MessageDialog.open(3, Displays.getActiveShell(), getLabel(), getSignMessage(), SWT.NONE,
            new String[] {"Ok", "Cancel", "Clear"});
         Job signJob = new Job(getSignMessage()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               if (res == 2) {
                  if (userHasPermission()) {
                     setSigned(artifact, dateAttrType, byAttrType, getLabel(), false);
                     refresh();
                  }
               } else if (res == 0) {
                  if (userHasPermission()) {
                     setSigned(artifact, dateAttrType, byAttrType, getLabel(), true);
                     refresh();
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

   protected XResultData isAuthorizedToChange() {
      XResultData rd = new XResultData();
      if (hasWidgetHint(WidgetHint.LeadRequired)) {
         Artifact storeArt = getArtifact();
         String teamArtifactId =
            storeArt.getSoleAttribute(AtsAttributeTypes.TeamDefinitionReference).getValue().toString();
         Artifact teamArtifact =
            ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(teamArtifactId), CoreBranches.COMMON);
         if (!teamArtifact.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead).contains(UserManager.getUser())) {
            rd.errorf("Insufficient Privileges to Sign [%s].\nOnly a ATS Team Lead may perform this action.\n" //
               + "Team Leads for this workflow are %s\n", byAttrType.getUnqualifiedName(),
               teamArtifact.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead).toString());
         }
      }
      if (rd.isSuccess()) {
         hasUserGroupAuthorization(rd);
      }
      if (rd.isSuccess()) {
         for (IAtsWorkItemHook wiHook : AtsApiService.get().getWorkItemService().getWorkItemHooks()) {
            wiHook.isModifiableAttribute(artifact, getSignByAttrType(), rd);
         }
      }
      return rd;
   }

   private void hasUserGroupAuthorization(XResultData rd) {
      if (userGroup.getId() > 0) {
         IUserGroup group = AtsApiService.get().userService().getUserGroupOrNull(userGroup);
         if (group != null && group.getId() > 0) {
            if (!group.isMember(UserManager.getUser())) {
               StringBuilder sb = new StringBuilder();
               sb.append("You are not authorized to Sign.\n\n");
               sb.append("\n\nAuthorized Users Are:\n-----------------------------\n");
               for (UserToken member : group.getMembers()) {
                  sb.append(member.getName());
                  sb.append("\n");
               }
               rd.error(sb.toString());
            }
         } else {
            rd.errorf("User Group %s Not Found", "User Group needs to be setup.", userGroup.toStringWithId());
         }
      }
   }

   protected String getSignMessage() {
      return "Sign [" + getLabel() + "]?";
   }

   public boolean userHasPermission() {
      return true;
   }

   public static void setSigned(Artifact artifact, AttributeTypeId signDateAttrType, AttributeTypeId signByAttrType,
      String label, boolean signed) {
      setSigned(Collections.singleton(artifact), signDateAttrType, signByAttrType, label, signed);
   }

   public static void setSigned(Collection<Artifact> artifacts, AttributeTypeId signDateAttrType,
      AttributeTypeId signByAttrType, String label, boolean signed) {
      SkynetTransaction tx =
         TransactionManager.createTransaction(artifacts.iterator().next().getBranch(), "Set " + label);
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

   public AttributeTypeId getSignDateAttrType() {
      return dateAttrType;
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && artifact.getSoleAttributeValue(getSignDateAttrType(), null) == null) {
         Date date = getArtifact().getSoleAttributeValue(dateAttrType, null);
         if (date == null) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be signed");
         }
      }
      return Status.OK_STATUS;
   }

   public void setUnsigned() {
      XAbstractSignByAndDateButton.setSigned(getArtifact(), dateAttrType, byAttrType, getLabel(), false);
   }

   public void setSigned() {
      XAbstractSignByAndDateButton.setSigned(getArtifact(), dateAttrType, byAttrType, getLabel(), true);
   }

   @Override
   public Label getControl() {
      return labelWidget;
   }

   public AttributeTypeToken getSignByAttrType() {
      return byAttrType;
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      super.setAttributeType(attributeType);
      if (attributeType.isDate()) {
         this.dateAttrType = attributeType;
      } else {
         this.byAttrType = attributeType;
      }
   }

   @Override
   public void setAttributeType2(AttributeTypeToken attributeType2) {
      super.setAttributeType2(attributeType2);
      if (attributeType2.isDate()) {
         this.dateAttrType = attributeType2;
      } else {
         this.byAttrType = attributeType2;
      }
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      if (getOseeImage() != null) {
         Image img = ImageManager.getImage(getOseeImage());
         setImage(img);
         getbutton().setImage(img);
      }
   }

}
