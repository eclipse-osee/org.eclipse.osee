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
package org.eclipse.osee.ats.rest.internal.notify;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.orcs.OrcsApi;

public class OseeEmailServer extends OseeEmail {

   private OseeEmailServer() {
   }

   private OseeEmailServer(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType) {
      super(toAddresses, fromAddress, replyToAddress, subject, body, bodyType);
   }

   private OseeEmailServer(String fromEmail, String toAddress, String subject, String body, BodyType bodyType) {
      super(fromEmail, toAddress, subject, body, bodyType);
   }

   @Override
   public void setClassLoader() {
      Thread.currentThread().setContextClassLoader(new ExportClassLoader(ServiceUtil.getPackageAdmin()));
   }

   public static OseeEmailServer create() {
      loadDefaultMailServer();
      return new OseeEmailServer();
   }

   public static OseeEmailServer create(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType) {
      loadDefaultMailServer();
      return new OseeEmailServer(toAddresses, fromAddress, replyToAddress, subject, body, bodyType);
   }

   public static OseeEmailServer create(String fromEmail, String toAddress, String subject, String body, BodyType bodyType) {
      loadDefaultMailServer();
      return new OseeEmailServer(fromEmail, toAddress, subject, body, bodyType);
   }

   private static void loadDefaultMailServer() {
      if (defaultMailServer == null) {
         OrcsApi orcsApi = ServiceUtil.getOrcsApi();
         ArtifactReadable globalArt =
            (ArtifactReadable) orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
               CoreArtifactTokens.GlobalPreferences).getArtifactOrSentinal();
         defaultMailServer = globalArt.getSoleAttributeValue(CoreAttributeTypes.DefaultMailServer, "");
      }
   }

}
