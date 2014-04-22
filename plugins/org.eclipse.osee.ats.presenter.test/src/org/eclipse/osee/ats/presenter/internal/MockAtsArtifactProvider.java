/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.presenter.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.ui.api.search.AtsArtifactProvider;
import org.eclipse.osee.display.presenter.mocks.MockArtifact;
import org.eclipse.osee.display.presenter.mocks.MockArtifactProvider;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author John R. Misinco
 */
public class MockAtsArtifactProvider extends MockArtifactProvider implements AtsArtifactProvider {

   private final HashCollection<MockArtifact, MockArtifact> programsAndBuilds =
      new HashCollection<MockArtifact, MockArtifact>();

   public MockAtsArtifactProvider() {
      createProgramsAndBuilds();
   }

   private void createProgramsAndBuilds() {
      MockArtifact program1 = new MockArtifact("prg1Guid_18H74Zqo3gA", "program1");
      MockArtifact program2 = new MockArtifact("prg2Guid_DC2cxIwhWwA", "program2");
      MockArtifact program3 = new MockArtifact("prg3Guid_ALnf3ohtbQA", "program3");
      MockArtifact build1 = new MockArtifact("bld1Guid_BwTPQWRIagA", "build1");
      MockArtifact build2 = new MockArtifact("bld2Guid_DkYoCyCF6gA", "build2");
      MockArtifact build3 = new MockArtifact("bld3Guid_31DjLanu7gA", "build3");
      MockArtifact build4 = new MockArtifact("bld4Guid_H2oLkW5W3QA", "build4");
      programsAndBuilds.put(program1, build1);
      programsAndBuilds.put(program1, build2);
      programsAndBuilds.put(program2, build3);
      programsAndBuilds.put(program3, build1);
      programsAndBuilds.put(program3, build3);
      programsAndBuilds.put(program3, build4);
   }

   @Override
   public Collection<ArtifactReadable> getPrograms() {
      return new LinkedList<ArtifactReadable>(programsAndBuilds.keySet());
   }

   @Override
   public Collection<ArtifactReadable> getBuilds(String programGuid) {
      List<ArtifactReadable> toReturn = null;
      for (MockArtifact program : programsAndBuilds.keySet()) {
         if (program.getGuid().equals(programGuid)) {
            toReturn = new LinkedList<ArtifactReadable>(programsAndBuilds.getValues(program));
            break;
         }
      }
      return toReturn;
   }

   @Override
   public long getBaselineBranchUuid(String buildArtGuid) throws OseeCoreException {
      return 12345;
   }

}
