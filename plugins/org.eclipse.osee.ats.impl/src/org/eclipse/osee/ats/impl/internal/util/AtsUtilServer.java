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
package org.eclipse.osee.ats.impl.internal.util;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.core.util.ArtifactIdWrapper;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.AttributeIdWrapper;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeId;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilServer {

   // TODO use real application context
   public static ApplicationContext getApplicationContext() {
      return null;
   }

   public static IOseeBranch getAtsBranch() {
      return CoreBranches.COMMON;
   }

   public static ArtifactReadable getArtifact(OrcsApi orcsApi, IAtsObject atsObject) throws OseeCoreException {
      ArtifactReadable result = null;
      if (atsObject.getStoreObject() != null) {
         result = (ArtifactReadable) atsObject.getStoreObject();
      } else {
         result =
            orcsApi.getQueryFactory(null).fromBranch(AtsUtilServer.getAtsBranch()).andGuid(atsObject.getGuid()).getResults().getAtMostOneOrNull();
      }
      return result;
   }

   public static ArtifactId toArtifactId(IAtsObject atsObject) {
      return new ArtifactIdWrapper(atsObject);
   }

   public static AttributeId toAttributeId(IAttribute<?> attr) {
      return new AttributeIdWrapper(attr);
   }

   public static ArtifactReadable getArtifactByGuid(OrcsApi orcsApi, String guid) throws OseeCoreException {
      return orcsApi.getQueryFactory(null).fromBranch(AtsUtilServer.getAtsBranch()).andGuid(guid).getResults().getExactlyOne();
   }

   public static String getAtsId(Object obj) throws OseeCoreException {
      ArtifactReadable art = null;
      if (obj instanceof ArtifactReadable) {
         art = (ArtifactReadable) obj;
      } else if (obj instanceof IAtsObject) {
         art = (ArtifactReadable) ((IAtsObject) obj).getStoreObject();
      }
      Conditions.checkNotNull(art, "artifact");
      String toReturn = art.getSoleAttributeAsString(AtsAttributeTypes.AtsId, AtsUtilCore.DEFAULT_ATS_ID_VALUE);
      if (AtsUtilCore.DEFAULT_ATS_ID_VALUE.equals(toReturn)) {
         toReturn = art.getGuid();
      }
      return toReturn;
   }

}