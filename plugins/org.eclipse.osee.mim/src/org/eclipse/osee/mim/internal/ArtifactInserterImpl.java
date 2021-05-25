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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.ArtifactInserter;
import org.eclipse.osee.mim.annotations.OseeArtifactAttribute;
import org.eclipse.osee.mim.annotations.OseeArtifactRequiredAttribute;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Luciano T. Vaglienti
 * @param <T> Class to use for storing/presenting artifact information
 */
public class ArtifactInserterImpl<T> implements ArtifactInserter<T> {

   private final OrcsApi orcsApi;
   private ArtifactTypeToken artifactType;
   private String objectTypeToInsert;
   private final ArtifactAccessor<T> accessor;
   ArtifactToken folder;

   public ArtifactInserterImpl(ArtifactTypeToken artifactType, OrcsApi orcsApi, String objectTypeToInsert, ArtifactAccessor<T> accessor, ArtifactToken folder) {
      this.setArtifactType(artifactType);
      this.setObjectTypeToInsert(objectTypeToInsert);
      this.orcsApi = orcsApi;
      this.accessor = accessor;
      this.folder = folder;
   }

   @Override
   public XResultData addArtifact(T newArtifact, UserId account, BranchId branch) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Create " + this.getObjectTypeToInsert());
         if (createAndValidateNewArtifact(newArtifact, branch, tx, results)) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   /**
    * @return the artifactType
    */
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   /**
    * @param artifactType the artifactType to set
    */
   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   /**
    * @return the objectTypeToInsert
    */
   public String getObjectTypeToInsert() {
      return objectTypeToInsert;
   }

   /**
    * @param objectTypeToInsert the objectTypeToInsert to set
    */
   public void setObjectTypeToInsert(String objectTypeToInsert) {
      this.objectTypeToInsert = objectTypeToInsert;
   }

   private boolean createAndValidateNewArtifact(T newArtifact, BranchId branch, TransactionBuilder tx, XResultData results) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      ArtifactToken defArt = ArtifactToken.SENTINEL;
      T foundArtifact = accessor.get(tx.getBranch(),
         ArtifactId.valueOf((String) newArtifact.getClass().getMethod("getIdString").invoke(newArtifact)),
         (Class<T>) newArtifact.getClass());
      if (!this.validateFieldsExists(newArtifact).isEmpty()) {
         results.error(this.validateFieldsExists(newArtifact));
         return false;
      }
      if ((boolean) foundArtifact.getClass().getMethod("isValid").invoke(foundArtifact)) {
         results.error(newArtifact.getClass().getCanonicalName() + " already exists.");
         return false;
      }
      ArtifactToken writeFolder = tx.getWriteable(folder);
      if (writeFolder.isInvalid()) {
         writeFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(folder).asArtifactOrSentinel();
      }
      if (writeFolder.isInvalid()) {
         results.error("Folder cannot be null");
         return false;
      }
      defArt = tx.createArtifact(writeFolder, artifactType,
         (String) newArtifact.getClass().getMethod("getName").invoke(newArtifact));
      results.setTitle("Add " + this.objectTypeToInsert + " " + defArt.getIdString());
      List<String> idList = new LinkedList<String>();
      idList.add(defArt.getIdString());
      results.setIds(idList);

      for (Field field : getAllFields(new LinkedList<Field>(), newArtifact.getClass())) {
         field.setAccessible(true);
         if (field.isAnnotationPresent(OseeArtifactAttribute.class)) {
            Method getter = getGetter(newArtifact.getClass(), field.getName());
            if (getter != null && getter.invoke(newArtifact) != null) {
               tx.setSoleAttributeValue(defArt,
                  AttributeTypeToken.valueOf(
                     String.valueOf(field.getDeclaredAnnotation(OseeArtifactAttribute.class).attributeId())),
                  getter.invoke(newArtifact));
            } else if (field.get(newArtifact) != null) {
               tx.setSoleAttributeValue(defArt,
                  AttributeTypeToken.valueOf(
                     String.valueOf(field.getDeclaredAnnotation(OseeArtifactAttribute.class).attributeId())),
                  field.get(newArtifact));
            }
         }
      }
      return true;

   }

   private String validateFieldsExists(T newArtifact) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      String results = "";
      for (Field field : getAllFields(new LinkedList<Field>(), newArtifact.getClass())) {
         field.setAccessible(true);
         if (field.isAnnotationPresent(OseeArtifactRequiredAttribute.class) && field.isAnnotationPresent(
            OseeArtifactAttribute.class)) {
            if (getGetter(newArtifact.getClass(),
               field.getName()) != null && (getGetter(newArtifact.getClass(), field.getName()).invoke(
                  newArtifact) == null || getGetter(newArtifact.getClass(), field.getName()).invoke(newArtifact).equals(
                     ""))) {
               results = field.getName() + " must be set to a value";
            }
         }
      }
      return results;

   }

   @Override
   public XResultData relateArtifact(ArtifactId artifactToRelate, ArtifactId artifactToRelateTo, RelationTypeToken relation, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Relate " + this.getObjectTypeToInsert());
         tx.relate(artifactToRelateTo, relation, artifactToRelate);
         tx.commit();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData replaceArtifact(T newArtifact, UserId account, BranchId branch) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Replace " + this.getObjectTypeToInsert());
         if (this.replaceExistingArtifact(newArtifact, branch, tx, results)) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData patchArtifact(T newArtifact, UserId account, BranchId branch) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Replace " + this.getObjectTypeToInsert());
         if (this.partialUpdateArtifact(newArtifact, branch, tx, results)) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData unrelateArtifact(ArtifactId artifactToUnRelate, ArtifactId artifactToUnRelateFrom, RelationTypeToken relation, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Relate " + this.getObjectTypeToInsert());
         tx.unrelate(artifactToUnRelateFrom, relation, artifactToUnRelate);
         tx.commit();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData removeArtifact(ArtifactId artifactToRemove, UserId account, BranchId branch) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Delete Artifact");
         tx.deleteArtifact(orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(artifactType).andId(
            artifactToRemove).asArtifact());
         tx.commit();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return null;
   }

   private boolean replaceExistingArtifact(T newArtifact, BranchId branch, TransactionBuilder tx, XResultData results) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      ArtifactToken defArt = ArtifactToken.SENTINEL;
      T foundArtifact = accessor.get(tx.getBranch(),
         ArtifactId.valueOf((String) newArtifact.getClass().getMethod("getIdString").invoke(newArtifact)),
         (Class<T>) newArtifact.getClass());
      if (!this.checkIfIdExists(newArtifact)) {
         results.error("Artifact must contain an id");
      }
      if (!this.validateFieldsExists(newArtifact).isEmpty()) {
         results.error(this.validateFieldsExists(newArtifact));
         return false;
      }
      ArtifactToken writeFolder = tx.getWriteable(folder);
      if (writeFolder.isInvalid()) {
         writeFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(folder).asArtifactOrSentinel();
      }
      if (writeFolder.isInvalid()) {
         results.error("Folder cannot be null");
         return false;
      }
      defArt = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(ArtifactId.valueOf(
         (String) foundArtifact.getClass().getMethod("getIdString").invoke(foundArtifact))).asArtifactOrSentinel();
      results.setTitle("Replace " + this.objectTypeToInsert + " " + defArt.getIdString());
      List<String> idList = new LinkedList<String>();
      idList.add(defArt.getIdString());
      results.setIds(idList);
      for (Field field : getAllFields(new LinkedList<Field>(), newArtifact.getClass())) {
         field.setAccessible(true);
         if (field.isAnnotationPresent(OseeArtifactAttribute.class)) {
            Method getter = getGetter(newArtifact.getClass(), field.getName());
            if (getter != null && getter.invoke(newArtifact) != null) {
               tx.setSoleAttributeValue(defArt,
                  AttributeTypeToken.valueOf(
                     String.valueOf(field.getDeclaredAnnotation(OseeArtifactAttribute.class).attributeId())),
                  getter.invoke(newArtifact));
            }
         }
      }
      return true;
   }

   private boolean partialUpdateArtifact(T newArtifact, BranchId branch, TransactionBuilder tx, XResultData results) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      ArtifactToken defArt = ArtifactToken.SENTINEL;
      T foundArtifact = accessor.get(tx.getBranch(),
         ArtifactId.valueOf((String) newArtifact.getClass().getMethod("getIdString").invoke(newArtifact)),
         (Class<T>) newArtifact.getClass());
      if (!this.checkIfIdExists(newArtifact)) {
         results.error("Artifact must contain an id");
      }
      ArtifactToken writeFolder = tx.getWriteable(folder);
      if (writeFolder.isInvalid()) {
         writeFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(folder).asArtifactOrSentinel();
      }
      if (writeFolder.isInvalid()) {
         results.error("Folder cannot be null");
         return false;
      }
      defArt = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(ArtifactId.valueOf(
         (String) foundArtifact.getClass().getMethod("getIdString").invoke(foundArtifact))).asArtifactOrSentinel();
      results.setTitle("Edit " + this.objectTypeToInsert + " " + defArt.getIdString());
      List<String> idList = new LinkedList<String>();
      idList.add(defArt.getIdString());
      results.setIds(idList);
      for (Field field : getAllFields(new LinkedList<Field>(), newArtifact.getClass())) {
         field.setAccessible(true);
         if (field.isAnnotationPresent(OseeArtifactAttribute.class)) {
            Method getter = getGetter(newArtifact.getClass(), field.getName());
            if (getter != null && getter.invoke(newArtifact) != null && getter.invoke(newArtifact) != "") {
               tx.setSoleAttributeValue(defArt,
                  AttributeTypeToken.valueOf(
                     String.valueOf(field.getDeclaredAnnotation(OseeArtifactAttribute.class).attributeId())),
                  getter.invoke(newArtifact));
            }
         }
      }
      return true;
   }

   private Method getGetter(Class<?> type, String name) {
      for (Method method : type.getMethods()) {
         if (method.getName().startsWith("get") && method.getParameterTypes().length == 0 && !void.class.equals(
            method.getReturnType())) {
            //is a getter
            if (method.getName().endsWith(name)) {
               return method;
            }
         }
      }
      return null;

   }

   private boolean checkIfIdExists(T newArtifact) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      Long Id = (Long) getGetter(newArtifact.getClass(), "Id").invoke(newArtifact);
      if (Id.equals(null) || Id <= 0L) {
         return false;
      }
      return true;
   }

   List<Field> getAllFields(List<Field> fields, Class<?> type) {
      fields.addAll(Arrays.asList(type.getDeclaredFields()));

      if (type.getSuperclass() != null) {
         getAllFields(fields, type.getSuperclass());
      }

      return fields;
   }

}
