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
package org.eclipse.osee.ats.rest.internal.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.template.engine.AppendableRule;
import org.eclipse.osee.template.engine.IdentifiableOptionsRule;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Donald G. Dunne
 */
@Path("action")
public final class AtsUiResource {
   private final OrcsApi orcsApi;
   private final IResourceRegistry registry;

   public AtsUiResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      registry = orcsApi.getResourceRegistry();
   }

   /**
    * @return resource name
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() throws Exception {
      return AHTML.simplePage("ATS UI Resource");
   }

   /**
    * @return html5 action entry page
    */
   @Path("NewAction")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getNewSource() throws Exception {
      PageCreator page = PageFactory.newPageCreator(registry, AtsResourceTokens.AtsValuesHtml);
      page.readKeyValuePairs(AtsResourceTokens.AtsNewActionValuesHtml);
      List<ArtifactReadable> sortedAis = new ArrayList<ArtifactReadable>();
      for (ArtifactReadable ai : ActionUtility.getAis(orcsApi)) {
         sortedAis.add(ai);
      }
      Collections.sort(sortedAis, new IdComparator());
      AppendableRule rule =
         new IdentifiableOptionsRule<ArtifactReadable>("ActionableItemDataList", sortedAis, "actionableItemList");
      page.addSubstitution(rule);
      return page.realizePage(AtsResourceTokens.AtsNewActionHtml);
   }

   private static final class IdComparator implements Comparator<ArtifactReadable> {

      @Override
      public int compare(ArtifactReadable arg0, ArtifactReadable arg1) {
         return arg0.getName().compareTo(arg1.getName());
      }
   };

   /**
    * @return html5 action entry page
    */
   @Path("Search")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getSearch() throws Exception {
      PageCreator page = PageFactory.newPageCreator(registry, AtsResourceTokens.AtsValuesHtml);
      return page.realizePage(AtsResourceTokens.AtsSearchHtml);
   }

}