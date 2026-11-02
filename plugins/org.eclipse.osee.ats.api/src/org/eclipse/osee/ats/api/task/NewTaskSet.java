/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.task;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class NewTaskSet {

   private List<NewTaskData> taskDatas = new LinkedList<>();
   private XResultData results;
   private String commitComment;
   private String asUserId;
   private TransactionToken transaction = TransactionToken.SENTINEL;

   public NewTaskSet() {
      results = new XResultData();
   }

   public List<NewTaskData> getNewTaskDatas() {
      return taskDatas;
   }

   public void setTaskDatas(List<NewTaskData> taskDatas) {
      this.taskDatas = taskDatas;
   }

   public String getAsUserId() {
      return asUserId;
   }

   public void setAsUserId(String asUserId) {
      this.asUserId = asUserId;
   }

   public void add(NewTaskData newTaskData) {
      taskDatas.add(newTaskData);
   }

   @Override
   public String toString() {
      return "newTaskSet [datas=" + taskDatas + "]";
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public String getCommitComment() {
      return commitComment;
   }

   public void setCommitComment(String commitComment) {
      this.commitComment = commitComment;
   }

   public NewTaskData getTaskData() {
      return getNewTaskDatas().iterator().next();
   }

   public boolean isErrors() {
      return results.isErrors();
   }

   public boolean isSuccess() {
      return !isErrors();
   }

   public static NewTaskSet create(NewTaskData newTaskData, String commitComment, UserToken user) {
      return create(newTaskData, commitComment, user.getUserId());
   }

   public static NewTaskSet create(String commitComment, AtsUser user) {
      return create(commitComment, user.getUserId());
   }

   public static NewTaskSet create(String commitComment, String asUserId) {
      NewTaskSet newTaskSet = new NewTaskSet();
      newTaskSet.setAsUserId(asUserId);
      newTaskSet.setCommitComment(commitComment);
      return newTaskSet;
   }

   public static NewTaskSet create(String commitComment, UserToken user) {
      return create(commitComment, user.getUserId());
   }

   public static NewTaskSet create(NewTaskData newTaskData, String commitComment, String asUserId) {
      NewTaskSet newTaskSet = new NewTaskSet();
      newTaskSet.setAsUserId(asUserId);
      newTaskSet.setCommitComment(commitComment);
      newTaskSet.getNewTaskDatas().add(newTaskData);
      return newTaskSet;
   }

   /**
    * @return NewTaskSet with single NewTaskData
    */
   public static NewTaskSet createWithData(String commitComment, Long teamWfId, String asUserId) {
      NewTaskSet newTaskSet = create(commitComment, asUserId);
      NewTaskData newTaskData = new NewTaskData();
      newTaskSet.getNewTaskDatas().add(newTaskData);
      newTaskData.setTeamWfId(teamWfId);
      return newTaskSet;
   }

   /**
    * @return NewTaskSet with single NewTaskData
    */
   public static NewTaskSet createWithData(IAtsTeamWorkflow teamWf, String commitComment, AtsUser atsUser) {
      return createWithData(commitComment, teamWf.getId(), atsUser.getUserId());
   }

   public TransactionToken getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionToken transaction) {
      this.transaction = transaction;
   }

}
