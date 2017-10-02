/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeUtil;
import org.eclipse.osee.orcs.db.internal.exchange.ExportImportXml;
import org.eclipse.osee.orcs.db.internal.exchange.IOseeExchangeDataProvider;
import org.eclipse.osee.orcs.db.internal.exchange.ReferentialIntegrityConstraint;
import org.eclipse.osee.orcs.db.internal.exchange.StandardOseeDbExportDataProvider;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.transform.ExchangeDataProcessor;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;

/**
 * @author Roberto E. Escobar
 */
public class CheckBranchExchangeIntegrityCallable extends AbstractDatastoreCallable<URI> {

   private final URI fileToCheck;
   private final SystemPreferences preferences;
   private final IResourceManager resourceManager;

   public CheckBranchExchangeIntegrityCallable(Log logger, OrcsSession session, JdbcClient service, SystemPreferences preferences, IResourceManager resourceManager, URI fileToCheck) {
      super(logger, session, service);
      this.fileToCheck = fileToCheck;
      this.preferences = preferences;
      this.resourceManager = resourceManager;
   }

   private IResourceLocator findResourceToCheck(URI fileToCheck)  {
      return resourceManager.getResourceLocator(fileToCheck.toASCIIString());
   }

   private IOseeExchangeDataProvider createExportDataProvider(IResourceLocator exportDataLocator)  {
      String exchangeBasePath = ResourceConstants.getExchangeDataPath(preferences);
      Pair<Boolean, File> result =
         ExchangeUtil.getTempExchangeFile(exchangeBasePath, getLogger(), exportDataLocator, resourceManager);
      return new StandardOseeDbExportDataProvider(exchangeBasePath, getLogger(), result.getSecond(), result.getFirst());
   }

   @Override
   public URI call() throws Exception {
      List<ReferentialIntegrityConstraint> constraints = new ArrayList<>();
      long startTime = System.currentTimeMillis();

      IResourceLocator resourceLocator = findResourceToCheck(fileToCheck);

      IOseeExchangeDataProvider exportDataProvider = createExportDataProvider(resourceLocator);
      ExchangeDataProcessor processor = new ExchangeDataProcessor(exportDataProvider);

      initializeConstraints(constraints);

      String verifyFile = exportDataProvider.getExportedDataRoot().getName() + ".verify.xml";
      File writeLocation = exportDataProvider.getExportedDataRoot().getParentFile();

      Writer writer = null;
      try {
         writer = ExchangeUtil.createXmlWriter(writeLocation, verifyFile, (int) Math.pow(2, 20));
         ExportImportXml.openXmlNode(writer, ExportImportXml.DATA);

         for (ReferentialIntegrityConstraint constraint : constraints) {
            getLogger().info("Verifing constraint [%s]", constraint.getPrimaryKeyListing());

            constraint.checkConstraint(getLogger(), getJdbcClient(), processor);
            writeConstraintResults(writer, constraint);
         }
         ExportImportXml.closeXmlNode(writer, ExportImportXml.DATA);
      } finally {
         Lib.close(writer);
         processor.cleanUp();
         getLogger().info("Verified [%s] in [%s]", exportDataProvider.getExportedDataRoot(),
            Lib.getElapseString(startTime));
      }
      return new File(writeLocation, verifyFile).toURI();
   }

   private void initializeConstraints(List<ReferentialIntegrityConstraint> constraints) {
      ReferentialIntegrityConstraint constraint;

      constraint = new ReferentialIntegrityConstraint(ExportItem.OSEE_TX_DETAILS_DATA, "transaction_id");
      constraint.addForeignKey(ExportItem.OSEE_BRANCH_DATA, "parent_transaction_id", "baseline_transaction_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_DATA, "transaction_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_ARCHIVED_DATA, "transaction_id");
      constraint.addForeignKey(ExportItem.OSEE_MERGE_DATA, "commit_transaction_id");
      constraints.add(constraint);

      constraint = new ReferentialIntegrityConstraint(ExportItem.OSEE_ARTIFACT_DATA, "art_id");
      constraint.addForeignKey(ExportItem.OSEE_TX_DETAILS_DATA, "author", "commit_art_id");
      constraint.addForeignKey(ExportItem.OSEE_ATTRIBUTE_DATA, "art_id");
      constraint.addForeignKey(ExportItem.OSEE_RELATION_LINK_DATA, "a_art_id", "b_art_id");
      constraint.addForeignKey(ExportItem.OSEE_ARTIFACT_ACL_DATA, "art_id", "privilege_entity_id");
      constraint.addForeignKey(ExportItem.OSEE_BRANCH_ACL_DATA, "privilege_entity_id");
      constraints.add(constraint);

      constraint = new ReferentialIntegrityConstraint(ExportItem.OSEE_ARTIFACT_DATA, "gamma_id");
      constraint.addPrimaryKey(ExportItem.OSEE_ATTRIBUTE_DATA, "gamma_id");
      constraint.addPrimaryKey(ExportItem.OSEE_RELATION_LINK_DATA, "gamma_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_DATA, "gamma_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_ARCHIVED_DATA, "gamma_id");
      constraint.addForeignKey(ExportItem.OSEE_CONFLICT_DATA, "source_gamma_id");
      constraint.addForeignKey(ExportItem.OSEE_CONFLICT_DATA, "dest_gamma_id");
      constraints.add(constraint);

      constraint = new ReferentialIntegrityConstraint(ExportItem.OSEE_BRANCH_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_BRANCH_DATA, "parent_branch_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_TXS_ARCHIVED_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_TX_DETAILS_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_ARTIFACT_ACL_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_BRANCH_ACL_DATA, "branch_id");
      constraint.addForeignKey(ExportItem.OSEE_MERGE_DATA, "source_branch_id");
      constraint.addForeignKey(ExportItem.OSEE_MERGE_DATA, "dest_branch_id");
      constraint.addForeignKey(ExportItem.OSEE_MERGE_DATA, "merge_branch_id");
      constraint.addForeignKey(ExportItem.OSEE_CONFLICT_DATA, "merge_branch_id");
      constraints.add(constraint);
   }

   private void writeConstraintResults(Writer writer, ReferentialIntegrityConstraint constraint) throws IOException {
      HashCollection<String, Long> missingPrimaryKeys = constraint.getMissingPrimaryKeys();

      Set<Long> unreferencedPrimaryKeys = constraint.getUnreferencedPrimaryKeys();
      boolean passedCheck = missingPrimaryKeys.isEmpty() && unreferencedPrimaryKeys.isEmpty();

      writer.append("\t");
      ExportImportXml.openPartialXmlNode(writer, ExportImportXml.PRIMARY_KEY);
      ExportImportXml.addXmlAttribute(writer, ExportImportXml.ID, constraint.getPrimaryKeyListing());
      ExportImportXml.addXmlAttribute(writer, "status", passedCheck ? "OK" : "FAILED");

      if (passedCheck) {
         ExportImportXml.closePartialXmlNode(writer);
      } else {
         ExportImportXml.endOpenedPartialXmlNode(writer);
         writer.append("\t\t");
         ExportImportXml.openXmlNode(writer, ExportImportXml.UNREFERENCED_PRIMARY_KEY);
         Xml.writeWhileHandlingCdata(writer, "\t\t\t" + unreferencedPrimaryKeys.toString());
         writer.append("\n\t\t");
         ExportImportXml.closeXmlNode(writer, ExportImportXml.UNREFERENCED_PRIMARY_KEY);

         for (String foreignKey : missingPrimaryKeys.keySet()) {
            writer.append("\t\t");
            ExportImportXml.openPartialXmlNode(writer, "ForeignKey");
            ExportImportXml.addXmlAttribute(writer, ExportImportXml.ID, foreignKey);
            ExportImportXml.endOpenedPartialXmlNode(writer);
            Xml.writeWhileHandlingCdata(writer, "\t\t\t" + missingPrimaryKeys.getValues(foreignKey).toString());
            writer.append("\n\t\t");
            ExportImportXml.closeXmlNode(writer, "ForeignKey");
         }
         writer.append("\t");
         ExportImportXml.closeXmlNode(writer, ExportImportXml.PRIMARY_KEY);
      }
   }

}
