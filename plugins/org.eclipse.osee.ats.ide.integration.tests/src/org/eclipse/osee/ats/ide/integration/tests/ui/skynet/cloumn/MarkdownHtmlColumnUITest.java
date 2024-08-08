/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet.cloumn;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.MarkdownHtmlColumnUI;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for MarkdownHtmlColumnUI
 *
 * @author Donald G. Dunne
 */
public final class MarkdownHtmlColumnUITest {
   public static final ArtifactToken RobotInterfaceHeading =
      ArtifactToken.valueOf(659132, "Robot Interface Heading", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);

   @Test
   public void testGetHtml() throws Exception {
      MarkdownHtmlColumnUI col = MarkdownHtmlColumnUI.getInstance();
      Artifact swReq = ArtifactQuery.getArtifactFromId(RobotInterfaceHeading, DemoBranches.SAW_Bld_1);
      Assert.assertTrue(swReq != null && swReq.isValid());

      swReq.addAttribute(CoreAttributeTypes.MarkdownContent, "## My first heading\n");
      swReq.persist(getClass().getSimpleName());
      String html = col.getHtml(swReq);
      Assert.assertTrue(html.contains(">My first heading</h2>"));
   }

}
