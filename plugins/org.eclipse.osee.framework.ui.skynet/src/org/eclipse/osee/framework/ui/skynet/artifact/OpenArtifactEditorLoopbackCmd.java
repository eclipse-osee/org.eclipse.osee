/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.BaseArtifactLoopbackCmd;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IEditorPart;

/**
 * @author Roberto E. Escobar
 */
public class OpenArtifactEditorLoopbackCmd extends BaseArtifactLoopbackCmd {

   @Override
   public boolean isApplicable(String cmd) {
      return cmd != null && cmd.equalsIgnoreCase("open.artifact");
   }

   @Override
   public void process(final Artifact artifact, final Map<String, String> parameters, final HttpResponse httpResponse) {
      if (artifact != null) {
         try {
            boolean hasPermissionToRead = false;
            try {
               hasPermissionToRead = ServiceUtil.accessControlService().hasArtifactPermission(artifact,
                  PermissionEnum.READ, null).isSuccess();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }

            if (!hasPermissionToRead) {
               httpResponse.outputStandardError(HttpURLConnection.HTTP_UNAUTHORIZED,
                  String.format("Access denied - User does not have read access to [%s]", artifact));
            } else {
               final MutableBoolean isDone = new MutableBoolean(false);

               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        IEditorPart part = AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact),
                           ArtifactEditor.EDITOR_ID, true);
                        if (part != null) {
                           String html = AHTML.simplePage(
                              artifact.getName() + " has been opened in OSEE on branch " + artifact.getBranch() + "<br><br>" + "<form><input type=button onClick='window.opener=self;window.close()' value='Close'></form>");
                           httpResponse.getPrintStream().println(html);
                        } else {
                           httpResponse.outputStandardError(HttpURLConnection.HTTP_INTERNAL_ERROR,
                              String.format("Unable to open: [%s]", artifact.getName()));
                        }
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                        httpResponse.outputStandardError(HttpURLConnection.HTTP_INTERNAL_ERROR,
                           String.format("Unable to open: [%s]", artifact.getName()), ex);
                     } finally {
                        isDone.setValue(true);
                     }
                  }
               });

               int count = 1;
               while (!isDone.getValue() && count < 30) {
                  try {
                     Thread.sleep(350);
                  } catch (InterruptedException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
                  count++;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            httpResponse.outputStandardError(HttpURLConnection.HTTP_INTERNAL_ERROR,
               String.format("Unable to open: [%s]", artifact.getName()), ex);
         }
      } else {
         httpResponse.outputStandardError(HttpURLConnection.HTTP_BAD_REQUEST, "Unable to open null artifact");
      }
   }
}
