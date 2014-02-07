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
package org.eclipse.osee.define.report.internal;

import static org.eclipse.osee.define.report.internal.util.RequirementConstants.COMPANY;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.DOCUMENT_TEMPLATE_GUID;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.PROPERTIES;
import static org.eclipse.osee.define.report.internal.util.RequirementConstants.PUBLISH_REQUIREMENT;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.report.SoftwareRequirementReportGenerator;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.template.engine.OseeTemplateTokens;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.WordDocumentPropertiesRule;

public final class RequirementStreamingOutput implements StreamingOutput {

   private final OrcsApi orcsApi;
   private final String branchGuid;
   private final String srsRoot;

   /**
    * @author Megumi Telles
    */
   public RequirementStreamingOutput(OrcsApi orcsApi, String branchGuid, String srsRoot) {
      this.orcsApi = orcsApi;
      this.branchGuid = branchGuid;
      this.srsRoot = srsRoot;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Writer writer = new OutputStreamWriter(output);
         QueryFactory queryFactory = orcsApi.getQueryFactory(null);

         ArtifactReadable documentTemplate =
            queryFactory.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.GeneralData).andGuid(
               DOCUMENT_TEMPLATE_GUID).getResults().getExactlyOne();
         List<String> stringData = documentTemplate.getAttributeValues(CoreAttributeTypes.GeneralStringData);

         WordDocumentPropertiesRule documentRule =
            new WordDocumentPropertiesRule(PROPERTIES, PUBLISH_REQUIREMENT, "", COMPANY);

         SoftwareRequirementReportGenerator requirementReport =
            new SoftwareRequirementReportGenerator(queryFactory, branchGuid, srsRoot, stringData);
         PageCreator page = new PageCreator(orcsApi.getResourceRegistry());

         page.addSubstitution(documentRule);
         page.addSubstitution(requirementReport);
         page.realizePage(OseeTemplateTokens.WordXml, writer);
         writer.close();

      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }
}
