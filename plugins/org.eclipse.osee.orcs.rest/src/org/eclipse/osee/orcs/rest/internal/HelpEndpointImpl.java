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

package org.eclipse.osee.orcs.rest.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.dto.HelpPageDto;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.HelpEndpoint;

/**
 * @author Ryan T. Baldwin
 */
public final class HelpEndpointImpl implements HelpEndpoint {

   private final OrcsApi orcsApi;

   public HelpEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public List<HelpPageDto> getHelp(String appName) {
      Stream<HelpPageDto> helpPages =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.HelpPage).follow(
            CoreRelationTypes.HelpToHelp_Child).asArtifacts().stream().map(a -> new HelpPageDto(a));
      if (appName != null && !appName.isEmpty()) {
         helpPages = helpPages.filter(h -> h.getAppName().toLowerCase().equals(appName.toLowerCase()));
      }
      List<HelpPageDto> appHelpPages = helpPages.collect(Collectors.toList());

      // Filter out any child pages from the top level so they are not duplicated.
      List<HelpPageDto> children = getAllChildren(appHelpPages);
      return appHelpPages.stream().filter(p -> !children.contains(p)).collect(Collectors.toList());
   }

   private List<HelpPageDto> getAllChildren(List<HelpPageDto> helpPages) {
      List<HelpPageDto> children = new LinkedList<>();
      for (HelpPageDto page : helpPages) {
         if (!page.getChildren().isEmpty()) {
            children.addAll(page.getChildren());
            children.addAll(getAllChildren(page.getChildren()));
         }
      }
      return children;
   }

}
