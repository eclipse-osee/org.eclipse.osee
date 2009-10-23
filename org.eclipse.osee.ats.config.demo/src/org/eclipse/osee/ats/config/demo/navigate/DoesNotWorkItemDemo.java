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
package org.eclipse.osee.ats.config.demo.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewWorkflowManager;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemDemo extends WorldXNavigateItemAction {

   /**
    * @param parent
    * @throws OseeArgumentException
    */
   public DoesNotWorkItemDemo(XNavigateItem parent) throws OseeArgumentException {
      super(parent, "Does Not Work - Demo - Create peer review", FrameworkImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
         return;
      }

      createAndPersistPeerReview();
      AWorkbench.popup("Completed", "Complete");
   }

   private void createAndPersistPeerReview() throws Exception {

      TeamWorkFlowArtifact firstCodeArt = DemoDbUtil.getSampleCodeWorkflows().get(0);
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Demo - Create and Persist Review");
      // Create a PeerToPeer review and leave in Prepare state
      PeerToPeerReviewArtifact reviewArt =
            firstCodeArt.getSmaMgr().getReviewManager().createNewPeerToPeerReview(
                  "Peer Review first set of code changes",
                  firstCodeArt.getSmaMgr().getStateMgr().getCurrentStateName(), transaction);
      reviewArt.persist(transaction);

      // Create a PeerToPeer review and transition to Review state
      reviewArt =
            firstCodeArt.getSmaMgr().getReviewManager().createNewPeerToPeerReview(
                  "Does Not Work " + AtsUtil.getAtsDeveloperIncrementingNum(),
                  firstCodeArt.getSmaMgr().getStateMgr().getCurrentStateName(), transaction);
      List<UserRole> roles = new ArrayList<UserRole>();
      roles.add(new UserRole(Role.Author, DemoDbUtil.getDemoUser(DemoUsers.Joe_Smith)));
      roles.add(new UserRole(Role.Reviewer, DemoDbUtil.getDemoUser(DemoUsers.Kay_Jones)));
      roles.add(new UserRole(Role.Reviewer, DemoDbUtil.getDemoUser(DemoUsers.Alex_Kay), 2.0, true));
      Result result =
            PeerToPeerReviewWorkflowManager.transitionTo(reviewArt,
                  PeerToPeerReviewArtifact.PeerToPeerReviewState.Review, roles, null, UserManager.getUser(), false,
                  transaction);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Review: " + result.getText());
      }
      reviewArt.persist(transaction);
      transaction.execute();

      AtsUtil.openAtsAction(reviewArt, AtsOpenOption.OpenAll);
   }
}
