/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.task;

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import org.eclipse.osee.ats.ide.workflow.task.AtsOpenWithTaskRenderer;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.junit.Test;

/**
 * Test against the appropriate presentation type used for the applicability rating.
 *
 * @author Megumi Telles
 */
public class AtsLbaRpcrTaskRendererTest {

   private final PresentationType presentationType1 = PresentationType.F5_DIFF;
   private final PresentationType presentationType2 = PresentationType.PREVIEW;
   private final PresentationType presentationType3 = PresentationType.DIFF;
   private final PresentationType presentationType4 = PresentationType.SPECIALIZED_EDIT;
   AtsOpenWithTaskRenderer renderer = new AtsOpenWithTaskRenderer(new HashMap<RendererOption, Object>());

   @Test
   public void testGetPresentationTypeSpecialized() {
      assertTrue("Rating is expected specialized",
         renderer.getPresentationType(presentationType1) == IRenderer.SPECIALIZED_KEY_MATCH);
   }

   @Test
   public void testGetPresentationTypePresentation() {
      assertTrue("Rating is expected presentation",
         renderer.getPresentationType(presentationType2) == IRenderer.PRESENTATION_SUBTYPE_MATCH);
   }

   @Test
   public void testGetPresentationTypeNoMatch() {
      assertTrue("Rating should match", renderer.getPresentationType(presentationType3) == IRenderer.NO_MATCH);
      assertTrue("Rating should match", renderer.getPresentationType(presentationType4) == IRenderer.NO_MATCH);
   }

}
