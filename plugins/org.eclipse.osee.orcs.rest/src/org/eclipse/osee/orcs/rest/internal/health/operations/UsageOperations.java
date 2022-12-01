/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.activity.api.ActivityEntry;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class UsageOperations {

   private static final String ALL_USERS = "All Users";
   private final JdbcClient jdbcClient;
   private final OrcsApi orcsApi;
   private final Pattern verPattern = Pattern.compile("\"version\":\"(.*?)\"");
   private final HashCollectionSet<String, String> byCategory = new HashCollectionSet<>();

   public UsageOperations(OrcsApi orcsApi, JdbcClient jdbcClient) {
      this.orcsApi = orcsApi;
      this.jdbcClient = jdbcClient;
   }

   public String getUsageHtml(String months) {
      Collection<ActivityEntry> logMsgs = getUsageLogEntries(months);
      return getHtml(String.format("OSEE usage in past %s month(s) as of %s", months, DateUtil.getDateNow()), logMsgs);
   }

   private String getHtml(String title, Collection<ActivityEntry> logMsgs) {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(2, title));
      StringBuilder usageSb = addUsageTable(logMsgs);
      addCategoryTables(sb);
      sb.append("<br/><br/>");
      sb.append(AHTML.heading(3, "By Session"));
      sb.append(usageSb.toString());
      sb.append("<br/><br/>");
      return sb.toString();
   }

   private void addCategoryTables(StringBuilder sb) {
      sb.append(AHTML.heading(3, "All Users"));
      extracted(sb, ALL_USERS, byCategory.getValues(ALL_USERS), "Users");

      sb.append(AHTML.heading(3, "By Release Type"));
      for (String category : Arrays.asList("-DEV", "Development", "-REL", "-NR", "local")) {
         handleCategory(sb, category);
      }

      sb.append(AHTML.heading(3, "By Release Version"));
      for (Entry<String, Set<String>> entry : byCategory.entrySet()) {
         if (!entry.getKey().equals(ALL_USERS)) {
            extracted(sb, entry.getKey(), entry.getValue(), "Release Version");
         }
      }
   }

   private void handleCategory(StringBuilder sb, String category) {
      Set<String> values = new HashSet<>();
      for (Entry<String, Set<String>> entry : byCategory.entrySet()) {
         if (entry.getKey().contains(category)) {
            values.addAll(entry.getValue());
         }
      }
      extracted(sb, category, values, "Release Type");
   }

   private void extracted(StringBuilder sb, String category, Set<String> values, String categoryName) {
      sb.append(AHTML.heading(4, String.format("---  %s: %s - Count: %s", categoryName, category, values.size())));
      sb.append("Users: " + org.eclipse.osee.framework.jdk.core.util.Collections.toString("; ", values));
   }

   private StringBuilder addUsageTable(Collection<ActivityEntry> logMsgs) {
      StringBuilder usageSb = new StringBuilder();
      usageSb.append(AHTML.beginMultiColumnTable(95, 1));
      usageSb.append(
         AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Date", "Account", "User", "UserId", "Version", "Msg")));
      for (ActivityEntry entry : logMsgs) {
         UserToken user = orcsApi.userService().getUser(entry.getAccountId());
         String version = "Unknown";
         Matcher m = verPattern.matcher(entry.getMessageArgs());
         if (m.find()) {
            version = m.group(1);
         }
         byCategory.put(version, user.getName());
         byCategory.put(ALL_USERS, user.getName());
         usageSb.append(AHTML.addRowMultiColumnTable(entry.getStartTimestamp().toString(),
            entry.getAccountId().toString(), user.getName(), user.getUserId(), version, entry.getMessageArgs()));
      }
      usageSb.append(AHTML.endMultiColumnTable());
      return usageSb;
   }

   private Collection<ActivityEntry> getUsageLogEntries(String months) {
      Calendar cal = Calendar.getInstance();
      int minusMonths = Integer.valueOf(months) * -1;
      cal.add(Calendar.MONTH, minusMonths);
      Date date = cal.getTime();
      String query =
         "select * from osee_activity where type_id = " + CoreActivityTypes.IDE.getIdString() + " and trunc(start_timestamp) > '" + DateUtil.get(
            date, DateUtil.DD_MMM_YYYY) + "' and msg_args like '%Session Created%' order by start_timestamp desc";
      return getLogEntries(query);
   }

   private Collection<ActivityEntry> getLogEntries(String query) {
      List<ActivityEntry> logMsgs = new ArrayList<>();
      System.err.println("query: " + query);
      Consumer<JdbcStatement> consumer = stmt -> {
         ActivityEntry entry = new ActivityEntry(stmt.getLong(1));
         entry.setAccountId(stmt.getLong("account_id"));
         entry.setClientId(stmt.getLong("client_id"));
         entry.setDuration(stmt.getLong("duration"));
         entry.setMessageArgs(stmt.getString("msg_args"));
         entry.setParentId(stmt.getLong("parent_id"));
         entry.setServerId(stmt.getLong("server_id"));
         entry.setStartTime(stmt.getLong("start_time"));
         entry.setStartTimestamp(stmt.getTimestamp("start_timestamp"));
         entry.setStatus(stmt.getInt("status"));
         entry.setTypeId(stmt.getLong("type_id"));
         logMsgs.add(entry);
      };
      jdbcClient.runQuery(consumer, query);
      return logMsgs;
   }

}
