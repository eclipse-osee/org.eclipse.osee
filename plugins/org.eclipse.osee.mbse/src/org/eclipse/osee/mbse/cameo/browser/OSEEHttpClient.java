/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.mbse.cameo.browser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author David W. Miller
 */
public class OSEEHttpClient {

   private final ObjectMapper mapper = new ObjectMapper();

   public List<BranchData> getBranchData(String uri) {
      List<BranchData> toReturn = null;
      HttpGet request = new HttpGet(uri);

      try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(request)) {
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            toReturn = getBranchesFromString(result);
         } else {
            toReturn = new ArrayList<BranchData>();
            Application.getInstance().getGUILog().showMessage(
               String.format("Get branch data failed status code %d, uri: %s", statusCode, uri));
         }
      } catch (Exception e) {
         toReturn = new ArrayList<BranchData>();
         Application.getInstance().getGUILog().showMessage("Get branch data failed (exception) for uri: " + uri);
      }
      return toReturn;
   }

   public void showLocation() {
      StringBuilder locations = new StringBuilder();
      Project prj = Application.getInstance().getProjectsManager().getActiveProject();

      List<ProjectDescriptor> projectDescriptors = ProjectDescriptorsFactory.getAvailableDescriptorsForProject(prj);
      for (int i = projectDescriptors.size() - 1; i >= 0; --i) {
         URI uri = projectDescriptors.get(i).getURI();
         if (uri != null) {
            String location = uri.toString();
            if (location != null) {
               locations.append(location).append("\n");
            }
         }
      }
      Application.getInstance().getGUILog().showMessage(locations.toString());
   }

   private List<BranchData> getBranchesFromString(String data) {
      try {
         BranchData[] branches = mapper.readValue(data, BranchData[].class);
         List<BranchData> items = new ArrayList<BranchData>();
         for (int i = 0; i < branches.length; ++i) {
            // return only working branches
            if (branches[i].getBranchType() == 0) {
               items.add(branches[i]);
            }
         }
         return items;
      } catch (JsonMappingException e) {
         Application.getInstance().getGUILog().showMessage("Json Mapping Error for BranchData");
      } catch (JsonProcessingException e) {
         Application.getInstance().getGUILog().showMessage("Json Processing Error for BranchData");
      }
      return new ArrayList<>();
   }

   public void connectElementToOSEE(String elementName) {
      if (elementName == null || elementName.isEmpty()) {
         Application.getInstance().getGUILog().showMessage("Null element name provided to OSEE element connection");
         return;
      }
      String url = ProjectBranchUtility.getOSEECreationUrl(1, 200091);
      HttpPost post = new HttpPost(url);
      post.setHeader("Accept", "application/json");
      post.setHeader("Content-type", "application/json");
      post.setHeader("osee.account.id", "11");
      String json = "[\"" + elementName + "\"]";
      StringEntity sentity = null;
      try {
         sentity = new StringEntity(json);
      } catch (UnsupportedEncodingException ex) {
         //
      }
      post.setEntity(sentity);

      try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(post)) {

         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            Application.getInstance().getGUILog().showMessage(String.format("Artifact Created data: %s", result));
         } else {

            Application.getInstance().getGUILog().showMessage(
               String.format("Get branch data failed status code %d, uri: %s", statusCode,
                  EntityUtils.toString(response.getEntity())));
         }
      } catch (Exception e) {
         Application.getInstance().getGUILog().showMessage("Get branch data failed (exception) for uri: ");
      }
   }
}
