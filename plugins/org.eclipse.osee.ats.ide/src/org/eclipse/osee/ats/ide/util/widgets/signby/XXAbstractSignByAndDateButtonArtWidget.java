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
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.XWidgetStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.user.UserGroupAuthorization;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonHyperWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;

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
public abstract class XXAbstractSignByAndDateButtonArtWidget extends XXButtonHyperWidget {

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAbstractSignByAndDateButtonArtWidget(WidgetId widgetId, AttributeTypeToken attrType1, AttributeTypeToken attrType2) {
      super(widgetId, (attrType1.isDate() ? attrType2.getUnqualifiedName() : attrType1.getUnqualifiedName()));
      setToolTip("Sign or Clear");
      setOseeImage(CoreImage.RUN_EXC);
      Conditions.assertTrue(attrType1.isLong(), "Attribute Type 1 should be Long");
      setAttributeType(attrType1);
      Conditions.assertTrue(attrType2.isDate(), "Attribute Type 2 should be Date");
      setAttributeType2(attrType2);
      addListener();
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getDisplayValue() {
      Date date = getArtifact().getSoleAttributeValue(getDateAttrType(), null);
      if (date != null) {
         UserToken user = OseeApiService.userSvc().getUser(
            getArtifact().getSoleAttributeValue(getUserAttrType(), SystemUser.UnAssigned.getId()));
         if (user != null) {
            return String.format("Signed by %s on %s", user.getName(), DateUtil.getDateNow(date, DateUtil.MMDDYYHHMM));
         }
      }
      return "";
   }

   @Override
   public boolean handleSelection() {
      try {

         XResultData result = isAuthorizedToChange();
         if (result.isErrors()) {
            XResultDataUI.report(result, "Sign-off Error");
            return false;
         }
         // Ok --> 0, Cancel --> 1, Clear --> 2
         int res = MessageDialog.open(3, Displays.getActiveShell(), getLabel(), getSignMessage(), SWT.NONE,
            new String[] {"Ok", "Cancel", "Clear"});
         Job signJob = new Job(getSignMessage()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               if (res == 2) {
                  if (userHasPermission()) {
                     setUnsigned();
                     Displays.ensureInDisplayThread(new Runnable() {

                        @Override
                        public void run() {
                           refresh();
                        }
                     });
                  }
               } else if (res == 0) {
                  if (userHasPermission()) {
                     setSigned();
                     Displays.ensureInDisplayThread(new Runnable() {

                        @Override
                        public void run() {
                           refresh();
                        }
                     });
                  }
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(signJob, false, Job.SHORT, null);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   };

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   public XResultData isValid() {
      if (isRequiredEntry() && getArtifact().getSoleAttributeValue(getSignDateAttrType(), null) == null) {
         Date date = getArtifact().getSoleAttributeValue(getDateAttrType(), null);
         if (date == null) {
            return new XResultData(XResultType.Error, getLabel() + " must be signed");
         }
      }
      return XWidgetStatus.OK;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   protected void addListener() {
      addXModifiedListener(listener);
   }

   private AttributeTypeToken getDateAttrType() {
      return getAttributeType2();
   }

   private AttributeTypeToken getUserAttrType() {
      return getAttributeType();
   }

   private final XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         handleSelection();
      }
   };

   @SuppressWarnings("unlikely-arg-type")
   protected XResultData isAuthorizedToChange() {
      XResultData rd = new XResultData();
      if (is(XOption.LRFT)) {
         Artifact storeArt = getArtifact();
         String teamArtifactId =
            storeArt.getSoleAttribute(AtsAttributeTypes.TeamDefinitionReference).getValue().toString();
         Artifact teamArtifact =
            ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(teamArtifactId), CoreBranches.COMMON);
         if (!teamArtifact.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead).contains(OseeApiService.user())) {
            rd.errorf("Insufficient Privileges to Sign [%s].\nOnly a ATS Team Lead may perform this action.\n" //
               + "Team Leads for this workflow are %s\n", getUserAttrType().getUnqualifiedName(),
               teamArtifact.getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead).toString());
         }
      }
      if (rd.isSuccess()) {
         UserGroupAuthorization.check(widData.getUserGroup(), getLabel(), rd);
      }
      if (rd.isSuccess()) {
         for (IAtsWorkItemHook wiHook : AtsApiService.get().getWorkItemService().getWorkItemHooks()) {
            wiHook.isModifiableAttribute(getArtifact(), getSignByAttrType(), rd);
         }
      }
      return rd;
   }

   protected String getSignMessage() {
      return "Sign [" + getLabel() + "]?";
   }

   public boolean userHasPermission() {
      return true;
   }

   public static void setSigned(Collection<Artifact> artifacts, AttributeTypeToken signDateAttrType,
      AttributeTypeToken signByAttrType, String label, boolean signed) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + label);
      for (Artifact art : artifacts) {
         setSigned(ArtifactId.valueOf(art.getId()), signDateAttrType, signByAttrType, signed, changes);
      }
      changes.executeIfNeeded();
   }

   /**
    * Applies the sign (or clear) to the given change set without executing, so the caller can batch it with other
    * changes.
    */
   public static void setSigned(ArtifactId artifact, AttributeTypeToken signDateAttrType, AttributeTypeToken signByAttrType,
      boolean signed, IAtsChangeSet changes) {
      if (signed) {
         changes.setSoleAttributeValue(artifact, signByAttrType, OseeApiService.user().getId());
         changes.setSoleAttributeValue(artifact, signDateAttrType, new Date());
      } else {
         changes.deleteAttributes(artifact, signByAttrType);
         changes.deleteAttributes(artifact, signDateAttrType);
      }
   }

   @Override
   public boolean isEditable() {
      return true;
   }

   public AttributeTypeId getSignDateAttrType() {
      return getDateAttrType();
   }

   public void setUnsigned() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + getLabel());
      setSigned(changes, false);
      changes.executeIfNeeded();
   }

   public void setSigned() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + getLabel());
      setSigned(changes, true);
      changes.executeIfNeeded();
   }

   /**
    * Applies the sign (or clear) to the given change set without executing, so the caller can batch it with other
    * changes (e.g. WfeDebugFillAction).
    */
   public void setSigned(IAtsChangeSet changes, boolean signed) {
      setSigned(ArtifactId.valueOf(getArtifact().getId()), getDateAttrType(), getUserAttrType(), signed, changes);
   }

   public AttributeTypeToken getSignByAttrType() {
      return getUserAttrType();
   }

}
