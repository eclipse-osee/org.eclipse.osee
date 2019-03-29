/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.integration.internal;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class OseeUtil {

   private OseeUtil() {
      // Utility Class
   }

   private static long checkAndGetUuid(OseeType type) {
      String uuid = type.getId();
      Conditions.checkNotNull(uuid, "uuid", "for type [%s]", type.getName());
      return Long.valueOf(uuid);
   }

   public static ArtifactTypeToken toToken(XArtifactType model) {
      return ArtifactTypeToken.valueOf(checkAndGetUuid(model), Strings.unquote(model.getName()));
   }

   public static AttributeTypeId toToken(XAttributeType model) {
      return AttributeTypeToken.valueOf(checkAndGetUuid(model), Strings.unquote(model.getName()));
   }

   public static IRelationType toToken(XRelationType model) {
      return RelationTypeToken.create(checkAndGetUuid(model), Strings.unquote(model.getName()));
   }

   public static boolean isRestrictedSide(XRelationSideEnum relationSideEnum, RelationSide relationSide) {
      Conditions.checkNotNull(relationSideEnum, "relation side restriction");
      Conditions.checkNotNull(relationSide, "relation side");

      boolean toReturn = false;
      switch (relationSideEnum) {
         case BOTH:
            toReturn = true;
            break;
         case SIDE_A:
            toReturn = relationSide.isSideA();
            break;
         case SIDE_B:
            toReturn = !relationSide.isSideA();
            break;
         default:
            break;
      }
      return toReturn;
   }

   public static PermissionEnum getPermission(ObjectRestriction restriction) {
      Conditions.checkNotNull(restriction, "restriction");
      AccessPermissionEnum modelPermission = restriction.getPermission();
      Conditions.checkNotNull(modelPermission, "restriction permission");
      PermissionEnum toReturn;
      if (modelPermission == AccessPermissionEnum.ALLOW) {
         toReturn = PermissionEnum.WRITE;
      } else {
         toReturn = PermissionEnum.READ;
      }
      return toReturn;
   }
}