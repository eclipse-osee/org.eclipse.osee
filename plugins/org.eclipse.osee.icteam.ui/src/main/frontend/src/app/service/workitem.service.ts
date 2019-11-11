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
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpEvent } from '@angular/common/http';
import { ResponseContentType, Http, RequestOptions } from '@angular/http';

@Injectable()
export class WorkitemService {

  constructor(private http: HttpClient) {

  }

  getTaskTypeAndPriorities(attrName: String) {
    return this.http.post('service?url=/getproject/GeneralArtifact/enumeration/values/test', attrName);
  }

  getTeamMembers(guid: String) {
    return this.http.post('service?url=/getproject/Components/teamsuser', guid);
  }

  save(taskData: any) {
    console.log(taskData);
    return this.http.post('service?url=/getproject/tasks/createTask', taskData);
  }

  getAllTaskOFUserPerSprint(artifact: any) {
    return this.http.post('service?url=/getproject/tasks/tasksforuserAndSprint', artifact);
  }

  getAllTaskForBackLog(artifact: any) {
    return this.http.post('service?url=/getproject/tasks/tasksforBackLog', artifact);
  }

  updateTaskStatus(artifact: any) {
    return this.http.post('service?url=/getproject/tasks/transitionWeb', artifact);
  }

  updateUser(taskData: any) {
    return this.http.put('service?url=/getproject/tasks/taskupdateweb', taskData);
  }

  saveComment(commmntArtifact: any) {
    return this.http.put('service?url=/getproject/tasks/updateCommentForTWWeb', commmntArtifact);
  }

  getTaskDetails(task: any) {
    return this.http.post('service?url=/getproject/tasks/getTaskDetailsWeb', task);
  }

  updateTask(attachment: any) {
    return this.http.put('service?url=/getproject/tasks/taskupdateweb', attachment);
  }

  getTaskInfoForLinking(task:any){
    return this.http.put('service?url=/getproject/tasks/taskInfoForLinking',task);
  }

  linkTasks(task:any){
    return this.http.put('service?url=/getproject/tasks/linkTasks',task);
  }
  getAllProjectsForTaskLinking(task:any){
    return this.http.put('service?url=/getproject/tasks/getAllProjectsForTaskLinking',task);
  }
  getselectedProject(task:any){
    return this.http.put('service?url=/getproject/tasks/getselectedProject',task);
  }
  getAllTasksLinked(task:any){
    return this.http.put('service?url=/getproject/tasks/getAllTasksLinked',task);
  }
  deleteLink(task:any){
    return this.http.put('service?url=/getproject/tasks/deleteLink',task);
  }
  
}
