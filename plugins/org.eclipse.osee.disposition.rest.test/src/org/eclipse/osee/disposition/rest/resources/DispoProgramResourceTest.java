/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoFactory;
import org.eclipse.osee.disposition.rest.util.HtmlWriter;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Angel Avila
 */
public class DispoProgramResourceTest {

   @Mock
   private DispoApi dispoApi;
   @Mock
   private HtmlWriter htmlWriter;
   @Mock
   private DispoProgram id1;
   @Mock
   private DispoProgram id2;
   @Mock
   private DispoFactory dispoFactory;

   private DispoProgramResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoProgramResource(dispoApi, htmlWriter, dispoFactory);
      when(id1.getUuid()).thenReturn(23L);
      when(id2.getUuid()).thenReturn(25L);
   }

   @Test
   public void testGetAllAsHtml() {
      // No Sets
      ResultSet<IOseeBranch> emptyResultSet = ResultSets.emptyResultSet();
      when(dispoApi.getDispoPrograms()).thenReturn(emptyResultSet);
      Response noProgramsResponse = resource.getAllPrograms();
      String messageActual = (String) noProgramsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noProgramsResponse.getStatus());
      assertEquals(DispoMessages.Program_NoneFound, messageActual);

      IOseeBranch branch = TokenFactory.createBranch(id1.getUuid(), "testBranch");
      ResultSet<IOseeBranch> branchList = ResultSets.singleton(branch);

      when(dispoApi.getDispoPrograms()).thenReturn(branchList);
      when(htmlWriter.createDispositionPage("Programs", "", branchList)).thenReturn("htmlFromWriter");
      Response oneSetResponse = resource.getAllPrograms();
      String html = (String) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals("htmlFromWriter", html);
   }

   @Test
   public void testGetProgramById() {
      // No Sets
      when(dispoApi.getDispoProgramById(id2)).thenReturn(null);
      Response noSetsResponse = resource.getProgramById(String.valueOf(id2.getUuid()));
      String messageActual = (String) noSetsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noSetsResponse.getStatus());
      assertEquals(DispoMessages.Program_NotFound, messageActual);

      IOseeBranch testBranch = TokenFactory.createBranch(id1.getUuid(), "testBranch");
      when(dispoFactory.createProgram(String.valueOf(id1.getUuid()))).thenReturn(id1);
      when(dispoApi.getDispoProgramById(id1)).thenReturn(testBranch);
      String prefixPath = testBranch.getUuid() + "/set";
      String subTitle = "Disposition Sets";
      when(htmlWriter.createDispoPage(testBranch.getName(), prefixPath, subTitle, "[]")).thenReturn("htmlFromWriter");

      Response response = resource.getProgramById(String.valueOf(testBranch.getUuid()));
      String returnedHtml = (String) response.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      assertEquals("htmlFromWriter", returnedHtml);
   }
}
