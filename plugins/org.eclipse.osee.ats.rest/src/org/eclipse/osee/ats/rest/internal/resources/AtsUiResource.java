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
package org.eclipse.osee.ats.rest.internal.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.resource.AtsResourceTokens;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
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
@Path("/ui/action")
public final class AtsUiResource {

   private final IResourceRegistry registry;
   private final OrcsApi orcsApi;

   public AtsUiResource(IResourceRegistry registry, OrcsApi orcsApi) {
      this.registry = registry;
      this.orcsApi = orcsApi;
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
      PageCreator page = PageFactory.newPageCreator(registry, AtsResourceTokens.AtsValuesHtml, new String[0]);
      List<ArtifactReadable> sortedAis = new ArrayList<ArtifactReadable>();
      for (ArtifactReadable ai : getAis()) {
         sortedAis.add(ai);
      }
      Collections.sort(sortedAis, new IdComparator());
      AppendableRule<ArtifactReadable> rule =
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

   private ResultSet<ArtifactReadable> getAis() throws OseeCoreException {
      return orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
         AtsArtifactTypes.ActionableItem).getResults();
   }

   /**
    * @return html5 action entry page
    */
   @Path("Search")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getSearch() throws Exception {
      PageCreator page = PageFactory.newPageCreator(registry, AtsResourceTokens.AtsValuesHtml, new String[0]);
      return page.realizePage(AtsResourceTokens.AtsSearchHtml);
   }

}
