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
package org.eclipse.osee.ats.presenter;

import junit.framework.Assert;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponentInterface;
import org.eclipse.osee.ats.mocks.MockAtsArtifactProvider;
import org.eclipse.osee.ats.mocks.MockAtsSearchHeaderComponent;
import org.eclipse.osee.ats.presenter.internal.AtsSearchPresenterImpl;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class WebProgramsPresenterTest {

   @Test
   public void testSelectProgram() {
      MockAtsArtifactProvider provider = new MockAtsArtifactProvider();
      AtsSearchPresenterImpl<AtsSearchHeaderComponentInterface> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponentInterface>(provider);
      MockAtsSearchHeaderComponent comp = new MockAtsSearchHeaderComponent();
      WebId program = new WebId("prg1Guid", "program1");
      presenter.selectProgram(program, comp);
      Assert.assertEquals(2, comp.getBuilds().size());
      Assert.assertEquals("build2", comp.getBuilds().get(1).getName());
   }

   @Test
   public void testSelectSearch() {
      AtsSearchPresenterImpl<AtsSearchHeaderComponentInterface> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponentInterface>(null);
      MockSearchNavigator navigator = new MockSearchNavigator();
      WebId program = new WebId("prgGuid", "prgName");
      WebId build = new WebId("bldGuid", "bldName");
      presenter.selectSearch(program, build, true, "test search phrase", navigator);
      String url = navigator.getResultsUrl();
      String expected = "program=prgGuid?build=bldGuid?nameOnly=true?search=test%20search%20phrase";
      Assert.assertEquals(expected, url);
   }

   @Test
   public void testInitSearchHome() {
      MockAtsArtifactProvider provider = new MockAtsArtifactProvider();
      AtsSearchPresenterImpl<AtsSearchHeaderComponentInterface> presenter =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponentInterface>(provider);
      MockAtsSearchHeaderComponent headerComp = new MockAtsSearchHeaderComponent();
      presenter.initSearchHome(headerComp);
      Assert.assertTrue(headerComp.isClearAllCalled());
      Assert.assertEquals(3, headerComp.getPrograms().size());
   }

   @Test
   public void testInitSearchResults() {

   }
}
