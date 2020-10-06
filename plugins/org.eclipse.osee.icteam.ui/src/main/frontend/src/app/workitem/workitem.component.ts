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
import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../service/dashboard.service';
import { ProjectService } from '../service/project.service';
import { WorkitemService } from '../service/workitem.service';
import { NgbModal, ModalDismissReasons, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ProjectModel } from '../model/projectModel';
import { Router, ActivatedRoute, Params, NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-workitem',
  templateUrl: './workitem.component.html',
  styleUrls: ['./workitem.component.css']
})
export class WorkitemComponent implements OnInit {
  projectList = [];
  packageList = [];
  releaseList = [];
  taskTypeList = [];
  priorityList = [];
  selectedProject;
  selectedPackage;
  selectedTaskType;
  selectedPriority;
  taskName;
  taskDesc;
  taskData: any;
  projectsDisable: Boolean;

  assigneeDropdownList = [];
  dropdownList = [];
  // selectedItems = [];
  selectedUser : String;
  // dropdownSettings = {};

  taskResponse: boolean;
  userGuid: String;
  userDetails: any;
  sprintsList: any;
  selectedRelease:any;
  selectedStory:any;

  workspaceArtifactGuid : any;
  guid=null;
  branchGuid : any;
  workspaceArtifactBranchGuid : any;
  selectedProjectFromArtifact : any;

  taskGuid = null;
  selectedProjectFromTask :any;



  constructor(private dashboardService: DashboardService, private projectService: ProjectService,
    public activeModal: NgbActiveModal, private workitemService: WorkitemService,
    private route: ActivatedRoute, private router: Router  ) {
  }

  ngOnInit() {
    this.userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
    this.userGuid = JSON.parse(sessionStorage.getItem('userDetails')).userGuid;
    if(this.guid!=null){
    this.workspaceArtifactGuid=this.guid;
    this.workspaceArtifactBranchGuid=this.branchGuid;
    this.selectedProject=this.selectedProjectFromArtifact;
    }
    if(this.taskGuid!=null){
      this.selectedProject=this.selectedProjectFromTask;
    }
    // this.selectedItems = [];
    // this.dropdownSettings = {
    //   singleSelection: false,
    //   text: "Select Users",
    //   selectAllText: 'Select All',
    //   unSelectAllText: 'UnSelect All',
    //   enableSearchFilter: true,
    //   classes: "col-sm-7 myclass custom-class"
    // };

    if (this.selectedProject) {
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



    this.workitemService.getTaskTypeAndPriorities('1152921504606851584').subscribe(
      (result: Response) => {
        this.taskTypeList = result['artifactList'];
      },
      (error) => {
        console.error(error);

      }
    );

    this.workitemService.getTaskTypeAndPriorities('1152921504606847179').subscribe(
      (result: Response) => {
        this.priorityList = result['artifactList'];
      },
      (error) => {
        console.error(error);

      }
    );

  }
  getSelectedProject() {
    this.dropdownList = [];
    // this.selectedItems = [];

    this.projectService.getPackagesForProjects(this.selectedProject.guid).subscribe(
      (result: Response) => {
        this.packageList = result['artifactList'];
      },
      (error) => {
        console.error(error);

      }
    );
    this.projectService.getOpenReleaseForProject(this.selectedProject.guid).subscribe(
      (result: Response) => {
        this.releaseList = result['list'];
      },
      (error) => {
        console.error(error);

      }
    );
  }

  getSelectedPackage() {
    this.dropdownList = [];
    // this.selectedItems = [];

    this.workitemService.getTeamMembers(this.selectedPackage.guid).subscribe(
      (result: Response) => {
        this.assigneeDropdownList = result['artifactList'];
        console.log(this.assigneeDropdownList);
        let i = 1;

        this.assigneeDropdownList.forEach(element => {
          if (element.artifactType == "User") {
            let nameList = {};
            nameList["id"] = element.attributeMap["User Id"][0];
            nameList["itemName"] = element.name;
            this.dropdownList.push(nameList);
            i++;
          }
        });
        // this.selectedUser=this.dropdownList[0].itemName;
      },
      (error) => {
        console.error(error);
      }
    );
  }



  createTask() {
    const urlData = location.origin;
    console.log(urlData);
    this.taskData = new ProjectModel();
    this.taskData.currentLoggedInUser = this.userDetails.username;
    this.taskData.currentUserId = this.userDetails.username;
    this.taskData.name = this.taskName;
    this.taskData.attributeMap = new Map<any, Array<String>>();
    let userString = "New;";
    
    // for (let i = 0; i <= this.selectedItems.length - 1; i++) {
      this.dropdownList.forEach(element => {
        if(this.selectedUser === element.itemName){
          userString = userString + "<" + element.id + ">"
        }
      });
     
    // }
    userString = userString + ";;"
    this.taskData.urlinfo = urlData;
    this.taskData.attributeMap["1152921504606847192"] = new Array(userString);
    this.taskData.attributeMap["1152921504606851584"] = new Array(this.selectedTaskType.name);
    this.taskData.attributeMap["1152921504606847088"] = new Array(this.taskName);
    this.taskData.attributeMap["1152921504606847174"] = new Array(this.userDetails.username);
    this.taskData.attributeMap["1152921504606847196"] = new Array(this.taskDesc);
    this.taskData.attributeMap["1152921504606847201"] = new Array(this.selectedPackage.relationMap.TeamActionableItem[0].guid);
    this.taskData.attributeMap["1152921504606847179"] = new Array(this.selectedPriority.name);
    this.taskData.attributeMap["1152921504606847200"] = new Array(this.selectedPackage.guid);
    if(this.selectedStory === undefined){

    }
    else{
      this.taskData.attributeMap["1152921573057888257"] = new Array(this.selectedStory.toString());
    }
    
    this.taskData.attributeMap["raplink"] = new Array<String>("");

    this.taskData.relationMap = new Map();
    const relationMapRelated = new Array<Map<String, String>>()
    const mapdata = new Map();

    mapdata["artifactType"] = "Actionable Item";
    mapdata["guid"] = this.selectedPackage.guid;

    relationMapRelated.push(mapdata);

    this.taskData.relationMap["RelationTypeSide - uuid=[2305843009213694467] type=[ActionableItemWorkFlow] side=[SIDE_A]"] = relationMapRelated;
    const relationMapRelated2 = new Array<Map<String, String>>()
    const mapdata2 = new Map();

    mapdata2["branchGuid"] = this.selectedProject.branchGuid;
    mapdata2["guid"] = this.selectedProject.guid;
    const attrMap = new Map<String, Array<any>>();
    attrMap["Shortname"] = new Array(String(this.selectedProject.attributeMap.Shortname));
    attrMap["TaskCountForProject"] = new Array(String(this.selectedProject.attributeMap.TaskCountForProject));
    attrMap["Name"] = new Array(this.selectedProject.name);
    mapdata2["attributeMap"] = attrMap;
    relationMapRelated2.push(mapdata2);
    this.taskData.relationMap["RelationTypeSide - uuid=[2305843009214812512] type=[ProjectToTeamWorkFlow] side=[SIDE_A]"] = relationMapRelated2;


    if(this.workspaceArtifactGuid && this.workspaceArtifactBranchGuid){
      this.taskData.attributeMap["ArtifactGuid"]=new Array(String(this.workspaceArtifactGuid));
      this.taskData.attributeMap["ArtifactBranchGuid"]=new Array(String(this.workspaceArtifactBranchGuid));
    }

    if (this.selectedRelease !=  null) {
      if(this.selectedRelease=="Product_Backlog"){
        this.taskData.attributeMap["Product_Backlog"]=new Array(String("Product_Backlog"));
      }
      const relationMapRelated3 = new Array<Map<String, String>>();
      const mapdata3 = new Map();
      mapdata3['artifactType'] = 'Version';
      mapdata3['guid'] = this.selectedRelease.guid;
      relationMapRelated3.push(mapdata3);
      this.taskData.relationMap['RelationTypeSide - uuid=[2305843009213694319] type=[TeamWorkflowTargetedForVersion] side=[SIDE_B]'] = relationMapRelated3;
    }
    console.log(this.taskData);
    if(this.taskGuid!=null){
      this.taskData.attributeMap["CreateTaskFromLinkGuid"]=new Array(this.taskGuid);
    }
    this.workitemService.save(this.taskData).subscribe(
      result => {
        this.activeModal.close("newTaskCrated");
      },
      error => {
        console.error("Error->While saving task to database", error);
        this.taskResponse = true;
      })

  }


}
