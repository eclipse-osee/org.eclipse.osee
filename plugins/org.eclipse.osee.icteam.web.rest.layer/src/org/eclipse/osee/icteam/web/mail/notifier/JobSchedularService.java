/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.mail.notifier;

import java.util.List;

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
// import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * This class is to get global artifact which is used to get mail server
 * 
 * @author Ajay Chandrahasan
 */
public class JobSchedularService {

  /**
   * This method returns global artifact
   * 
   * @return
   */
  public static String getGlobalArtifact() {
    List<ArtifactReadable> lst = null;
    String soleAttributeAsString = null;
    try {

      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      QueryFactory queryFactory = orcsApi.getQueryFactory();
      ResultSet<ArtifactReadable> results =
          queryFactory.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.GlobalPreferences).getResults();

      for (ArtifactReadable artifactReadable : results) {
        soleAttributeAsString = artifactReadable.getSoleAttributeAsString(CoreAttributeTypes.DefaultMailServer);
      }

    }
    catch (OseeCoreException e) {
      e.printStackTrace();
    }
    return soleAttributeAsString;
  }

}
