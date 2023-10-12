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
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Ryan T. Baldwin
 */
public class VersionInformationToken extends ArtifactAccessorResult {

   public static final VersionInformationToken SENTINEL = new VersionInformationToken();

   private String versionInfo;
   private String versionUnit;
   private boolean underTest;

   public VersionInformationToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public VersionInformationToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setVersionInfo(art.getSoleAttributeAsString(CoreAttributeTypes.VersionInfo, ""));
      this.setVersionUnit(art.getSoleAttributeAsString(CoreAttributeTypes.VersionUnit, ""));
      this.setUnderTest(art.getSoleAttributeValue(CoreAttributeTypes.UnderTest, false));
   }

   public VersionInformationToken(Long id, String name) {
      super(id, name);
      this.setVersionInfo("");
      this.setVersionUnit("");
      this.setUnderTest(false);
   }

   public VersionInformationToken() {
      super();
   }

   public String getVersionInfo() {
      return versionInfo;
   }

   public void setVersionInfo(String versionInfo) {
      this.versionInfo = versionInfo;
   }

   public String getVersionUnit() {
      return versionUnit;
   }

   public void setVersionUnit(String versionUnit) {
      this.versionUnit = versionUnit;
   }

   public boolean isUnderTest() {
      return underTest;
   }

   public void setUnderTest(boolean underTest) {
      this.underTest = underTest;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.VersionInfo, this.getVersionInfo());
      values.put(CoreAttributeTypes.VersionUnit, this.getVersionUnit());
      values.put(CoreAttributeTypes.UnderTest, Boolean.toString(this.isUnderTest()));

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.VersionInformation.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.VersionInformation.getValidAttributeTypes()) {
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
