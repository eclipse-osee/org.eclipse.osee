/*
 * Created on Aug 12, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal;

import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class AtsServerImplTest {

   // @formatter:off
   @Mock private IAtsTeamWorkflow teamWf;
   @Mock private ArtifactReadable teamArt;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testGetAtsId() {
      when(teamWf.getStoreObject()).thenReturn(teamArt);
      when(teamArt.getSoleAttributeAsString(AtsAttributeTypes.AtsId, AtsUtilCore.DEFAULT_ATS_ID_VALUE)).thenReturn(
         "ATS123");

      AtsServerImpl serverImpl = new AtsServerImpl();
      Assert.assertEquals("ATS123", serverImpl.getAtsId(teamWf));
   }
}
