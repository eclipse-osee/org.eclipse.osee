/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class EventChangeTypeBasicGuidArtifactTest {

   @Test
   public void testEquals() {
      EventChangeTypeBasicGuidArtifact eventArt1 =
         new EventChangeTypeBasicGuidArtifact(Lib.generateUuid(), 1234L, 546L, GUID.create());
      EventChangeTypeBasicGuidArtifact eventArt2 =
         new EventChangeTypeBasicGuidArtifact(eventArt1.getBranchId(), eventArt1.getFromArtTypeGuid(),
            eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertEquals(eventArt1.hashCode(), eventArt2.hashCode());
      Assert.assertEquals(eventArt1, eventArt2);

      eventArt2 =
         new EventChangeTypeBasicGuidArtifact(eventArt1.getBranchId(), eventArt1.getFromArtTypeGuid(),
            eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
         new EventChangeTypeBasicGuidArtifact(Lib.generateUuid(), eventArt1.getFromArtTypeGuid(),
            eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
         new EventChangeTypeBasicGuidArtifact(eventArt1.getBranchId(), 111L, eventArt1.getArtTypeGuid(),
            eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
         new EventChangeTypeBasicGuidArtifact(eventArt1.getBranchId(), eventArt1.getFromArtTypeGuid(),
            eventArt1.getArtTypeGuid(), GUID.create());

      Assert.assertNotSame(eventArt1, eventArt2);

      Set<EventBasicGuidArtifact> toAdd = new HashSet<>();
      toAdd.add(eventArt2);
      toAdd.add(eventArt1);
      Assert.assertEquals(2, toAdd.size());

      toAdd.add(eventArt1);
      Assert.assertEquals(2, toAdd.size());

      Set<EventBasicGuidArtifact> eventArts = new HashSet<>();
      eventArts.add(eventArt2);
      eventArts.addAll(toAdd);
      Assert.assertEquals(2, toAdd.size());

   }
}
