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
package org.eclipse.osee.ats.help.ui;

import static org.eclipse.osee.framework.core.data.HelpContextRegistry.asContext;
import org.eclipse.osee.framework.core.data.HelpContext;

/**
 * @author Roberto E. Escobar
 */
public final class AtsHelpContext {

   private AtsHelpContext() {
      // Private Constructor
   }

   private static final String PLUGIN_ID = "org.eclipse.osee.ats.help.ui";

   public static final HelpContext ACTION_VIEW = toContext("atsActionView");
   public static final HelpContext NAVIGATOR = toContext("atsNavigator");
   public static final HelpContext CONFIGURE_WORKFLOW = toContext("atsConfigureWorkflow");

   public static final HelpContext PRIORITY = toContext("atsPriority");
   public static final HelpContext REPORT_BUG = toContext("atsReportABug");

   public static final HelpContext WORLD_VIEW = toContext("atsWorldView");
   public static final HelpContext WORKFLOW_EDITOR__WORKFLOW_TAB = toContext("atsWorkflowEditorWorkflowTab");
   public static final HelpContext WORKFLOW_EDITOR__TASK_TAB = toContext("atsWorkflowEditorTaskTab");
   public static final HelpContext NEW_ACTION_PAGE_1 = toContext("new_action_wizard_page_1");
   public static final HelpContext NEW_ACTION_PAGE_2 = toContext("new_action_wizard_page_2");
   public static final HelpContext DECISION_REVIEW = toContext("decisionReview");
   public static final HelpContext PEER_TO_PEER_REVIEW = toContext("peerToPeerReview");

   private static HelpContext toContext(String id) {
      return asContext(PLUGIN_ID, id);
   }
}
