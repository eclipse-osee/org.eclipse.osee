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
import { Component, OnInit, Injectable } from '@angular/core';
import { NgbModal, NgbActiveModal, NgbDateStruct, NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import { DashboardService } from '../service/dashboard.service';
import { ProjectService } from '../service/project.service';
import { NgForm } from '@angular/forms';
import { ProjectModel } from '../model/projectModel';
import { FileSelectDirective, FileDropDirective, FileUploader, FileUploaderOptions } from 'ng2-file-upload/ng2-file-upload';
import {  NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { DataserviceService } from '../service/dataservice.service';




/**
 * To Customize date format "dd-MM-YYYY"
 */
@Injectable()
export class NgbICTeamParserFormatter extends NgbDateParserFormatter {



  format(date: NgbDateStruct): string {
    if (date === null) {
      return '';
    }
    return date ?
      `${isNumber(date.day) ? padNumber(date.day) : ''}-${isNumber(date.month) ? padNumber(date.month) : ''}-${date.year}` :
      '';
  }


  parse(value: string): NgbDateStruct {
    if (!value) {
      return null;
    }
    const d = new Date(Number(value));
    return { year: d.getFullYear(), month: d.getMonth() + 1, day: d.getDate() };
  }
}

@Component({
  selector: 'app-release',
  templateUrl: './release.component.html',
  styleUrls: ['./release.component.scss'],
  providers: [{ provide: NgbDateParserFormatter, useClass: NgbICTeamParserFormatter, }]
})
export class ReleaseComponent implements OnInit {
  projectList = [];
  selectedProject: any;
  teamList: any;
  mileStoneList = [];
  userGuid: String;
  projectsDisable: Boolean;
  isUpdate: Boolean;
  release: any;
  selectedSprintForUpdate: any;
  selectedMilestoneForUpdate: any;
  userDetails: any;
  relaseResponse: String;
  isSprintSelected: boolean;
  releaseType: string;
  modalOption: NgbModalOptions = {};
  isUnique = false;
  message: any;
  changeToUpdate: boolean;


  constructor(private modalService: NgbModal, public activeModal: NgbActiveModal,
    private dashboardService: DashboardService, private projectService: ProjectService,
    private ngbDateParserFormatter: NgbDateParserFormatter, private data: DataserviceService) { }

  ngOnInit() {
    this.release = new Map<any, any>();
    this.userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
    this.userGuid = JSON.parse(sessionStorage.getItem('userDetails')).userGuid;
    this.isSprintSelected = false;
    this.changeToUpdate = false;
    if (this.selectedProject) {
      this.release.selectedProject = this.selectedProject;
      this.projectList = new Array(this.selectedProject);
      this.getSelectedProject();
      this.projectsDisable = true;
    } else {

      this.dashboardService.getUserSpecificProjects(this.userGuid).subscribe(
        (result: Response) => {
          this.projectList = result['artifactList'];
        },
        (error) => {
          console.error(error);

        }
      );
    }

  }

  getSelectedProject() {
    console.log(JSON.stringify(this.release.selectedProject));
    console.log(this.release.selectedProject.name);
    console.log(this.release.selectedProject.guid);
    this.selectedProject = this.release.selectedProject;
    this.projectService.getTeamForProjects(this.release.selectedProject.guid).subscribe(
      (result: Response) => {
        this.teamList = result['artifactList'];
      },
      (error) => {
        console.error(error);

      }
    );
    this.renderReleaseForProject();

  }

  renderReleaseForProject() {
    this.projectService.getReleaseForProject(this.release.selectedProject.guid).subscribe(
      (result: Response) => {
        this.mileStoneList = result['artifactList'];
        let slectedMilestoneTeam = this.teamList.filter(team => team.guid === this.mileStoneList[0].relationMap.TeamDefinitionToVersion[0].guid)
        this.release.selectedTeam = slectedMilestoneTeam[0];
      },
      (error) => {
        console.error(error);

      }
    );
  }

  renderSprintinfo(springData: any) {
    this.isUpdate = true;
    this.isSprintSelected = true;
    this.changeToUpdate = true;
    this.message = '';
    // console.log("springData", springData);
    this.selectedSprintForUpdate = springData;
    this.release.releaseName = springData.name;
    this.releaseType = 'Sprint';
    const sprintdate = Date.parse(springData.attributeMap['ats.Release Date'][0].replace('IST', '')).toString();
    this.release.model = this.ngbDateParserFormatter.parse(sprintdate);
    console.log('this.release.model', this.release.model);

  }
  renderMilestoneinfo(milestoneData: any) {
    this.isUpdate = true;
    this.isSprintSelected = false;
    this.changeToUpdate = true;
    this.message = '';
    console.log('milestoneData', milestoneData);
    this.selectedMilestoneForUpdate = milestoneData;
    this.release.releaseName = milestoneData.name;
    const slectedMilestoneTeam = this.teamList.filter(team => team.guid === milestoneData.relationMap.TeamDefinitionToVersion[0].guid)
    this.release.selectedTeam = slectedMilestoneTeam[0];
    console.log(" milestoneData.attributeMap['ats.Release Date']", this.ngbDateParserFormatter.parse(milestoneData.attributeMap['ats.Release Date']));
    const milestonedate = Date.parse(milestoneData.attributeMap['ats.Release Date'][0].replace('IST', '')).toString();
    this.releaseType = 'MileStone';
    this.release.model = this.ngbDateParserFormatter.parse(milestonedate);
    console.log('this.release.model', this.release.model);

  }
  NewSprintInit() {
    this.isUpdate = false;
    this.selectedMilestoneForUpdate = null;
    this.selectedSprintForUpdate = null;
    this.release.releaseName = null;
    this.release.selectedTeam = null;
    this.release.model = null;
    this.changeToUpdate = false;
    this.message = '';
  }

  createSprint(releaseObject: NgForm) {
    console.log(releaseObject);
    this.changeToUpdate = true;
    if (releaseObject.form.valid) {
      const releaseData = new ProjectModel();
      console.log(releaseObject.value);
      releaseData.currentUserId = this.userDetails.username;
      releaseData.currentUserId = this.userDetails.username;
      releaseData.name = releaseObject.form.value['releaseName'];
      releaseData.attributeMap = new Map<any, Array<String>>();
      if (this.selectedSprintForUpdate) {
        releaseData.guid = this.selectedSprintForUpdate.guid;
        releaseData.attributeMap['Name'] = new Array(releaseObject.form.value['releaseName']);
        releaseData.attributeMap['ats.Release Date'] = new Array(this.ngbDateParserFormatter.format(releaseObject.value.dp));
        console.log('releaseData', releaseData);
        this.projectService.updateRelease(releaseData).subscribe(
          result => {
            // this.activeModal.close();
            this.renderReleaseForProject();
            this.NewSprintInit();
            console.log(result);
          },
          error => {
            console.error(error);
          }
        );
      }
      if (this.selectedMilestoneForUpdate) {
        releaseData.guid = this.selectedMilestoneForUpdate.guid;
        releaseData.attributeMap['Name'] = new Array(releaseObject.form.value['releaseName']);
        releaseData.attributeMap['ats.Release Date'] = new Array(this.ngbDateParserFormatter.format(releaseObject.value.dp));
        console.log('releaseData', releaseData);
        this.projectService.updateRelease(releaseData).subscribe(
          result => {
            // this.activeModal.close();
            this.renderReleaseForProject();
            this.NewSprintInit();
            console.log(result);
          },
          error => {
            console.error(error);
          }
        );
      } else {
        releaseData.relationMap = new Map();
        const projectToVersion = new Map<String, String>();
        projectToVersion['guid'] = this.selectedProject.guid;
        if (!(this.releaseType === 'Sprint')) {
        const teamDefinitionToVersion = new Map<String, String>();
        teamDefinitionToVersion['guid'] = releaseObject.value.team.guid;
        releaseData.relationMap["RelationTypeSide - uuid=[2305843009213694320] type=[TeamDefinitionToVersion] side=[SIDE_A]"] = new Array(teamDefinitionToVersion);
      }
        releaseData.relationMap["RelationTypeSide - uuid=[2305843009214812513] type=[ProjectToVersion] side=[SIDE_A]"] = new Array(projectToVersion);
        // releaseData.attributeMap["ats.Baseline Branch Guid"] = new Array(this.selectedProject.attributeMap["ats.Baseline Branch Guid"][0]);
        releaseData.attributeMap['ats.Description'] = new Array('Just Check');
        if (this.releaseType === 'Sprint') {
          releaseData['parentGuid'] = releaseObject.value.mileStone;
        }
        releaseData.attributeMap['ats.Release Date'] = new Array(this.ngbDateParserFormatter.format(releaseObject.value.dp));
        console.log(releaseData);

        this.projectService.createRelease(releaseData).subscribe(
          result => {
            // this.activeModal.close();
            const status = result['list'][0].attributeMap['Status'][0];
            if (status === 'Success' ) {
              this.isUnique = true;
            this.data.addRelease(result['artifactList']);
            this.renderReleaseForProject();
            this.NewSprintInit();
            } else {
              this.isUnique = false;
              this.message = result['list'][0].attributeMap['Message'][0];
            }
            console.log(result);
          },
          error => {
            console.error(error);
          }
        );
      }
    }

  }


  releaseSprint() {
    this.changeToUpdate = false;
      const releaseData = new ProjectModel();

      releaseData.attributeMap = new Map<any, Array<String>>();
      if (this.selectedSprintForUpdate) {
        releaseData.guid = this.selectedSprintForUpdate.guid;

        releaseData.attributeMap['ats.Released'] = new Array('true');
        this.relaseResponse = '';
        console.log('releaseData', releaseData);
        this.projectService.releaseRelease(releaseData).subscribe(
          result => {

            const response =  result['list'][0].attributeMap['isReleasable'][0];
            if (response === 'false') {
              this.relaseResponse = 'Cannot Close Release, Release has some open task';
            }
            console.log(result);
          },
          error => {
            console.error(error);
          }
        );
      }

  }

  onMileStoneSelection(release: string) {
    if (release === 'MileStone') {
      this.releaseType = release;
    }
    this.changeToUpdate = false;
  }

  onSprintSelection(release: string) {
    if (release === 'Sprint') {
      this.releaseType = release;
    }
    this.changeToUpdate = false;
  }

}

export function isNumber(value: any): value is number {
  return !isNaN(toInteger(value));
}

export function padNumber(value: number) {
  if (isNumber(value)) {
    return `0${value}`.slice(-2);
  } else {
    return '';
  }
}

export function toInteger(value: any): number {
  return parseInt(`${value}`, 10);
}
