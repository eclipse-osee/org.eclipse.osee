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
package org.eclipse.osee.framework.core.dsl.integration.util;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class OseeUtil {

   private OseeUtil() {
      // Utility Class
   }

   public static IArtifactType toToken(XArtifactType model) {
      return new ArtifactTypeToken(model);
   }

   public static IAttributeType toToken(XAttributeType model) {
      return new AttributeTypeToken(model);
   }

   public static IRelationType toToken(XRelationType model) {
      return new RelationTypeToken(model);
   }

   private final static class ArtifactTypeToken extends NamedIdentity implements IArtifactType {
      public ArtifactTypeToken(XArtifactType model) {
         super(model.getTypeGuid(), Strings.unquote(model.getName()));
      }
   }

   private final static class AttributeTypeToken extends NamedIdentity implements IAttributeType {
      public AttributeTypeToken(XAttributeType model) {
         super(model.getTypeGuid(), Strings.unquote(model.getName()));
      }
   }

   private final static class RelationTypeToken extends NamedIdentity implements IRelationType {
      public RelationTypeToken(XRelationType model) {
         super(model.getTypeGuid(), Strings.unquote(model.getName()));
      }
   }

   public static boolean isRestrictedSide(XRelationSideEnum relationSideEnum, RelationSide relationSide) throws OseeCoreException {
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

   public static PermissionEnum getPermission(ObjectRestriction restriction) throws OseeCoreException {
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

   public static String getRelationOrderType(String guid) throws OseeCoreException {
      RelationOrderBaseTypes type = RelationOrderBaseTypes.getFromGuid(guid);
      return type.getName().replaceAll(" ", "_");
   }

   public static String orderTypeNameToGuid(String orderTypeName) throws OseeCoreException {
      Conditions.checkNotNull(orderTypeName, "orderTypeName");
      return RelationOrderBaseTypes.getFromOrderTypeName(orderTypeName.replaceAll("_", " ")).getGuid();
   }

}
