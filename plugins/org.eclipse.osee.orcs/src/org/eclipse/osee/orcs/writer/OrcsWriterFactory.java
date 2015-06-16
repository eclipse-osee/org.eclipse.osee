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
package org.eclipse.osee.orcs.writer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifact;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifactToken;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifactType;
import org.eclipse.osee.orcs.writer.model.reader.OwAttribute;
import org.eclipse.osee.orcs.writer.model.reader.OwAttributeType;
import org.eclipse.osee.orcs.writer.model.reader.OwBranch;
import org.eclipse.osee.orcs.writer.model.reader.OwCollector;
import org.eclipse.osee.orcs.writer.model.reader.OwRelation;
import org.eclipse.osee.orcs.writer.model.reader.OwRelationType;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterFactory {

   private final Pattern relTypePattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]-\\[(.*)\\]-\\[(.*)\\]");
   private final Pattern nameIdPattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");
   private final Map<Long, OwArtifactType> uuidToArtType = new HashMap<>();
   private final Map<Long, OwArtifactToken> uuidToArtToken = new HashMap<>();
   private final OwCollector collector;

   public OrcsWriterFactory(OwCollector collector) {
      this.collector = collector;
   }

   public OwArtifactToken getOrCreateToken(String value) {
      OwArtifactToken token = null;
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         Long uuid = Long.valueOf(matcher.group(2));
         token = uuidToArtToken.get(uuid);
         if (token == null) {
            token = new OwArtifactToken();
            token.setName(matcher.group(1));
            token.setUuid(uuid);
            token.setData(value);
            collector.getArtTokens().add(token);
            uuidToArtToken.put(uuid, token);
         }
      }
      return token;
   }

   public OwArtifactType getOrCreateArtifactType(String value) {
      OwArtifactType artType = null;
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         Long uuid = Long.valueOf(matcher.group(2));
         artType = uuidToArtType.get(uuid);
         if (artType == null) {
            artType = new OwArtifactType();
            artType.setName(matcher.group(1));
            artType.setUuid(uuid);
            artType.setData(value);
            collector.getArtTypes().add(artType);
            uuidToArtType.put(uuid, artType);
         }
      }
      return artType;
   }

   public OwAttribute getOrCreateAttribute(OwArtifact artifact, OwAttributeType attrType) {
      OwAttribute attr = null;
      for (OwAttribute fAttr : artifact.getAttributes()) {
         if (fAttr.getType().getUuid() == attrType.getUuid()) {
            attr = fAttr;
            break;
         }
      }
      if (attr == null) {
         attr = new OwAttribute();
         attr.setType(attrType);
         artifact.getAttributes().add(attr);
      }
      return attr;
   }

   public OwRelation createRelationType(OwRelationType relType, String value) {
      OwArtifactToken token = getOrCreateToken(value);
      OwRelation relation = new OwRelation();
      relation.setData(value);
      relation.setArtToken(token);
      relation.setType(relType);
      return relation;
   }

   public void processRelationType(OwRelationType relType, String value) {
      relType.setData(value);
      Matcher matcher = relTypePattern.matcher(value);
      if (matcher.find()) {
         relType.setName(matcher.group(1));
         relType.setSideName(matcher.group(2));
         relType.setSideA(matcher.group(3).contains("Side A"));
         relType.setUuid(Long.valueOf(matcher.group(4)));
      }
   }

   public void processAttributeType(OwAttributeType attrType, String value) {
      attrType.setData(value);
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         attrType.setName(matcher.group(1));
         attrType.setUuid(Long.valueOf(matcher.group(2)));
      }
   }

   public OwBranch getOrCreateBranchToken(String value) {
      OwBranch branch = new OwBranch();
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         branch.setName(matcher.group(1));
         branch.setUuid(Long.valueOf(matcher.group(2)));
      }
      return branch;
   }

}
