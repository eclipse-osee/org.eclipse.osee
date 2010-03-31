/*
 * Created on Mar 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.cases;

import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.event.artifact.DefaultEventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.artifact.IEventBasicGuidArtifact;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DefaultEventChangeTypeBasicGuidArtifactTest {

   @Test
   public void testEquals() throws OseeCoreException {
      DefaultEventChangeTypeBasicGuidArtifact eventArt1 =
            new DefaultEventChangeTypeBasicGuidArtifact(GUID.create(), GUID.create(), GUID.create(), GUID.create());
      DefaultEventChangeTypeBasicGuidArtifact eventArt2 =
            new DefaultEventChangeTypeBasicGuidArtifact(eventArt1.getBranchGuid(), eventArt1.getFromArtTypeGuid(),
                  eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertEquals(eventArt1.hashCode(), eventArt2.hashCode());
      Assert.assertEquals(eventArt1, eventArt2);

      eventArt2 =
            new DefaultEventChangeTypeBasicGuidArtifact(eventArt1.getBranchGuid(), eventArt1.getFromArtTypeGuid(),
                  eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
            new DefaultEventChangeTypeBasicGuidArtifact(GUID.create(), eventArt1.getFromArtTypeGuid(),
                  eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
            new DefaultEventChangeTypeBasicGuidArtifact(eventArt1.getBranchGuid(), GUID.create(),
                  eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
            new DefaultEventChangeTypeBasicGuidArtifact(eventArt1.getBranchGuid(), eventArt1.getFromArtTypeGuid(),
                  eventArt1.getArtTypeGuid(), GUID.create());

      Assert.assertNotSame(eventArt1, eventArt2);

      Set<IEventBasicGuidArtifact> toAdd = new HashSet<IEventBasicGuidArtifact>();
      toAdd.add(eventArt2);
      toAdd.add(eventArt1);
      Assert.assertEquals(2, toAdd.size());

      toAdd.add(eventArt1);
      Assert.assertEquals(2, toAdd.size());

      Set<IEventBasicGuidArtifact> eventArts = new HashSet<IEventBasicGuidArtifact>();
      eventArts.add(eventArt2);
      eventArts.addAll(toAdd);
      Assert.assertEquals(2, toAdd.size());

   }
}
