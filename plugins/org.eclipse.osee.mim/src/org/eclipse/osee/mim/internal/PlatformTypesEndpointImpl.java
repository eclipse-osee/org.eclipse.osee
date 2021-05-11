/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.PlatformTypesEndpoint;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * A new instance of this REST endpoint is created for each REST call so this class does not require a thread-safe
 * design
 *
 * @author Luciano T. Vaglienti
 */
public class PlatformTypesEndpointImpl implements PlatformTypesEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final UserId account;

   public PlatformTypesEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account) {
      this.orcsApi = orcsApi;
      this.account = account;
      this.branch = branch;
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes() {
      List<PlatformTypeToken> pList = new LinkedList<PlatformTypeToken>();
      for (ArtifactReadable p : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfacePlatformType).getResults().getList()) {
         pList.add(new PlatformTypeToken(p));
      }
      return pList;
   }

   private PlatformTypeToken getPlatFormTypeToken(String id, BranchId branch) {
      if (Strings.isNumeric(id)) {
         ArtifactToken platformTypeArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.InterfacePlatformType).andId(
               ArtifactId.valueOf(id)).getResults().getAtMostOneOrNull();
         if (platformTypeArt != null) {
            return new PlatformTypeToken(platformTypeArt);
         }
      } else {
         ArtifactToken platformTypeArt = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
            CoreArtifactTypes.InterfacePlatformType).andNameEquals(id).getResults().getAtMostOneOrNull();
         if (platformTypeArt != null) {
            return new PlatformTypeToken(platformTypeArt);
         }
      }
      return PlatformTypeToken.SENTINEL;
   }

   @Override
   public XResultData updatePlatformType(PlatformTypeToken platformTypeToken) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Update PlatformType");
         if (createUpdatedPlatformType(platformTypeToken, "update", tx, results) != null) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   /**
    * Creates an updated platform Type, Not a rest call
    *
    * @param platformType
    * @param action
    * @param tx
    * @param results
    * @return
    */
   private ArtifactToken createUpdatedPlatformType(PlatformTypeToken platformType, String action, TransactionBuilder tx, XResultData results) {
      ArtifactToken defArt = null;
      PlatformTypeToken lplatformType = getPlatFormTypeToken(platformType.getIdString(), tx.getBranch());
      if (platformType.getErrors() != "" && (action.equals("add") || action.equals(
         "update")) && lplatformType.isInvalid()) {
         //this check is only necessary when doing a new platformType /full refresh
         results.error(platformType.getErrors());
      }
      if (action != null && action.equals("add") && lplatformType.isValid()) {
         results.error("Platform Type: " + lplatformType.getName() + " already exists.");
      }
      if (results.isErrors()) {
         return null;
      }
      if (lplatformType.isInvalid()) {
         ArtifactToken platformFolder = tx.getWriteable(CoreArtifactTokens.InterfacePlatformTypesFolder);
         if (platformFolder.isInvalid()) {
            platformFolder = getPlatformTypeFolder(branch, platformFolder);
         }
         if (platformFolder.isInvalid()) {
            results.error("PlatformTypes folder cannot be null.");
            return null;
         }
         Long artId = platformType.getId();
         if (artId == null || artId <= 0) {
            artId = Lib.generateArtifactIdAsInt();
         }
         results.setTitle(action.toUpperCase() + " Platform Type: " + String.valueOf(artId));
         List<String> idList = new LinkedList<String>();
         idList.add(String.valueOf(artId));
         results.setIds(idList);

         defArt =
            tx.createArtifact(platformFolder, CoreArtifactTypes.InterfacePlatformType, platformType.getName(), artId);

      } else {
         defArt = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(
            ArtifactId.valueOf(lplatformType.getId())).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      }
      platformType.updateValues(defArt, tx);
      //      tx.setName(defArt, platformType.getName());
      //      for (AttributeTypeToken attrToken : platformType.getAttributes().keySet()) {
      //         //         if (platformType.getAttributes().get(attrToken) != null && !platformType.getAttributes().get(attrToken).equals(
      //         //            "")) {
      //         tx.setSoleAttributeValue(defArt, attrToken, platformType.getAttributes().get(attrToken));
      //         //         }
      //      }
      return defArt;
   }

   /**
    * Updates the transaction with updated information on the platform type. Performs null check prior to setting to
    * support proper PUT operation
    *
    * @param token
    * @param platformToken
    * @param tx
    */
   private void updatePlatformTypeDefinition(ArtifactToken token, PlatformTypeToken platformToken, TransactionBuilder tx) {
      if (!platformToken.getName().equals("") && platformToken.getName() != null) {
         tx.setName(token, platformToken.getName());
      }
      if (platformToken.getInterfacePlatformTypeAnalogAccuracy() != null && !platformToken.getInterfacePlatformTypeAnalogAccuracy().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy,
            platformToken.getInterfacePlatformTypeAnalogAccuracy());
      }
      if (platformToken.getInterfacePlatformTypeBitsResolution() != null && !platformToken.getInterfacePlatformTypeBitsResolution().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeBitsResolution,
            platformToken.getInterfacePlatformTypeBitsResolution());
      }
      if (platformToken.getInterfacePlatformTypeByteSize() != null && !platformToken.getInterfacePlatformTypeByteSize().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeByteSize,
            platformToken.getInterfacePlatformTypeByteSize());
      }
      if (platformToken.getInterfacePlatformTypeCompRate() != null && !platformToken.getInterfacePlatformTypeCompRate().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeCompRate,
            platformToken.getInterfacePlatformTypeCompRate());
      }
      if (platformToken.getInterfacePlatformTypeDefaultValue() != null && !platformToken.getInterfacePlatformTypeDefaultValue().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeDefaultValue,
            platformToken.getInterfacePlatformTypeDefaultValue());
      }
      if (platformToken.getInterfacePlatformTypeEnumLiteral() != null && !platformToken.getInterfacePlatformTypeEnumLiteral().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeEnumLiteral,
            platformToken.getInterfacePlatformTypeEnumLiteral());
      }
      if (platformToken.getInterfacePlatformTypeMaxval() != null && !platformToken.getInterfacePlatformTypeMaxval().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeMaxval,
            platformToken.getInterfacePlatformTypeMaxval());
      }
      if (platformToken.getInterfacePlatformTypeMinval() != null && !platformToken.getInterfacePlatformTypeMinval().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeMinval,
            platformToken.getInterfacePlatformTypeMinval());
      }
      if (platformToken.getInterfacePlatformTypeMsbValue() != null && !platformToken.getInterfacePlatformTypeMsbValue().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeMsbValue,
            platformToken.getInterfacePlatformTypeMsbValue());
      }
      if (platformToken.getInterfacePlatformTypeUnits() != null && !platformToken.getInterfacePlatformTypeUnits().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeUnits,
            platformToken.getInterfacePlatformTypeUnits());
      }
      if (platformToken.getInterfacePlatformTypeValidRangeDescription() != null && !platformToken.getInterfacePlatformTypeValidRangeDescription().equals(
         "")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription,
            platformToken.getInterfacePlatformTypeValidRangeDescription());
      }
      if (platformToken.getInterfaceLogicalType() != null && !platformToken.getInterfaceLogicalType().equals("")) {
         tx.setSoleAttributeValue(token, CoreAttributeTypes.InterfaceLogicalType,
            platformToken.getInterfaceLogicalType());
      }
   }

   /**
    * Gets the platform types folder
    *
    * @param branch
    * @param folder
    * @return
    */
   private ArtifactToken getPlatformTypeFolder(BranchId branch, ArtifactToken folder) {
      if (folder.isInvalid()) {
         folder = orcsApi.getQueryFactory().fromBranch(branch).andId(
            CoreArtifactTokens.InterfacePlatformTypesFolder).getArtifactOrSentinal();
      }
      return folder;
   }

   @Override
   public XResultData createPlatformType(PlatformTypeToken platformTypeToken) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Create PlatformType");
         if (createUpdatedPlatformType(platformTypeToken, "add", tx, results) != null) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public PlatformTypeToken getPlatformType(String typeId) {
      return new PlatformTypeToken(
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.InterfacePlatformType).andId(
            ArtifactId.valueOf(typeId)).getResults().getAtMostOneOrNull());
   }

   @Override
   public XResultData removePlatformType(String typeId) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         ArtifactToken art =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.InterfacePlatformType).andId(
               ArtifactId.valueOf(typeId)).getResults().getAtMostOneOrNull();
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Delete Platform Type");
         tx.deleteArtifact(art);
         tx.commit();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData createPlatformType(Object platformTypeToken) {
      return null;
   }

   @Override
   public XResultData patchPlatformType(PlatformTypeToken platformTypeToken) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Create PlatformType");
         if (createUpdatedPlatformType(platformTypeToken, "edit", tx, results) != null) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

}