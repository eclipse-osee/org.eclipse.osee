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
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Arrays;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Allows for the conversion of ATS database from Build to Build
 *
 * @author Donald G. Dunne
 */
@Path("convert")
public final class ConvertResource {

   private final IAtsServer atsServer;

   public ConvertResource(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   /**
    * @return html representation of ATS Convert Resource
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getStates() throws Exception {
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.beginMultiColumnTable(95, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Name", "Report", "Run", "Description")));
      for (IAtsDatabaseConversion convert : atsServer.getDatabaseConversions()) {
         sb.append(AHTML.addRowMultiColumnTable(convert.getName(), getForm(convert.getName(), "report", "REPORT-ONLY"),
            getForm(convert.getName(), "run", "RUN"), AHTML.textToHtml(convert.getDescription())));
      }
      sb.append(AHTML.endMultiColumnTable());

      return new ViewModel("convert.html") //
         .param("conversionTable", sb.toString());
   }

   private String getForm(String convertName, String operation, String buttonLabel) {
      StringBuffer sb = new StringBuffer();
      sb.append("<form method=\"post\" action=\"/ats/convert\" >");
      sb.append("<input type=\"hidden\" name=\"convertName\" value=\"" + convertName + "\"/>");
      sb.append("<input type=\"hidden\" name=\"operation\" value=\"" + operation + "\"/>");
      sb.append("<input type=\"submit\" value=\"" + buttonLabel + "\" /></form>");
      return sb.toString();
   }

   /**
    * @param convertName - conversion name to perform. eg: ProgramGuidToId
    * @param operation - run if conversion should run, report for report without persist
    * @return Html results of conversion
    */
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public ViewModel createAction(MultivaluedMap<String, String> form, @Context UriInfo uriInfo) throws Exception {

      String convertName = form.getFirst("convertName");
      String operation = form.getFirst("operation");
      boolean reportOnly = !operation.equals("run");
      XResultData results = new XResultData(false);
      results.logf("Running [%s] ...\n", convertName);
      for (IAtsDatabaseConversion convert : atsServer.getDatabaseConversions()) {
         if (convert.getName().equals(convertName)) {
            convert.run(results, reportOnly, atsServer);
         }
      }
      if (results.isErrors()) {
         throw new OseeCoreException(results.toString());
      }

      return RestUtil.simplePage(convertName, results.toString());
   }
}
