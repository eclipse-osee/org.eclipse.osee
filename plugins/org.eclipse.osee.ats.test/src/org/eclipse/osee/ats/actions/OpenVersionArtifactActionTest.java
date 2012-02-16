/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class OpenVersionArtifactActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenVersionArtifactAction createAction() throws OseeCoreException {
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      AtsTestUtil.getTeamWf().addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
         AtsTestUtil.getVerArt1());
      AtsTestUtil.getVerArt1().persist(transaction);
      AtsTestUtil.getTeamWf().persist(transaction);
      transaction.execute();
      return new OpenVersionArtifactAction(AtsTestUtil.getTeamWf());
   }
}
