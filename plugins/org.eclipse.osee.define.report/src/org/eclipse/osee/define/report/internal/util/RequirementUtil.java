/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal.util;

import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Megumi Telles
 */
public class RequirementUtil {

   private static final String REMOVESPACENCOMMA = "[,\\s]";
   private static final String REMOVEBRACKETS = "\\[|\\]";

   public RequirementUtil() {
      // utility class
   }

   public static ResultSet<ArtifactReadable> getHigherLevelTrace(ArtifactReadable readable) {
      Conditions.checkNotNull(readable, "artifact readable for higher level trace");
      return readable.getRelated(CoreRelationTypes.Requirement_Trace__Higher_Level);
   }

   public static ResultSet<ArtifactReadable> getSupportingInfo(ArtifactReadable readable) {
      Conditions.checkNotNull(readable, "artifact readable for supporting info");
      return readable.getRelated(CoreRelationTypes.SupportingInfo_SupportedBy);
   }

   public static String getAbbrevQualificationMethod(ResultSet<ArtifactReadable> readables) {
      HashSet<String> qualMethods = new HashSet<String>();
      Conditions.checkNotNull(readables, "artifact readable for qual methods");
      for (ArtifactReadable readable : readables) {
         if (readable.isOfType(CoreArtifactTypes.AbstractSoftwareRequirement)) {
            List<String> qualMethod = readable.getAttributeValues(CoreAttributeTypes.QualificationMethod);
            for (String qual : qualMethod) {
               String substring = "/" + qual.substring(0, 1);
               qualMethods.add(substring);
            }
         }
      }
      String quals = qualMethods.toString();
      quals = quals.replaceAll(REMOVEBRACKETS, "");
      quals = quals.replaceAll(REMOVESPACENCOMMA, "");
      return quals;
   }

   public static String extractAbbrevSubsystem(String heading) {
      String subSystem = Strings.emptyString();
      Conditions.checkNotNull(heading, "heading string");
      String[] s_heading = heading.split(":");
      if (s_heading.length == 2) {
         String[] s_csciNsubsys = s_heading[0].split("\\.");
         if (s_csciNsubsys.length == 2) {
            subSystem = s_csciNsubsys[1];
         }
      }
      return subSystem;
   }

}
