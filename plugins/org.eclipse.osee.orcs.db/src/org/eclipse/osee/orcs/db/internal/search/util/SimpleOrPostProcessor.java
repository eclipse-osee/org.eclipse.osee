/*
 * Created on Apr 24, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal.search.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

public class SimpleOrPostProcessor extends QueryPostProcessor {

   private final CriteriaAttributeKeywords criteria;
   private final DeletionFlag deletionFlag;

   protected SimpleOrPostProcessor(Log logger, CriteriaAttributeKeywords criteria, DeletionFlag deletionFlag) {
      super(logger);
      this.criteria = criteria;
      this.deletionFlag = deletionFlag;
   }

   @Override
   protected List<Match<ArtifactReadable, AttributeReadable<?>>> innerCall() throws Exception {
      List<Match<ArtifactReadable, AttributeReadable<?>>> toReturn =
         new LinkedList<Match<ArtifactReadable, AttributeReadable<?>>>();

      Map<AttributeReadable<?>, List<MatchLocation>> matchedAttributes =
         new LinkedHashMap<AttributeReadable<?>, List<MatchLocation>>();

      IAttributeType type = criteria.getTypes().iterator().next();
      List<ArtifactReadable> toProcess = getItemsToProcess();

      for (ArtifactReadable toCheck : toProcess) {
         List<AttributeReadable<Object>> attributes = toCheck.getAttributes(type, deletionFlag);

         for (AttributeReadable<Object> attribute : attributes) {
            String value = String.valueOf(attribute);

            for (String toMatch : criteria.getValues()) {
               if (value.equals(toMatch)) {
                  MatchLocation loc = new MatchLocation(1, value.length());
                  List<MatchLocation> list = matchedAttributes.get(attribute);
                  if (list == null) {
                     list = new LinkedList<MatchLocation>();
                     matchedAttributes.put(attribute, list);
                  }
                  list.add(loc);
               }
            }
         }
         toReturn.add(new ArtifactMatch(toCheck, matchedAttributes));
      }
      return toReturn;
   }
}
