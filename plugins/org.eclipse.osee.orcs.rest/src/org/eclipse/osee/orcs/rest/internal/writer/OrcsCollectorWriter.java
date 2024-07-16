/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.internal.writer;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifact;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttribute;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttributeType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelation;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelationType;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class OrcsCollectorWriter {

   private final OwCollector collector;
   private final OrcsApi orcsApi;
   private Map<Long, ArtifactId> idToArtifact;
   private TransactionBuilder transaction;
   private final BranchId branch;
   private UserId user;
   private final XResultData results;

   public OrcsCollectorWriter(OrcsApi orcsApi, OwCollector collector, XResultData results) {
      this.orcsApi = orcsApi;
      this.collector = collector;
      this.results = results;
      this.branch = BranchId.valueOf(collector.getBranch().getId());
      idToArtifact = new HashMap<>();
   }

   public XResultData run() {
      processCreate(results);
      processUpdate(results);
      processDelete(results);
      getTransaction().commit();
      results.log("Complete");
      return results;
   }

   private void processDelete(XResultData results) {
      for (ArtifactToken owArtifact : collector.getDelete()) {
         ArtifactReadable artifact =
            orcsApi.getQueryFactory().fromBranch(branch).andId(owArtifact).getResults().getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);

         if (artifact.isInvalid()) {
            results.warningf("Delete Artifact Token %s does not exist in database.  Skipping", owArtifact);
         } else {
            getTransaction().deleteArtifact(artifact);
            results.logf("Deleted artifact %s", owArtifact);
         }
      }
   }

   private void processUpdate(XResultData results) {
      for (OwArtifact owArtifact : collector.getUpdate()) {
         ArtifactReadable artifact =
            orcsApi.getQueryFactory().fromBranch(branch).andUuid(owArtifact.getId()).getResults().getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);

         if (artifact.isInvalid()) {
            throw new OseeArgumentException("Artifact not found for OwArtifact %s", owArtifact);
         }

         if (Strings.isValid(owArtifact.getName()) && !owArtifact.getName().equals(artifact.getName())) {
            getTransaction().setName(artifact, owArtifact.getName());
            logChange(artifact, CoreAttributeTypes.Name, artifact.getName(), owArtifact.getName());
         }

         try {
            createMissingRelations(owArtifact.getRelations(), artifact, results);
         } catch (Exception ex) {
            throw new OseeWrappedException(ex, "Exception processing relations for [%s]", owArtifact);
         }
         if (owArtifact.getAppId() != null) {
            try {
               ApplicabilityId appId = ApplicabilityId.BASE;
               HashMap<Long, ApplicabilityToken> tokenMap =
                  orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch);
               Collection<ApplicabilityToken> tokens = tokenMap.values();
               for (ApplicabilityToken applicToken : tokens) {
                  if (applicToken.getName().equals(owArtifact.getAppId().getValue())) {
                     appId = ApplicabilityId.valueOf(applicToken.getId());
                     break;
                  }
               }
               if (appId.isInvalid()) {
                  results.warningf("Couldn't find applicability " + owArtifact.getAppId().getValue() + " on branch %s",
                     branch.getIdString());
               } else {
                  ApplicabilityId currApp = artifact.getApplicability();
                  if (!currApp.equals(appId)) {
                     getTransaction().setApplicability(artifact, appId);
                  }
               }

            } catch (Exception ex) {
               throw new OseeWrappedException(ex, "Exception processing applicability for [%s]", owArtifact);
            }
         }
         try {
            for (OwAttribute owAttribute : owArtifact.getAttributes()) {
               AttributeTypeToken attrType = getAttributeType(orcsApi.tokenService(), owAttribute.getType());

               if (artifact.getAttributeCount(attrType) <= 1 && owAttribute.getValues().size() <= 1) {
                  String currValue = artifact.getSoleAttributeAsString(attrType, null);

                  String newValue = null;
                  if (owAttribute.getValues().size() == 1) {
                     Object object = owAttribute.getValues().iterator().next();
                     if (object != null) {
                        newValue = owAttribute.getValues().iterator().next().toString();
                     }
                  }

                  // handle delete attribute case first
                  if (Strings.isValid(currValue) && newValue == null) {
                     logChange(artifact, attrType, currValue, newValue);
                     getTransaction().deleteAttributes(artifact, attrType);
                  } else if (attrType.isBoolean()) {
                     Boolean currVal = getBoolean(currValue);
                     Boolean newVal = getBoolean(newValue);
                     if (currVal == null || !currVal.equals(newVal)) {
                        logChange(artifact, attrType, currValue, newValue);
                        getTransaction().setSoleAttributeValue(artifact, attrType, newVal);
                     }
                  } else if (attrType.isDouble()) {
                     try {
                        Double currVal = getDouble(currValue);
                        Double newVal = getDouble(newValue);
                        if (currVal == null || !currVal.equals(newVal)) {
                           logChange(artifact, attrType, currValue, newValue);
                           getTransaction().setSoleAttributeValue(artifact, attrType, newVal);
                        }
                     } catch (Exception ex) {
                        throw new OseeArgumentException("Exception processing Double for OwAttribute %s Exception %s",
                           owAttribute, ex);
                     }
                  } else if (attrType.isInteger()) {
                     try {
                        Integer currVal = getInteger(currValue);
                        Integer newVal = getInteger(newValue);
                        if (currVal == null || !currVal.equals(newVal)) {
                           logChange(artifact, attrType, currValue, newValue);
                           getTransaction().setSoleAttributeValue(artifact, attrType, newVal);
                        }
                     } catch (Exception ex) {
                        throw new OseeArgumentException("Exception processing Integer for OwAttribute %s Exception %s",
                           owAttribute, ex);
                     }
                  } else if (attrType.isDate()) {
                     try {
                        Date currVal = artifact.getSoleAttributeValue(attrType, null);
                        Date newVal = getDate(newValue);
                        if (currVal == null || currVal.compareTo(newVal) != 0) {
                           logChange(artifact, attrType, DateUtil.getMMDDYYHHMM(currVal),
                              DateUtil.getMMDDYYHHMM(newVal));
                           TransactionBuilder tx = getTransaction();
                           tx.setSoleAttributeValue(artifact, attrType, newVal);
                        }
                     } catch (Exception ex) {
                        throw new OseeArgumentException("Exception processing Integer for OwAttribute %s Exception %s",
                           owAttribute, ex);
                     }
                  } else if (attrType.equals(CoreAttributeTypes.WordTemplateContent)) {
                     if (!newValue.contains("<w:p><w:r><w:t>")) {
                        newValue = "<w:p><w:r><w:t>" + AXml.textToXml(newValue) + "</w:t></w:r></w:p>";
                     }
                     if (currValue == null && newValue != null || currValue != null && !currValue.equals(newValue)) {
                        logChange(artifact, attrType, currValue, newValue);
                        getTransaction().setSoleAttributeValue(artifact, attrType, newValue);
                     }

                  } else if (currValue == null && newValue != null || currValue != null && !currValue.equals(
                     newValue)) {
                     logChange(artifact, attrType, currValue, newValue);
                     getTransaction().setSoleAttributeValue(artifact, attrType, newValue);
                  }
               } else if (owAttribute.getValues().size() > 1 && artifact.getArtifactType().getMax(attrType) > 1) {
                  if (attrType.isDate()) {
                     throw new OseeArgumentException(
                        "Date attributes not supported for multi-value set for OwAttribute %s Exception %s",
                        owAttribute);
                  }
                  List<String> values = new LinkedList<>();
                  for (Object obj : owAttribute.getValues()) {
                     values.add(obj.toString());
                  }
                  getTransaction().setAttributesFromStrings(artifact, attrType, values);
                  logChange(artifact, attrType, artifact.getAttributeValues(attrType).toString(), values.toString());
               }
            }
         } catch (Exception ex) {
            throw new OseeWrappedException(ex, "Exception processing attributes for [%s]", owArtifact);
         }
      }
   }

   private void logChange(ArtifactReadable artifact, AttributeTypeToken attrType, String currValue, String newValue) {
      results.log(String.format("Attribute Updated: Current [%s], New [%s] for attr type [%s] and artifact %s",
         currValue, newValue, attrType, artifact.toStringWithId()));
   }

   private Integer getInteger(String value) {
      Integer result = null;
      if (Strings.isValid(value)) {
         result = Integer.valueOf(value);
      }
      return result;
   }

   private Double getDouble(String value) {
      Double result = null;
      if (Strings.isValid(value)) {
         result = Double.valueOf(value);
      }
      return result;
   }

   private Boolean getBoolean(String value) {
      if (Strings.isValid(value)) {
         if (value.toLowerCase().equals("true")) {
            return true;
         } else if (value.toLowerCase().equals("false")) {
            return false;
         } else if (value.equals("1")) {
            return true;
         } else if (value.equals("0")) {
            return false;
         }
      }
      return null;
   }

   protected static AttributeTypeToken getAttributeType(OrcsTokenService tokenService, OwAttributeType attributeType) {
      AttributeTypeToken attributeTypeToken = AttributeTypeToken.SENTINEL;
      if (attributeType != null && attributeType.isValid()) {
         attributeTypeToken = tokenService.getAttributeTypeOrSentinel(attributeType.getId());
      }
      return attributeTypeToken;
   }

   private void processCreate(XResultData results) {
      for (OwArtifact owArtifact : collector.getCreate()) {
         OwArtifactType owArtType = owArtifact.getType();
         ArtifactTypeToken artType = orcsApi.tokenService().getArtifactTypeOrCreate(owArtType.getId());

         long artifactId = owArtifact.getId();
         String name = owArtifact.getName();
         ArtifactToken artifact;
         ApplicabilityId appId = ApplicabilityId.BASE;
         if (owArtifact.getAppId() != null) {
            try {

               HashMap<Long, ApplicabilityToken> tokenMap =
                  orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch);
               Collection<ApplicabilityToken> tokens = tokenMap.values();
               for (ApplicabilityToken applicToken : tokens) {
                  if (applicToken.getName().equals(owArtifact.getAppId().getValue())) {
                     appId = ApplicabilityId.valueOf(applicToken.getId());
                     break;
                  }
               }
               if (appId == null) {
                  results.warningf("Couldn't find applicability " + owArtifact.getAppId().getValue() + " on branch %s",
                     branch.getIdString());
                  appId = ApplicabilityId.BASE;
               }

            } catch (Exception ex) {
               throw new OseeWrappedException(ex, "Exception processing applicability for [%s]", owArtifact);
            }
         }

         if (artifactId < 1) {
            artifact = getTransaction().createArtifact(artType, name, appId);
         } else {
            artifact = getTransaction().createArtifact(artType, name, ArtifactId.valueOf(artifactId), appId);
         }

         if (idToArtifact == null) {
            idToArtifact = new HashMap<>();
         }
         idToArtifact.put(artifactId, artifact);

         try {
            createAttributes(owArtifact, artifact, results);
         } catch (Exception ex) {
            throw new OseeWrappedException(ex, "Exception creating attributes for [%s]", owArtifact);
         }

         try {
            createMissingRelations(owArtifact, artifact, results);
         } catch (Exception ex) {
            throw new OseeWrappedException(ex, "Exception creating relations for [%s]", owArtifact);
         }

      }
   }

   private void createMissingRelations(OwArtifact owArtifact, ArtifactId artifact, XResultData results) {
      createMissingRelations(owArtifact.getRelations(), artifact, results);
   }

   private void createMissingRelations(List<OwRelation> relations, ArtifactId artifact, XResultData results) {
      OrcsTokenService tokenService = orcsApi.tokenService();
      for (OwRelation relation : relations) {
         OwRelationType owRelType = relation.getType();
         RelationTypeToken relType = tokenService.getRelationTypeOrCreate(owRelType.getId());

         ArtifactToken artToken = relation.getArtToken();

         ArtifactReadable otherArtifact = null;

         if (idToArtifact.containsKey(artToken.getId())) {
            otherArtifact = (ArtifactReadable) idToArtifact.get(artToken.getId());
         } else {
            otherArtifact = orcsApi.getQueryFactory().fromBranch(branch).andId(artToken).getResults().getExactlyOne();
            idToArtifact.put(artToken.getId(), otherArtifact);
         }

         RelationTypeSide relTypeSide =
            RelationTypeSide.create(relType, RelationSide.valueOf(relation.getType().isSideA()));
         if (relation.getType().isSideA()) {
            if (!otherArtifact.areRelated(relTypeSide, (ArtifactReadable) artifact)) {
               getTransaction().relate(otherArtifact, relType, artifact, RelationSorter.USER_DEFINED);
            }
         } else {
            if (!otherArtifact.areRelated(relTypeSide, (ArtifactReadable) artifact)) {
               getTransaction().relate(artifact, relType, otherArtifact, RelationSorter.USER_DEFINED);
            }
         }
      }
   }

   private void createAttributes(OwArtifact owArtifact, ArtifactToken artifact, XResultData results) {
      for (OwAttribute owAttribute : owArtifact.getAttributes()) {
         if (CoreAttributeTypes.Name.notEqual(owAttribute.getType().getId())) {
            OwAttributeType owAttrType = owAttribute.getType();
            AttributeTypeToken attrType = getAttributeType(orcsApi.tokenService(), owAttrType);

            List<Object> values = owAttribute.getValues();
            for (Object value : values) {
               String valueOf = String.valueOf(value);
               if (Strings.isValid(valueOf) && !valueOf.equals("null")) {
                  if (attrType.isDouble()) {
                     getTransaction().setSoleAttributeValue(artifact, attrType, Double.valueOf((String) value));
                  } else if (attrType.isInteger()) {
                     getTransaction().setSoleAttributeValue(artifact, attrType, Integer.valueOf((String) value));
                  } else if (attrType.isBoolean()) {
                     Boolean set = getBoolean((String) value);
                     if (set != null) {
                        getTransaction().setSoleAttributeValue(artifact, attrType, set);
                     }
                  } else if (attrType.isDate()) {
                     Date date = getDate(value);
                     if (date != null) {
                        getTransaction().setSoleAttributeValue(artifact, attrType, date);
                     } else {
                        throw new OseeArgumentException("Unexpected date format [%s]", value);
                     }
                  } else if (attrType.equals(CoreAttributeTypes.WordTemplateContent)) {

                     if (!valueOf.contains("<w:p><w:r><w:t>")) {

                        valueOf = "<w:p><w:r><w:t>" + AXml.textToXml(valueOf) + "</w:t></w:r></w:p>";
                     }
                     getTransaction().createAttribute(artifact, attrType, valueOf);

                  } else if (artifact.getArtifactType().getMax(attrType) == 1) {
                     getTransaction().setSoleAttributeValue(artifact, attrType, value);
                  } else {
                     getTransaction().createAttribute(artifact, attrType, value);
                  }
               }
            }
         }
      }
   }

   private Date getDate(Object value) {
      Date date = null;
      boolean resolved = false;
      if (Strings.isNumeric((String) value)) {
         date = new Date(Long.valueOf((String) value));
      } else {
         try {
            date = DateUtil.getDate(DateUtil.MMDDYY, (String) value);
            resolved = true;
         } catch (Exception ex) {
            // do nothing
         }
         if (date == null) {
            try {
               date = DateUtil.getDate("MM/dd/yy", (String) value);
               resolved = true;
            } catch (Exception ex) {
               // do nothing
            }
         }
         if (date == null) {
            try {
               date = DateUtil.getDate(DateUtil.MMDDYYHHMM, (String) value);
               resolved = true;
            } catch (Exception ex) {
               // do nothing
            }
         }
         if (date == null) {
            try {
               Calendar calendar = jakarta.xml.bind.DatatypeConverter.parseDateTime((String) value);
               date = calendar.getTime();
               resolved = true;
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      if (Strings.isValid((String) value) && !resolved) {
         throw new OseeArgumentException("Date format [%s] not supported.", value);
      }
      return date;
   }

   public TransactionBuilder getTransaction() {
      if (transaction == null) {
         transaction = orcsApi.getTransactionFactory().createTransaction(branch, collector.getPersistComment());
      }
      return transaction;
   }
}