/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionDatas;
import org.eclipse.osee.ats.core.action.AtsActionService;
import org.eclipse.osee.ats.core.action.CreateActionOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsActionServiceServer extends AtsActionService {

   public AtsActionServiceServer(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public NewActionData createAction(NewActionData data) {
      try {

         AtsUser asUser = CreateActionOperation.getAsUser(data, atsApi.user(), atsApi);
         if (asUser == null || asUser.isUnAssigned()) {
            data.getRd().errorf("Invalid asUser [%s]", data.getAsUser());
            return data;
         }

         String operationName = data.getOpName();
         if (Strings.isInvalid(operationName)) {
            data.getRd().error("Operation Name must be specified");
            return data;
         }

         IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(data.getOpName(), asUser);

         createAction(data, changes);

         if (data.getRd().isErrors()) {
            return data;
         }

         /**
          * Only execute if change set was not sent in. This means there are other changes being done in addition to the
          * action creation.
          */
         TransactionId transaction = changes.executeIfNeeded();
         if (transaction != null && transaction.isInvalid()) {
            data.getRd().errorf("TransactionId came back as inValid.  Action not created.");
            return data;
         }
         data.getActResult().setTransaction(transaction);
      } catch (Exception ex) {
         data.getRd().errorf("Exception creating action [%s]", Lib.exceptionToString(ex));
      }
      return data;
   }

   @Override
   public NewActionDatas createActions(NewActionDatas datas) {
      try {
         AtsUser asUser = CreateActionOperation.getAsUser(ArtifactId.valueOf(datas.getAsUserArtId()), atsApi.user(),
            datas.getRd(), atsApi);
         if (datas.getRd().isErrors()) {
            return datas;
         }

         String operationName = datas.getOpName();
         if (Strings.isInvalid(operationName)) {
            datas.getRd().error("Operation Name must be specified");
            return datas;
         }

         IAtsChangeSet changes = null;
         if (datas.getChanges() == null) {
            changes = atsApi.getStoreService().createAtsChangeSet(datas.getOpName(), asUser);
            datas.setPersist(true);
         } else {
            changes = datas.getChanges();
         }

         atsApi.getNotificationService().setNotificationsEnabled(datas.isEmailPocs());

         for (NewActionData data : datas.getNewActionDatas()) {
            createAction(data, changes);
         }

         if (datas.getRd().isErrors()) {
            return datas;
         }

         // Execute if persist, else change was set sent in
         if (datas.isPersist()) {
            TransactionToken transaction = changes.executeIfNeeded();
            if (transaction != null && transaction.isInvalid()) {
               datas.getRd().errorf("TransactionId came back as inValid.  Actions not created.");
               return datas;
            }
            datas.setTransaction(transaction);
         }
      } catch (Exception ex) {
         datas.getRd().errorf("Exception creating action [%s]", Lib.exceptionToString(ex));
      } finally {
         atsApi.getNotificationService().setNotificationsEnabled(true);
      }

      return datas;
   }

}
