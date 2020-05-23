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

package org.eclipse.osee.ats.ide.notify;

import org.eclipse.osee.ats.ide.util.Overview;
import org.eclipse.osee.ats.ide.util.Overview.PreviewStyle;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationManagerUI {

   public static String getPreviewHtml(AbstractWorkflowArtifact workflow, PreviewStyle... styles) {
      Overview o = new Overview();
      o.addHeader(workflow, styles);
      o.addFooter(workflow, styles);
      return o.getPage();
   }

}
