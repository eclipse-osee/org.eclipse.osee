/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.orcs.core.internal;

import java.security.cert.X509Certificate;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.utility.EmailCertificateService;
import org.eclipse.osee.orcs.utility.EmailCertificateValidator;

public class EmailCertificateServiceImpl implements EmailCertificateService {

   private final OrcsApi orcsApi;

   public EmailCertificateServiceImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public void setPublicCertificateForCurrentUser(String certificatePem) {
      // 1) Parse + basic validity (structure, dates)
      X509Certificate cert = EmailCertificateValidator.parseAndCheckBasicValidity(certificatePem);

      // 2) Ensure it is intended for email use (key usage / EKU, etc.)
      EmailCertificateValidator.checkSuitableForEmail(cert);

      // 3) Persist the PEM as-is for the current user
      UserToken user = orcsApi.userService().getUser();

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         "Store email public certificate associated with user.");
      tx.setSoleAttributeFromString(user.getArtifactId(), CoreAttributeTypes.EmailPublicCertificate, certificatePem);
      tx.commit();
   }

   @Override
   public String getPublicCertificateForCurrentUser() {
      UserToken user = orcsApi.userService().getUser();
      QueryFactory queryFactory = orcsApi.getQueryFactory();

      return queryFactory.fromBranch(CoreBranches.COMMON).andId(
         user.getArtifactId()).asArtifact().getSoleAttributeAsString(CoreAttributeTypes.EmailPublicCertificate, null);
   }

   @Override
   public void deletePublicCertificateForCurrentUser() {
      UserToken user = orcsApi.userService().getUser();

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON,
         "Delete email public certificate associated with user.");
      tx.deleteSoleAttribute(user.getArtifactId(), CoreAttributeTypes.EmailPublicCertificate);
      tx.commit();
   }
}
