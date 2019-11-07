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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwApplicability;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifact;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactToken;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttribute;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttributeType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwBranch;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelation;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelationType;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterFactory {

   private final Pattern relTypePattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]-\\[(.*)\\]-\\[(.*)\\]");
   private final Pattern nameIdPattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");
   private final Map<Long, OwArtifactType> idToArtType = new HashMap<>();
   private final Map<Long, OwArtifactToken> idToArtToken = new HashMap<>();
   private final OwCollector collector;

   public OrcsWriterFactory(OwCollector collector) {
      this.collector = collector;
   }

   public OwArtifactToken getOrCreateToken(String value) {
      OwArtifactToken token = null;
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         Long id = Long.valueOf(matcher.group(2));
         token = idToArtToken.get(id);
         if (token == null) {
            token = new OwArtifactToken(id, matcher.group(1));
            token.setData(value);
            collector.getArtTokens().add(token);
            idToArtToken.put(id, token);
         }
      }
      return token;
   }

   public OwArtifactType getOrCreateArtifactType(String value) {
      OwArtifactType artType = null;
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         Long id = Long.valueOf(matcher.group(2));
         artType = idToArtType.get(id);
         if (artType == null) {
            artType = new OwArtifactType(id, matcher.group(1));
            artType.setData(value);
            collector.getArtTypes().add(artType);
            idToArtType.put(id, artType);
         }
      }
      return artType;
   }

   public OwAttribute getOrCreateAttribute(OwArtifact artifact, OwAttributeType attrType) {
      OwAttribute attr = null;
      for (OwAttribute fAttr : artifact.getAttributes()) {
         if (fAttr.getType().getId() > 0L && fAttr.getType().getId().equals(attrType.getId())) {
            attr = fAttr;
            break;
         } else if (fAttr.getType().getName().equals(attrType.getName())) {
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

   public OwApplicability getOrCreateApplicability(OwArtifact artifact) {
      OwApplicability app = artifact.getAppId();
      if (app == null) {
         app = new OwApplicability();
         artifact.setAppId(app);
      }
      return app;
   }

   public OwRelation createRelationType(OwRelationType relType, String value) {
      ArtifactToken token = getOrCreateToken(value);
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
         relType.setId(Long.valueOf(matcher.group(4)));
      }
   }

   public void processAttributeType(OwAttributeType attrType, String value) {
      attrType.setData(value);
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         attrType.setName(matcher.group(1));
         attrType.setId(Long.valueOf(matcher.group(2)));
      }
      // otherwise, assume the value is the attribute name
      else {
         attrType.setName(value);
      }
   }

   public OwBranch getOrCreateBranchToken(String value) {
      OwBranch branch = new OwBranch(0L, "");
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         branch.setName(matcher.group(1));
         branch.setId(Long.valueOf(matcher.group(2)));
      }
      return branch;
   }

}
