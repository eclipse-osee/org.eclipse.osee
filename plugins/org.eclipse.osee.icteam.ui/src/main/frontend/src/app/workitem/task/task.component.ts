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
import { Component, OnInit, Injectable, ViewChild } from '@angular/core';
import { NgClass, NgStyle } from '@angular/common';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { WorkitemService } from '../../service/workitem.service';
import { ProjectModel } from '../../model/projectModel';
import { FileSelectDirective, FileDropDirective, FileUploader, FileUploaderOptions } from 'ng2-file-upload/ng2-file-upload';
import { NgbModal, ModalDismissReasons, NgbActiveModal, NgbDateStruct, NgbDateParserFormatter,NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { CookieService } from 'ngx-cookie-service';
import { NgForm } from '@angular/forms';
import { IMultiSelectOption } from '../../utils/dropdown/types';
import { IMultiSelectSettings } from '../../utils/dropdown/types';
import { IMultiSelectTexts } from '../../utils/dropdown/types';
import { MultiselectDropdownComponent } from '../../utils/dropdown/dropdown.component';
import {LinkTaskComponent} from '../task/link-task/link-task.component';
import { WorkitemComponent } from '../../workitem/workitem.component';
import { ProjectService } from '../../service/project.service';


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
    return null;
  }
}


@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss'],
  providers: [{ provide: NgbDateParserFormatter, useClass: NgbICTeamParserFormatter }]
})
export class TaskComponent implements OnInit {

  @ViewChild('taskForm') taskForm;


  taskId: any;
  taskDetails: any;
  expecteddate: any;
  projectId: any;
  attchmentlist: any;
  isSaveDisable: Boolean;
  userDetails: any;
  modalOption: NgbModalOptions = {};
  selectedUser :any;
  userListBeforeFilter: any;
  isTaskPageisLoading: Boolean;
  commentdetails: any;
  linklist: any;
  linkedTaskGuid:any;
  taskID: String;
  selectedProject :any;
  usersList =[];


  // Settings configuration
  usersListSettings: IMultiSelectSettings = {
    enableSearch: true,
    checkedStyle: 'fontawesome',
    buttonClasses: 'btn btn-sm fa fa-user',
    dynamicTitleMaxItems: 1,
    //selectionLimit: 1,
    autoUnselect: true,
    displayAllSelectedText: false
    //showCheckAll: true,
    // showUncheckAll: true
  };

  // Text configuration
  usersListTexts: IMultiSelectTexts = {
    checkAll: 'Select all',
    uncheckAll: 'Unselect all',
    checked: '',
    checkedPlural: '',
    searchPlaceholder: 'Search',
    defaultTitle: '',
    allSelected: 'All selected',
  };

  // usersList: IMultiSelectOption[];


  constructor(private route: ActivatedRoute, private workitemService: WorkitemService,private ngbDateParserFormatter: NgbDateParserFormatter, private cookieService: CookieService,private modalService: NgbModal,private projectService: ProjectService) {
  }
  ngAfterViewInit() {

  }

  onChanges(): void {
    this.taskForm.form.valueChanges.subscribe((change) => {
      console.log(change);
      this.isSaveDisable = false;
    });

  }

  ngOnInit() {
    //this.isFooterEnable = false;
    this.userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
    this.isSaveDisable = true;
    this.isTaskPageisLoading = true;

  

    //overide the onCompleteItem property of the uploader so we are
    //able to deal with the server response.
    console.log(this.cookieService.getAll());

    this.route.params.subscribe((taskview: Params) => {
      const rege = /%2(52)*B/g;
      this.taskId = taskview.id
      this.projectId = taskview.projectId;
      this.projectId = this.projectId.replace(rege, '+');
      console.log("this.taskId", this.taskId);
      let task: ProjectModel = new ProjectModel();
      this.taskId = this.taskId.replace(rege, '+');
      task.guid = this.taskId;
      this.workitemService.getTaskDetails(task).subscribe((result) => {
        console.log("Task Details", result);
        this.isTaskPageisLoading = false;
        this.taskDetails = result;

        let date = new Date(this.taskDetails.expectedDate);
        let userList = this.taskDetails.toStateAssignee;

        let shortName = this.taskDetails.attributeMap['Shortname'][0];
        let workPackageId = this.taskDetails.attributeMap['WorkPackage'][0];
        this.taskID = shortName + '-' + workPackageId;

       

        console.log("Users for task", userList);
        // this.model = date.getDate() + '-' + date.getMonth() + 1 + '-' + date.getFullYear();

        this.taskDetails.assignee.forEach(member => {
          this.selectedUser =member.name;

        })
        this.userListBeforeFilter = this.taskDetails.listTeamMembersTeamLeads;
        console.log("User list before filter---->", this.userListBeforeFilter);
        let userData = [];
        this.userListBeforeFilter.forEach(element => {
          console.log("Inside for loop===", element);
          let nameList = {};
          nameList['id'] = element.attributeMap['User Id'][0];
          nameList['name'] = element.name;
          userData.push(nameList);
        });
        this.usersList = userData;



        this.expecteddate = {
          "year": date.getFullYear(),
          "month": date.getMonth() + 1,
          "day": date.getDate()
        };
        setTimeout(() => {
          this.onChanges();
        }, 2000)

        this.taskDetails.attributeMap.states.push(this.taskDetails.currentState);

        if (!this.taskDetails.version) {
          this.taskDetails['version'] = {}
          this.taskDetails.version.guid = "Product_Backlog";
        }
      }, (error: any) => {
        console.error("Error:: While try to get task details", error);
      });

    });
    

    this.projectService.getProjectByUuid(this.projectId).subscribe(
      (result: Response) => {
          console.log(result);
          if (result) {
              this.selectedProject = result['artifactList'][0];
          }
          console.log(' this.selectedProject', this.selectedProject);
      },
      (error) => {
          console.error(error);
      }
  );
  }

  onUserSelect() {
    //console.log("Inside select user", item);
    console.log(this.selectedUser);
  }

  assignUser(item: any) {
    console.log("Inside assign user");
    console.log(this.selectedUser);
  }

  openDialog(){
    const activeModal =this.modalService.open(LinkTaskComponent, this.modalOption);
    activeModal.componentInstance.guid = this.taskId;
  }

  formateTheData(data: any, itemValue: any) {
    if (itemValue === 'expecteddate') {
      this.taskDetails.expectedDate = this.ngbDateParserFormatter.format(data);
    }
  }

  submitCommnet(commetData: any) {
    let commmntArtifact: ProjectModel = new ProjectModel();
    commmntArtifact.guid = this.taskDetails.guid;
    commmntArtifact.attributeMap = new Map<any, any>();
    commmntArtifact.attributeMap['userID'] = new Array(this.userDetails.username);
    commmntArtifact.attributeMap['comment'] = new Array<String>(commetData);
    commmntArtifact.attributeMap['currentState'] = new Array<String>(this.taskDetails.currentStateString);
    commmntArtifact.attributeMap['isResponse'] = new Array<String>('true');
    this.workitemService.saveComment(commmntArtifact).subscribe(
      (response: any) => {
        console.log("Response", response);
        this.taskDetails.commentArtifactList = response.commentArtifactList;
        this.commentdetails = undefined;
      },
      (error: any) => {
        console.error("Error while saving comment", error);
      }
    );
  }

 


  




 



  getAllTaskLinks(taskGuid: any) {
    let task: ProjectModel = new ProjectModel();
    task.guid = taskGuid;
    this.workitemService.getAllTasksLinked(task).subscribe((result) => {
      // let linklistTemp:any;
      // linklistTemp = result['listTeamWorkFlow'];
      this.linklist=result['listTeamWorkFlow'];
    //   linklistTemp.forEach(element => {
    //     this.linklist.push(element);
    //  });
    }, (error) => {
      console.error(error);
    })

  }

  deleteLink(link: any) {
    let task: ProjectModel = new ProjectModel();
    task.guid = link.guid;
    task.parentGuid=this.taskId;
    this.workitemService.deleteLink(task).subscribe((result) => {
      let index = this.linklist.indexOf(link);
      if (index > -1) {
        this.linklist.splice(index, 1);
      }

    }, (error) => {
      console.error(error);
    })

  }

  createTaskFromLink(){
    const activeModal=this.modalService.open(WorkitemComponent,this.modalOption);
    activeModal.componentInstance.taskGuid = this.taskDetails.guid;
    activeModal.componentInstance.selectedProjectFromTask = this.selectedProject;
  }


  onSubmit(taskData: NgForm) {
    console.log("taskData", taskData);
    console.log("taskData", taskData.form.value);
    let taskDetails: ProjectModel = new ProjectModel();
    taskDetails.guid = this.taskId;
    taskDetails.currentUserId = this.userDetails.username;
    taskDetails.currentLoggedInUser = this.userDetails.username;
    taskDetails.attributeMap = new Map<any, Array<String>>();
    taskDetails.relationMap = new Map<any, Array<any>>();
    let userString: String;
    let selectedUserId : String;
    for (let key in taskData.form.value) {
      console.log(key, "value", taskData.form.value[key]);
      if (key === "1152921504606847192") {
        taskDetails.parentGuid = this.projectId;
        // taskDetails.attributeMap[key] = taskDetails.attributeMap[key];
        taskDetails.attributeMap['reason'] = new Array("");
        taskDetails.attributeMap['toState'] = new Array(taskData.form.value[key]);
        taskDetails.attributeMap['toStateUserId'] = new Array(this.userDetails.username);
        taskDetails.attributeMap['asUserId'] = new Array(this.userDetails.username);
        taskDetails.attributeMap['isAdmin'] = new Array('true');
        taskDetails.attributeMap['raplink'] = new Array('');
        userString = taskData.form.value[key] + ";";
        continue;
      }
      if (key === "1152921504606847182") {
        if (!taskData.form.value[key]) {
          delete taskData.form.value[key];
          continue;
        }
      }

      if (key === "1152921504606847165;Date") {
        if(taskData.form.value[key]!=null)
        {
        // taskData.form.value[key] = this.ngbDateParserFormatter.format(taskData.form.value[key]);
        taskData.form.value[key] = new Date(taskData.form.value[key].year, taskData.form.value[key].month - 1, taskData.form.value[key].day).toUTCString();
      }
      else
      taskData.form.value[key] = "NIL";
    }

      if (key === "sprint") {
        this.taskDetails.listVersionsDropDown.forEach(version => {
          if (version.guid === taskData.form.value[key]) {
            taskDetails.relationMap["RelationTypeSide - uuid=[2305843009213694319] type=[TeamWorkflowTargetedForVersion] side=[SIDE_B]"] = new Array(version);
            delete taskData.form.value[key];
          }
          if( taskData.form.value[key]=="Product_Backlog"){
            taskDetails.attributeMap["Product_Backlog"] = new Array(taskData.form.value[key].toString());
          }
        });
        continue;
      }

      if (key === "1152921504606847200") {
        this.taskDetails.listCompoentsDropDown.forEach(package1 => {
          if (package1.guid === taskData.form.value[key]) {
            taskDetails.relationMap["RelationTypeSide - uuid=[2305843009213694467] type=[ActionableItemWorkFlow] side=[SIDE_A]"] = new Array(package1);
          }
        });

      }

      if(key === "assignselector"){
        this.usersList.forEach(element => {
          if(taskData.form.value[key] === element.name){
            selectedUserId=element.id;
          }
        });
        this.selectedUser=taskData.form.value[key];
      }

      if (taskData.form.value[key]) {
        taskDetails.attributeMap[key] = new Array(taskData.form.value[key].toString());
      }
    }

    // for (let i = 0; i <= this.selectedUsers.length - 1; i++) {
      userString = userString + "<" + selectedUserId + ">"
    // }
    userString = userString + ";;"
    taskDetails.attributeMap["1152921504606847192"] = new Array(userString);
    delete taskDetails.attributeMap['assignselector'];
    const urlData = location.origin;
    taskDetails.attributeMap["URLInfo"]=new Array(urlData);



    this.workitemService.updateTask(taskDetails).subscribe((result: any) => {
      console.log("result", result);
        this.taskDetails.currentState=result.currentState;
    },
      (error: any) => {
        console.error(" Error :: While try to save the task", error);

      });
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
