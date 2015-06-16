/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.writer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifact;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifactToken;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifactType;
import org.eclipse.osee.orcs.writer.model.reader.OwAttribute;
import org.eclipse.osee.orcs.writer.model.reader.OwAttributeType;
import org.eclipse.osee.orcs.writer.model.reader.OwCollector;
import org.eclipse.osee.orcs.writer.model.reader.OwRelation;
import org.eclipse.osee.orcs.writer.model.reader.OwRelationType;

/**
 * @author Donald G. Dunne
 */
public class OrcsCollectorWriter {

   private final OwCollector collector;
   private final OrcsApi orcsApi;
   private Map<Long, ArtifactId> uuidToArtifact;
   private TransactionBuilder transaction;
   private IOseeBranch branch;
   private ArtifactReadable user;

   public OrcsCollectorWriter(OrcsApi orcsApi, OwCollector collector) {
      this.orcsApi = orcsApi;
      this.collector = collector;
      uuidToArtifact = new HashMap<>();
   }

   public XResultData run() {
      XResultData results = new XResultData(false);
      processCreate(results);
      return results;
   }

   private void processCreate(XResultData results) {
      for (OwArtifact owArtifact : collector.getCreate()) {
         OwArtifactType owArtType = owArtifact.getType();
         IArtifactType artType = orcsApi.getOrcsTypes().getArtifactTypes().getByUuid(owArtType.getUuid());

         long artifactUuid = owArtifact.getUuid();
         if (artifactUuid > 0L) {
            if (uuidToArtifact == null) {
               uuidToArtifact = new HashMap<>();
            }
         } else {
            artifactUuid = Lib.generateArtifactIdAsInt();
         }
         String name = owArtifact.getName();
         ArtifactId artifact = getTransaction().createArtifact(artType, name, GUID.create(), artifactUuid);

         uuidToArtifact.put(artifactUuid, artifact);

         createAttributes(owArtifact, artifact, results);

         createRelations(owArtifact, artifact, results);
      }
      getTransaction().commit();
   }

   private void createRelations(OwArtifact owArtifact, ArtifactId artifact, XResultData results) {
      for (OwRelation relation : owArtifact.getRelations()) {
         OwRelationType owRelType = relation.getType();
         IRelationType relType = orcsApi.getOrcsTypes().getRelationTypes().getByUuid(owRelType.getUuid());

         OwArtifactToken artToken = relation.getArtToken();
         long branchUuid = collector.getBranch().getUuid();
         ArtifactReadable otherArtifact = null;

         if (uuidToArtifact.containsKey(artToken.getUuid())) {
            otherArtifact = (ArtifactReadable) uuidToArtifact.get(artToken.getUuid());
         } else {
            otherArtifact =
               orcsApi.getQueryFactory().fromBranch(branchUuid).andUuid(artToken.getUuid()).getResults().getExactlyOne();
         }
         if (relation.getType().isSideA()) {
            getTransaction().relate(otherArtifact, relType, artifact);
         } else {
            getTransaction().relate(artifact, relType, otherArtifact);
         }
      }
   }

   private void createAttributes(OwArtifact owArtifact, ArtifactId artifact, XResultData results) {
      for (OwAttribute owAttribute : owArtifact.getAttributes()) {
         if (!CoreAttributeTypes.Name.getGuid().equals(owAttribute.getType().getUuid())) {
            OwAttributeType owAttrType = owAttribute.getType();
            IAttributeType attrType = orcsApi.getOrcsTypes().getAttributeTypes().getByUuid(owAttrType.getUuid());
            List<Object> values = owAttribute.getValues();
            for (Object value : values) {
               if (orcsApi.getOrcsTypes().getAttributeTypes().isFloatingType(attrType)) {
                  getTransaction().setSoleAttributeValue(artifact, attrType, Double.valueOf((String) value));
               } else if (orcsApi.getOrcsTypes().getAttributeTypes().isIntegerType(attrType)) {
                  getTransaction().setSoleAttributeValue(artifact, attrType, Integer.valueOf((String) value));
               } else if (orcsApi.getOrcsTypes().getAttributeTypes().isBooleanType(attrType)) {
                  boolean set = ((String) value).toLowerCase().equals("true");
                  getTransaction().setSoleAttributeValue(artifact, attrType, set);
               } else if (orcsApi.getOrcsTypes().getAttributeTypes().isDateType(attrType)) {
                  Date date = null;
                  if (Strings.isNumeric((String) value)) {
                     date = new Date(Long.valueOf((String) value));
                  } else {
                     try {
                        date = DateUtil.getDate(DateUtil.MMDDYY, (String) value);
                     } catch (Exception ex) {
                        // do nothing
                     }
                     try {
                        date = DateUtil.getDate("MM/dd/yy", (String) value);
                     } catch (Exception ex) {
                        // do nothing
                     }
                     try {
                        Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime((String) value);
                        date = calendar.getTime();
                     } catch (Exception ex) {
                        // do nothing
                     }
                  }
                  if (date != null) {
                     getTransaction().setSoleAttributeValue(artifact, attrType, date);
                  } else {
                     throw new OseeArgumentException("Unexpected date format [%s]", value);
                  }
               } else if (orcsApi.getOrcsTypes().getAttributeTypes().getMaxOccurrences(attrType) == 1) {
                  getTransaction().setSoleAttributeValue(artifact, attrType, value);
               } else {
                  getTransaction().createAttribute(artifact, attrType, value);
               }
            }
         }
      }
   }

   private IOseeBranch getBranch() {
      if (branch == null) {
         branch =
            orcsApi.getQueryFactory().branchQuery().andUuids(collector.getBranch().getUuid()).getResults().getAtMostOneOrNull();
      }
      return branch;
   }

   public TransactionBuilder getTransaction() throws OseeCoreException {
      if (transaction == null) {
         transaction =
            orcsApi.getTransactionFactory().createTransaction(getBranch(), getUser(), collector.getPersistComment());
      }
      return transaction;
   }

   private ArtifactReadable getUser() {
      if (user == null) {
         user =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.UserId,
               collector.getAsUserId()).getResults().getExactlyOne();
      }
      return user;
   }

}
