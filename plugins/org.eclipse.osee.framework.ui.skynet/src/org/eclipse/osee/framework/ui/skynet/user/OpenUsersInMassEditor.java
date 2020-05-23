/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.user;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class OpenUsersInMassEditor extends Action {

   private final Active active;

   public OpenUsersInMassEditor(String name, Active active) {
      super(name);
      this.active = active;
   }

   @Override
   public void run() {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {

         @Override
         public IStatus run(IProgressMonitor monitor) {
            try {
               List<User> users = active == Active.Active ? UserManager.getUsers() : UserManager.getUsersAll();

               MassArtifactEditor.editArtifacts(active == Active.Active ? "Active Users" : "All Users", users);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.runInJob(getText(), runnable, Activator.class, Activator.PLUGIN_ID);
   }
}
