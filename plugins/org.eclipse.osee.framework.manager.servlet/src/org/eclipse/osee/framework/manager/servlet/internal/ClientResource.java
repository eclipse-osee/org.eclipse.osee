/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.internal;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.Client;

/**
 * @author Donald G. Dunne
 */
@Path("client")
public class ClientResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;
   private final IApplicationServerManager serverManager;
   private final ISessionManager sessionManager;

   public ClientResource(IApplicationServerManager serverManager, ISessionManager sessionManager) {
      this.serverManager = serverManager;
      this.sessionManager = sessionManager;
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Client get() throws OseeCoreException {
      Client client = new Client();
      String[] supportedVersions = serverManager.getVersions();
      for (String ver : supportedVersions) {
         client.addSupportedVersion(ver);
      }
      return client;
   }

   @Path("status")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public ClientStatus getStatus() {
      return getStatus(null);
   }

   @Path("status/{userId}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public ClientStatus getStatus(@PathParam("userId") String userId) {
      ClientStatus status = new ClientStatus();
      Set<String> duplicate = new HashSet<String>(50);
      Calendar cal = DateUtil.getCalendar(new Date());
      cal.add(Calendar.DAY_OF_WEEK, -7);
      Date weekAgo = cal.getTime();

      Map<String, ReleaseTypeStatus> nameToReleaseMap = new HashMap<String, ReleaseTypeStatus>(10);
      for (ISession session : sessionManager.getAllSessions()) {
         if (session.getCreationDate().before(weekAgo)) {
            // skipping session from week ago
            continue;
         }
         if (userId != null && !session.getUserId().equals(userId)) {
            // skip users not specified if single user chosen
            continue;
         }
         String addrPort = String.format("%s:%s", session.getClientAddress(), session.getClientPort());
         // skip duplicate entries
         if (!duplicate.contains(addrPort)) {
            duplicate.add(addrPort);
            String infoStr = getInfo(session);
            if (Strings.isValid(infoStr)) {
               ClientInfo info = new ClientInfo(infoStr);
               String version = info.getVersion();
               ReleaseStatus release = (ReleaseStatus) nameToReleaseMap.get(version);
               String type = getType(version);
               if (release == null) {
                  release = new ReleaseStatus();
                  nameToReleaseMap.put(version, release);
                  status.getReleases().add(release);
                  release.setName(version);
                  release.setType(type);
               }

               ReleaseTypeStatus shortRel = nameToReleaseMap.get(type);
               if (shortRel == null) {
                  shortRel = new ReleaseTypeStatus();
                  nameToReleaseMap.put(type, shortRel);
                  status.getReleaseByType().add(shortRel);
                  shortRel.setType(type);
               }
               shortRel.incClient();

               release.incClient();
               release.getUsers().add(info.getName());
            }
         }
      }
      return status;
   }

   private String getType(String version) {
      if (version.endsWith("-REL")) {
         return "REL";
      } else if (version.endsWith("-RC")) {
         return "RC";
      } else if (version.toLowerCase().contains("dev") || version.toLowerCase().contains("local")) {
         return "DEV";
      }
      return String.format("unhandled version [%s]", version);
   }

   private String getInfo(ISession session) throws OseeCoreException {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         URL url =
            new URL(String.format("http://%s:%s/osee/request?cmd=info", session.getClientAddress(),
               session.getClientPort()));
         AcquireResult result = HttpProcessor.acquire(url, outputStream, 1000);
         if (result.wasSuccessful()) {
            return outputStream.toString(result.getEncoding());
         }
      } catch (Exception ex) {
         // do nothing
      }
      return "";
   }
}
