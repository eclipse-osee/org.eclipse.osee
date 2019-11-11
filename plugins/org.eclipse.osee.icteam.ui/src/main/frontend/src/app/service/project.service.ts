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
import { IProject } from '../model/iproject';
import { ProjectModel } from '../model/projectModel';
import { HttpClient, HttpHeaders, HttpEvent } from '@angular/common/http';

@Injectable()
export class ProjectService {

  constructor(private http: HttpClient) { }

  save(projectData: any) {
    console.log(projectData);
    return this.http.post('service?url=/getproject/projects/createproject', projectData);
  }

  getTeamForProjects(guid: String) {
    return this.http.post('service?url=/getproject/Teams/teamsForProject', guid);
  }

  createRelease(releaseData: any) {
    return this.http.post('service?url=/getproject/Releases/CreateReleaseWeb', releaseData);
  }

  getPackagesForProjects(guid: String) {
    return this.http.post('service?url=/getproject/projects/componentforproject', guid);
  }

  getReleaseForProject(projectGuid: String) {
    return this.http.post('service?url=/getproject/projects/releaseWeb', projectGuid);
  }

  getOpenReleaseForProject(projectGuid: String) {
    return this.http.post('service?url=/getproject/Releases/openReleaseWeb', projectGuid);
  }

  getCloseReleaseForProject(projectGuid: String) {
    return this.http.post('service?url=/getproject/Releases/closeReleaseWeb', projectGuid);
  }

  getProjectByUuid(projectGuid: String) {
    return this.http.post('service?url=/getproject/projects/uuidWeb', projectGuid);
  }

  updateRelease(release: any) {
    return this.http.put('service?url=/getproject/Releases/updateReleaseWeb', release);
  }

  releaseRelease(release: any) {
    return this.http.post('service?url=/getproject/Releases/releaseReleaseWeb', release);
  }
  getSprintByUuid(release: any):any {
    return this.http.post('service?url=/getproject/Releases/getSprintByUuid', release);
  }



}
