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
import { Component, OnInit, OnDestroy } from '@angular/core';
import { DashboardService } from '../service/dashboard.service';
import { ProjectService } from '../service/project.service';
import { NgbModal, ModalDismissReasons, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ProjectModel } from '../model/projectModel';
import { PackageService } from '../service/package.service';

@Component({
  selector: 'app-package',
  templateUrl: './package.component.html',
  styleUrls: ['./package.component.scss']
})
export class PackageComponent implements OnInit, OnDestroy {

  projectList = [];
  teamList = [];
  selectedProject: any;
  selectedTeam: any;
  packageName: any;
  packageData: any;
  packageResponse: Boolean;
  userGuid: String;
  packageList: any;
  projectsDisable: Boolean;
  packageGuid: String;
  selectedPackage: any;
  userDetails: any;
  teamDisable: Boolean;

  constructor(private dashboardService: DashboardService, private projectService: ProjectService,
    public activeModal: NgbActiveModal, private packageService: PackageService) {

  }
  ngOnDestroy(): void {
    this.activeModal.close();
    // throw new Error("Method not implemented.");
  }

  ngOnInit() {
    this.userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
    this.userGuid = JSON.parse(sessionStorage.getItem('userDetails')).userGuid;
    this.packageResponse = false;
    console.log("selectedProject", this.selectedProject);

    if (this.selectedProject) {
      this.projectList = new Array(this.selectedProject);
      this.projectsDisable = true;
      this.getSelectedProject();
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

    this.getAllPackage();
  }

  getAllPackage() {
    this.projectService.getPackagesForProjects(this.selectedProject.guid).subscribe(
      (result: Response) => {
        this.packageList = result['artifactList'];
        const selectedPackageTeam = this.teamList.filter(team =>
          team.guid === this.packageList[0].relationMap.TeamActionableItem[0].guid
        );
        this.selectedTeam = selectedPackageTeam[0];
        if (this.selectedTeam) {
          this.teamDisable = true;
        }
      },
      (error) => {
        console.error(error);

      }
    );
  }

  getSelectedProject() {
    console.log(JSON.stringify(this.selectedProject));
    console.log(this.selectedProject.name);
    console.log(this.selectedProject.guid);

    this.projectService.getTeamForProjects(this.selectedProject.guid).subscribe(
      (result: Response) => {
        this.teamList = result['artifactList'];
        this.getAllPackage();
      },
      (error) => {
        console.error(error);

      }
    );

  }

  getSelectedTeam() {
    console.log(this.selectedTeam);

    console.log(this.selectedTeam.name);
    console.log(this.selectedTeam.guid);
  }

  renderSelectedPackage(packageData: any) {
    this.selectedPackage = packageData;
    console.log("package", packageData);
    this.packageGuid = packageData.guid;
    this.packageName = packageData.name;
    let selectedPackageTeam = this.teamList.filter(team =>
      team.guid === packageData.relationMap.TeamActionableItem[0].guid
    );
    this.selectedTeam = selectedPackageTeam[0];
    console.log("this.selectedTeam ", this.selectedTeam);


  }
  NewPackageInit() {
    this.packageGuid = null;
    this.selectedPackage = null;
    this.packageName = null;
    this.selectedTeam = null;
  }

  createPackage() {
    console.log(this.packageName);
    console.log(this.selectedProject.name);
    console.log(this.selectedTeam.name);


    this.packageData = new ProjectModel();
    this.packageData.attributeMap = new Map();
    this.packageData.currentLoggedInUser = this.userDetails.username;
    this.packageData.currentUserId = this.userDetails.username;

    if (this.selectedPackage) {
      this.packageData.attributeMap['1152921504606847088'] = new Array(this.packageName);
      this.packageData.guid = this.selectedPackage.guid;
      this.packageService.update(this.packageData).subscribe(
        result => {
          this.getAllPackage();
          this.NewPackageInit();
          // this.activeModal.close();
        },
        error => {
          console.error("Error->While saving package to database", error);
          this.packageResponse = true;
        })
    } else {
      this.packageData.relationMap = new Map();
      const relationMapRelated = new Array<Map<String, String>>();
      const mapdata = new Map();
      mapdata["artifactType"] = "Team Definition";
      mapdata["guid"] = this.selectedTeam.guid;
      mapdata["name"] = this.selectedTeam.name;
      relationMapRelated.push(mapdata);

      this.packageData.relationMap["RelationTypeSide - uuid=[2305843009213694316] type=[TeamActionableItem] side=[SIDE_A]"] = relationMapRelated;

      this.packageData.name = this.packageName;

      console.log(this.packageData);
      this.packageService.save(this.packageData).subscribe(
        result => {
          this.packageList.push(result['artifactList'][0]);
          this.NewPackageInit();
          // this.activeModal.close();
        },
        error => {
          console.error("Error->While saving package to database", error);
          this.packageResponse = true;
        })
    }


  }

}
