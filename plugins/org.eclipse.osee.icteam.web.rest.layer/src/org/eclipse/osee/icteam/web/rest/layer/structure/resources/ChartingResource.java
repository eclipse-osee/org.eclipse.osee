/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
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
package org.eclipse.osee.icteam.web.rest.layer.structure.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import flexjson.JSONSerializer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.rest.layer.util.BurnDownData;
import org.eclipse.osee.icteam.web.rest.layer.util.CommonUtil;
import org.eclipse.osee.icteam.web.rest.layer.util.InterfaceAdapter;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Chart Resource to return burn down data for total stories and efforts
 *
 * @author Ajay Chandrahasan
 */
@Path("Charts")
public class ChartingResource extends AbstractConfigResource {

   public ChartingResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.Version, atsApi, orcsApi);
   }

   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
   SimpleDateFormat simpleDateFormatReverse = new SimpleDateFormat("yyyy-MM-dd");

   private final static String COMPLETED = "Completed";

   /**
    * Constructs and returns the effort burndown in JSON format.
    * 
    * @param sprintGuid Guid of the sprint
    * @return Effort burndown data
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getEffortBurnDownInfo")
   public String getBurnDownInfoData(final String sprintGuid) {

      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(sprintGuid, TransferableArtifact.class);
      ResultSet<ArtifactReadable> list =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.Version).andUuid(
            Long.valueOf(artifact.getUuid())).getResults();

      Date startDate = null;
      Date endDate = null;
      float totalTime = 0;
      Map<Date, Float> burnDownDataMap = new TreeMap<>();

      if (!list.isEmpty()) {
         ArtifactReadable artifactReadable = list.getOneOrNull();
         String startDateString = artifactReadable.getAttributeValuesAsString(AtsAttributeTypes.StartDate);
         String releaseDateString = artifactReadable.getAttributeValuesAsString(AtsAttributeTypes.ReleaseDate);

         try {
            startDate = simpleDateFormatReverse.parse(startDateString);
            endDate = simpleDateFormatReverse.parse(releaseDateString);
         } catch (ParseException e) {
            e.printStackTrace();
         }

         if (artifactReadable != null) {
            ResultSet<ArtifactReadable> taskResultSet =
               artifactReadable.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow);
            List<ArtifactReadable> tasksList = taskResultSet.getList();
            for (ArtifactReadable artReadable : tasksList) {
               String attributeValuesAsString = artReadable.getAttributeValuesAsString(AtsAttributeTypes.BurnDownData);
               LinkedHashMap<Object, LinkedTreeMap<String, String>> burnDownMap =
                  gson.fromJson(attributeValuesAsString, LinkedHashMap.class);
               if (burnDownMap != null) {
                  Set<Entry<Object, LinkedTreeMap<String, String>>> burnDownEntries = burnDownMap.entrySet();
                  boolean isFirst = true;
                  for (Entry<Object, LinkedTreeMap<String, String>> burnDownEntry : burnDownEntries) {
                     Date date = null;
                     if (burnDownEntry.getKey() instanceof String) {
                        String dateString = (String) burnDownEntry.getKey();
                        try {
                           date = simpleDateFormatReverse.parse(dateString);
                        } catch (ParseException e) {
                           e.printStackTrace();
                        }
                     }

                     String estimatedTime = burnDownEntry.getValue().get("first");
                     if (isFirst) {
                        totalTime += Float.parseFloat(estimatedTime);
                        isFirst = false;
                     }
                     String timeSpentStr = burnDownEntry.getValue().get("second");
                     float timeSpent = !timeSpentStr.isEmpty() ? Float.parseFloat(timeSpentStr) : 0;

                     if (burnDownDataMap.containsKey(date)) {
                        burnDownDataMap.merge(date, timeSpent, (oldValue, newValue) -> oldValue + newValue);
                     } else {
                        burnDownDataMap.put(date, timeSpent);
                     }
                  }
               }
            }
         }
      }

      Map<Date, Float> updatedMap = new LinkedHashMap<>();
      try {
         updatedMap = identifyAndFillEmptyDates(burnDownDataMap, startDate, endDate);
      } catch (ParseException e) {
         e.printStackTrace();
      }

      LinkedList<Date> linkedList = new LinkedList<>(updatedMap.keySet());
      Date currentDate = linkedList.getLast();
      for (Entry<Date, Float> entry : updatedMap.entrySet()) {
         updatedMap.merge(entry.getKey(), totalTime, (timeSpent, total) -> total - timeSpent);
      }

      long totalNumberOfDays = getNoOfDays(startDate, endDate);

      try {
         fillRemainingDates(updatedMap, currentDate, endDate);
      } catch (ParseException e) {
         e.printStackTrace();
      }

      Set<Date> datesSet = updatedMap.keySet();
      List<String> datesString =
         datesSet.stream().map(date -> simpleDateFormat.format(date)).collect(Collectors.toList());
      Collection<Float> values = updatedMap.values();

      BurnDownData burnDown = new BurnDownData();
      burnDown.setDatesSet(datesString);
      burnDown.setRemainingTimeSet(values);
      burnDown.setTotalTime(totalTime);
      burnDown.setNoOfDays((int) totalNumberOfDays);
      JSONSerializer jsonSerializer = new JSONSerializer();
      String burnDownData = jsonSerializer.deepSerialize(burnDown);

      return burnDownData;
   }

   /**
    * Constructs and returns the story points burndown in JSON format.
    * 
    * @param sprintGuid : Guid of a sprint
    * @return story points burndown data
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getStoryPointsBurndown")
   public String getStoryPointsBurndownData(final String sprintGuid) {

      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(sprintGuid, TransferableArtifact.class);
      ResultSet<ArtifactReadable> list =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.Version).andUuid(
            Long.valueOf(artifact.getUuid())).getResults();

      Date startDate = null;
      Date endDate = null;
      int totalStoryPoints = 0;
      Map<Date, Float> burnDownDataMap = new TreeMap<>();

      if (!list.isEmpty()) {
         ArtifactReadable artifactReadable = list.getOneOrNull();
         String startDateString = artifactReadable.getAttributeValuesAsString(AtsAttributeTypes.StartDate);
         String releaseDateString = artifactReadable.getAttributeValuesAsString(AtsAttributeTypes.ReleaseDate);

         try {
            startDate = simpleDateFormatReverse.parse(startDateString);
            endDate = simpleDateFormatReverse.parse(releaseDateString);
         } catch (ParseException e1) {
            e1.printStackTrace();
         }

         if (artifactReadable != null) {
            ResultSet<ArtifactReadable> taskResultSet =
               artifactReadable.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow);
            List<ArtifactReadable> tasksList = taskResultSet.getList();
            for (ArtifactReadable artReadable : tasksList) {

               String storyPoints = artReadable.getAttributeValuesAsString(AtsAttributeTypes.PointsAttributeType);
               String taskState = artReadable.getAttributeValuesAsString(AtsAttributeTypes.CurrentStateType);
               int currentStoryPoint = 0;
               if (!storyPoints.isEmpty()) {
                  currentStoryPoint = Integer.parseInt(storyPoints);
               }
               totalStoryPoints += currentStoryPoint;
               burnDownDataMap.put(startDate, 0f);
               if (taskState.equals(ChartingResource.COMPLETED)) {
                  String completedDateString = artReadable.getAttributeValuesAsString(AtsAttributeTypes.CompletedDate);
                  if (!completedDateString.isEmpty()) {
                     Date completedDate = null;
                     try {
                        completedDate = simpleDateFormatReverse.parse(completedDateString);
                     } catch (ParseException e) {
                        e.printStackTrace();
                     }

                     if (!burnDownDataMap.containsKey(completedDate)) {
                        burnDownDataMap.put(completedDate, (float) currentStoryPoint);
                     } else {
                        Float completedStoryPoints = burnDownDataMap.get(completedDate);
                        completedStoryPoints += currentStoryPoint;
                        burnDownDataMap.put(completedDate, (float) completedStoryPoints);
                     }
                  }
               }
            }
         }
      }

      Map<Date, Float> updatedMap = new LinkedHashMap<>();
      try {
         updatedMap = identifyAndFillEmptyDates(burnDownDataMap, startDate, endDate);
      } catch (ParseException e) {
         e.printStackTrace();
      }

      Map<Date, Float> hashMap = new TreeMap<>();
      float storyPoints = 0;
      for (Entry<Date, Float> entry : updatedMap.entrySet()) {
         storyPoints += entry.getValue();
         Date key = entry.getKey();
         hashMap.put(key, storyPoints);
      }

      LinkedList<Date> linkedList = new LinkedList<>(hashMap.keySet());
      Date currentDate = linkedList.getLast();
      for (Entry<Date, Float> entry : hashMap.entrySet()) {
         hashMap.merge(entry.getKey(), (float) totalStoryPoints, (oldValue, total) -> total - oldValue);
      }

      long totalNumberOfDays = getNoOfDays(startDate, endDate);

      try {
         fillRemainingDates(hashMap, currentDate, endDate);
      } catch (ParseException e) {
         e.printStackTrace();
      }

      Set<Date> datesSet = hashMap.keySet();
      List<String> datesString =
         datesSet.stream().map(date -> simpleDateFormat.format(date)).collect(Collectors.toList());
      Collection<Float> remainingStoryPoints = hashMap.values();
      List<Float> practicalStoryPointsData =
         CommonUtil.getPracticalStoryPointsData(totalNumberOfDays + 1, totalStoryPoints);

      BurnDownData burnDownData = new BurnDownData();
      burnDownData.setDatesSet(datesString);
      burnDownData.setNoOfDays((int) totalNumberOfDays);
      burnDownData.setRemainingStoryPoints(remainingStoryPoints);
      burnDownData.setTotalStoryPoints(totalStoryPoints);
      burnDownData.setPracticalStoryPoints(practicalStoryPointsData);

      JSONSerializer jsonSerializer = new JSONSerializer();
      String burnDownStoryPoints = jsonSerializer.deepSerialize(burnDownData);

      return burnDownStoryPoints;
   }

   /**
    * Return the number of days between two dates.
    * 
    * @param startDate Start date
    * @param endDate End date
    * @return No of days
    */
   private long getNoOfDays(Date startDate, Date endDate) {
      return ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
   }

   /**
    * The remaining days till the end of the sprint will be filled with null values.
    * 
    * @param burnDownDataMap : burndown map of the sprint
    * @param currentDate Specified date
    * @param endDate End date
    * @throws ParseException
    */
   private void fillRemainingDates(Map<Date, Float> burnDownDataMap, Date currentDate, Date endDate) throws ParseException {
      if (currentDate.compareTo(endDate) == 0) {
         return;
      }
      long noOfDays = getNoOfDays(currentDate, endDate);
      for (int i = 0; i < noOfDays; i++) {
         Date incrementedDate = incrementDatebyOne(currentDate);
         burnDownDataMap.put(incrementedDate, null);
         currentDate = incrementedDate;
      }
   }

   /**
    * The empty days between two consecutive entries in the Map would be filled by incrementing the date by one as key
    * and storing the previous remainingHours/Points as Value.
    * 
    * @param burnDownDataMap Burndown map of the sprint
    * @param startDate : Start date of the sprint
    * @param endDate End date of the sprint
    * @return Updated map
    * @throws ParseException
    */
   private Map<Date, Float> identifyAndFillEmptyDates(Map<Date, Float> burnDownDataMap, Date startDate, Date endDate) throws ParseException {

      LinkedHashMap<Date, Float> updatedMap = new LinkedHashMap<>();
      Set<Entry<Date, Float>> entrySet = burnDownDataMap.entrySet();
      Iterator<Entry<Date, Float>> iterator = entrySet.iterator();
      Date previousDate = null;
      float previousTime = 0;
      boolean isFirst = true;
      while (iterator.hasNext()) {
         Entry<Date, Float> next = iterator.next();
         Date date = next.getKey();
         Float remainingTime = next.getValue();
         if (isFirst) {
            if (date.compareTo(startDate) == 0) {
               updatedMap.put(startDate, remainingTime);
            } else {
               updatedMap.put(date, remainingTime);
            }
            isFirst = false;
         } else {
            if (date.compareTo(previousDate) == 0) {
               updatedMap.put(date, remainingTime);
            } else {
               long noOfDays = getNoOfDays(previousDate, date);
               for (int i = 1; i < noOfDays; i++) {
                  Date incrementedDate = incrementDatebyOne(previousDate);
                  updatedMap.put(incrementedDate, previousTime);
                  previousDate = incrementedDate;
               }
            }
            updatedMap.put(date, remainingTime);
         }
         previousDate = date;
         previousTime = remainingTime;
      }
      return updatedMap;
   }

   /**
    * Increments the date by one day and returns the incremented date.
    * 
    * @param date Current date
    * @return date incremented by one day
    * @throws ParseException
    */
   private Date incrementDatebyOne(Date date) throws ParseException {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.add(Calendar.DATE, 1);
      String newDate = simpleDateFormat.format(calendar.getTime());
      Date incrementedDate = simpleDateFormat.parse(newDate);
      return incrementedDate;
   }
}