/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Ryan T. Baldwin
 */
public class InfoGroupToken extends ArtifactAccessorResult {

   public static final InfoGroupToken SENTINEL = new InfoGroupToken();

   private String groupType;
   private List<InfoToken> info;

   public InfoGroupToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InfoGroupToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setGroupType(art.getSoleAttributeAsString(CoreAttributeTypes.GroupType, "InfoGroup"));
      this.setInfo(art.getRelated(CoreRelationTypes.InfoGroupToInfo_Info).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InfoToken(a)).collect(Collectors.toList()));
   }

   public InfoGroupToken(Long id, String name) {
      super(id, name);
      this.setGroupType("InfoGroup");
      this.setInfo(new LinkedList<>());
   }

   public InfoGroupToken() {
      super();
   }

   public String getGroupType() {
      return groupType;
   }

   public void setGroupType(String groupType) {
      this.groupType = groupType;
   }

   public List<InfoToken> getInfo() {
      return info;
   }

   public void setInfo(List<InfoToken> info) {
      this.info = info;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.GroupType, this.getGroupType());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.InfoGroup.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InfoGroup.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);

      art.setkey(key);

      return art;
   }

}
