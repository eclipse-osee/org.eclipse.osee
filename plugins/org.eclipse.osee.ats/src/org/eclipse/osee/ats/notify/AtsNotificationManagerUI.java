/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.notify;

import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;

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
