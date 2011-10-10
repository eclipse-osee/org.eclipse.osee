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
package org.eclipse.osee.orcs.search;

import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OseeApi;
import org.eclipse.osee.orcs.data.ReadableArtifact;

public class ApiTester {

   public static ApplicationContext getContext() {
      return null;
   }

   @SuppressWarnings("unused")
   public static void main(String[] args) throws OseeCoreException {
      OseeApi api = getApi();

      ApplicationContext context = getContext();
      QueryFactory query = api.getQueryFactory(context);

      ReadableArtifact art1 = query.fromName(CoreBranches.COMMON, "WPN_PAGE")//
      .includeCache()//
      .includeDeleted()//
      .build(LoadLevel.FULL)//
      .getExactlyOne();

      List<ReadableArtifact> arts = query//
      .fromName(CoreBranches.COMMON, "WPN_PAGE")//
      .includeCache()//
      .includeDeleted()//
      .build(LoadLevel.ATTRIBUTE)//
      .getList();

      /// Chained
      ReadableArtifact art2 = query//
      .fromBranch(CoreBranches.COMMON)//
      .and(CoreAttributeTypes.Active, Operator.EQUAL, "true")//
      .includeDeleted()//
      .and(CoreAttributeTypes.ParagraphNumber, Operator.LESS_THAN, "1.2.3")//
      .and(CoreAttributeTypes.Company, Operator.NOT_EQUAL, "company")//
      .andExists(CoreAttributeTypes.Active)//
      .build(LoadLevel.FULL)//
      .getExactlyOne();

      // None Chained
      QueryBuilder builder1 = query.fromBranch(CoreBranches.COMMON);
      builder1.and(CoreAttributeTypes.Active, Operator.EQUAL, "true");
      builder1.and(CoreAttributeTypes.ParagraphNumber, Operator.LESS_THAN, "1.2.3");
      builder1.and(CoreAttributeTypes.Company, Operator.NOT_EQUAL, "company");

      ResultSet<ReadableArtifact> result = builder1.build(LoadLevel.FULL);
      ReadableArtifact art3 = result.getExactlyOne();
      List<ReadableArtifact> items = result.getList();

      // One liner
      int count1 = query.fromName(CoreBranches.COMMON, "WPN_PAGE").includeDeleted(true).getCount();
      int count2 = query.fromName(CoreBranches.COMMON, "WPN_PAGE").includeDeleted().getCount();
      int count3 = query.fromName(CoreBranches.COMMON, "WPN_PAGE").getCount();

      //factory.fromName("WPN_PAGE", CoreBranches.COMMON).setOptions(new OptionsObject(LoadLevel.FULL, ).getCount();
      //factory.fromName("WPN_PAGE", CoreBranches.COMMON).setOptions(new FullLoadwithDeletedAndSomething()).getCount();

   }

   public static OseeApi getApi() {
      return null;
   }

   //   ArtifactQueryService.getFromName("WPN_PAGE", null).getArtifactList(LoadLevel.FULL, QueryOption.IncludeDeleted).getCount();
   //   ArtifactQueryService.getFromName("WPN_PAGE", null).setOptions(LoadLevel.FULL, QueryOption.IncludeDeleted).getCount();
   //   ArtifactQueryService.getFromName("WPN_PAGE", null).fullLoadWithDeleted().getCount();\
   //   ArtifactQueryService.getFromName("WPN_PAGE", null).setOptions(new OptionsObject(LoadLevel.FULL, ).getCount();
   //   ArtifactQueryService.getFromName("WPN_PAGE", null).setOptions(new FullLoadwithDeletedAndSomething()).getCount();
   //   ArtifactQueryService.getFromName("WPN_PAGE", null, ).getCount();
   //
   //   QueryFactory queryFactory = orcs.createQuery(); // Create Composes the services
   //
   //   QueryBuilder query = queryFactory.getFromName("WPN_PAGE", null);
   //   query.includeDeleted().excludeSomething();
   //
   //   Result result = query.build(); 
   //   result.getCount();
   //   result.getList();
   // etc.....
   //
   //   orcs.createQuery().getFromName("WPN_PAGE", null).includeDeleted()...QueryBuilder...//Result .build().getCount();
   //
}
