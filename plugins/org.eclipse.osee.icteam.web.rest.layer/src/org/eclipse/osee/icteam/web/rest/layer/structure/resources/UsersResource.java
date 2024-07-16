/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.rest.layer.structure.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.LdapUserDetailsWrapper;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifactsContainer;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.rest.data.write.TranferableArtifactLoader;
import org.eclipse.osee.icteam.web.rest.layer.util.CommonUtil;
import org.eclipse.osee.icteam.web.rest.layer.util.InterfaceAdapter;
import org.eclipse.osee.icteam.web.rest.layer.util.LdapUtil;
import org.eclipse.osee.icteam.web.rest.layer.util.UserUtility;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * User Resource to return User and User related queries
 * 
 * @author Ajay Chandrahasan
 */

@Path("Users")
public class UsersResource extends AbstractConfigResource {

   public UsersResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(CoreArtifactTypes.User, atsApi, orcsApi);
   }

   /**
    * Gets Ldap user for a given UserId
    * 
    * @param user {@link String} userId of the user
    * @return json {@link String} User details of a given user
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getLdapUsers")
   public String getLdapUsers(String user) {
      List<LdapUserDetailsWrapper> ldapSearchResult = LdapUtil.getLDAPSearchResult(user);
      TransferableArtifactsContainer artContainer = new TransferableArtifactsContainer();
      List<ITransferableArtifact> list = new ArrayList<ITransferableArtifact>();
      for (LdapUserDetailsWrapper ldapUserDetailsWrapper : ldapSearchResult) {
         String displayName = ldapUserDetailsWrapper.getDisplayName();
         String userId = ldapUserDetailsWrapper.getUserId();
         if (userId != null) {
            userId = userId.toLowerCase();
            String mail = ldapUserDetailsWrapper.getMail();
            TransferableArtifact ar = new TransferableArtifact();
            List<String> userIdList = new ArrayList<String>();
            userIdList.add(userId);
            List<String> displayNameList = new ArrayList<String>();
            displayNameList.add(displayName);
            List<String> mailList = new ArrayList<String>();
            mailList.add(mail);
            ar.putAttributes(CoreAttributeTypes.UserId.toString(), userIdList);
            ar.putAttributes(CoreAttributeTypes.Name.toString(), displayNameList);
            ar.putAttributes(CoreAttributeTypes.Email.toString(), mailList);
            list.add(ar);
         }
      }
      artContainer.addAll(list);
      JSONSerializer serializer = new JSONSerializer();
      String json = serializer.deepSerialize(artContainer);
      return json;
   }

   /**
    * Fetches all the users
    * 
    * @return json {@link String} List of all users details
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("allWeb")
   public String getAllUsersWeb() {
      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         ResultSet<ArtifactReadable> list =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).getResults();
         List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();
         for (ArtifactReadable artifactReadable : list) {
            TransferableArtifact ar = new TransferableArtifact();
            TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, ar);
            ResultSet<? extends AttributeReadable<Object>> attributes =
               artifactReadable.getAttributes(CoreAttributeTypes.UserId);
            if (attributes != null && attributes.size() > 0) {
               for (AttributeReadable<Object> attributeReadable : attributes) {
                  String userIDString = (String) attributeReadable.getValue();
                  ar.putAttributes(CoreAttributeTypes.UserId.getName(), Arrays.asList(userIDString));
               }
            }
            listTras.add(ar);
         }
         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         container.addAll(listTras);
         JSONSerializer serializer = new JSONSerializer();
         String json = serializer.deepSerialize(container);
         return json;
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Creates a new user
    * 
    * @param json {@link String} user details to be created
    * @return json {@link String} created user details
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("createUser")
   public String createUser(String json) {
      try {
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         ArtifactId childArtifact = null;
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         ArtifactReadable currentUser = CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser());
         UserId userId = UserId.valueOf(currentUser.getId());
         TransactionBuilder tx =
            txFactory.createTransaction(CommonUtil.getCommonBranch(orcsApi), userId, "Add New User");
         childArtifact = tx.createArtifact(CoreArtifactTypes.User, artifact.getName());
         Map<String, List<String>> attributes = artifact.getAttributes();
         Set<Entry<String, List<String>>> entrySet = attributes.entrySet();
         for (Entry<String, List<String>> entry : entrySet) {
            String type = entry.getKey();
            List<String> value = entry.getValue();
            for (String string : value) {
               tx.setSoleAttributeFromString(childArtifact,
                  AttributeTypeToken.valueOf(Long.parseLong(type), "Attribute"), string);
            }
         }
         tx.setSoleAttributeFromString(childArtifact, CoreAttributeTypes.Active, "true");
         tx.commit();
         ResultSet<ArtifactReadable> result =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(childArtifact).getResults();
         ArtifactReadable readableArtifact = result.getExactlyOne();
         TransferableArtifactsContainer artContainer = new TransferableArtifactsContainer();
         TransferableArtifact ar = new TransferableArtifact();
         ar.setName(readableArtifact.getName());
         TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(readableArtifact, ar);
         artContainer.addAll(Arrays.asList((ITransferableArtifact) ar));
         JSONSerializer serializer = new JSONSerializer();
         json = serializer.deepSerialize(artContainer);
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }
      return json;
   }

   /**
    * Fetch user details with given user Id
    * 
    * @param userntID {@link String}
    * @return userData {@link String} User details
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getUserID")
   public String getUserByGUID(String userntID) {
      String userData = null;
      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         ArtifactReadable artifactReadable = UserUtility.getUserById(orcsApi, userntID);
         Map<String, String> userDetails = new HashMap<String, String>();
         userDetails.put("guid", artifactReadable.getGuid());
         JSONSerializer serializer = new JSONSerializer();
         userData = serializer.deepSerialize(userDetails);
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }
      return userData;

   }

}

