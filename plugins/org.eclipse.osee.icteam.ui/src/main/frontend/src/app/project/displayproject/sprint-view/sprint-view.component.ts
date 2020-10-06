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
import { Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import { ProjectModel } from '../../../model/projectModel';
import { NgbdModalContent } from '../../../dashboard/user-dashboard/user-dashboard.component';
import { Router, ActivatedRoute, Params, NavigationEnd } from '@angular/router';
import { WorkitemService } from '../../../service/workitem.service';
import { NgbModal, NgbActiveModal, NgbModalOptions, NgbPopoverConfig  } from '@ng-bootstrap/ng-bootstrap';
import { ProjectService } from '../../../service/project.service';
import { DataserviceService } from '../../../service/dataservice.service';
import { MatPaginator, MatSort, MatTableDataSource, MatSortable} from '@angular/material';
import { FormControl} from '@angular/forms';
import { analyzeAndValidateNgModules } from '@angular/compiler';
import { ReplaySubject } from 'rxjs';

@Component({
  selector: 'app-sprint-view',
  templateUrl: './sprint-view.component.html',
  styleUrls: ['./sprint-view.component.css'],
  providers: [NgbPopoverConfig],
})
export class SprintViewComponent implements OnInit, OnDestroy {

  filterData: any;
  filterType: any;
  selectedFilterType: any;
  selectedProject: any;
  selectedSprint: any;
  userDetails: any;
  taskList: any;
  selectedProjectGuid: any;
  selectedUsers = [];
  taskID: any;
  userListBeforeFilter: any;
  usersForGroupData: any;
  filter: TasksFilter;
  usersList: any;
  taskId: any;
  statusList: Array<String> = new Array();
  test: any;
  subscription: any;

  noTasksAssigned: boolean;
  noBacklogTasksAssigned: boolean;
  columnDefs: any;
  rowData: any;
  modalOption: NgbModalOptions = {};
  commentText: string;
  sprintforbacklog = false;
  sprints: SprintData[] = [];
  filterValue: string;
  users = new FormControl();
  pageLength: number;
  dataSource: MatTableDataSource<SprintData>;
  cleardataSource: MatTableDataSource<SprintData>;
  sprintData: any;
  displayedColumns = ['taskId', 'name', 'story', 'status', 'type', 'assignee'];
  currentStatus: any;
  selectedsprintName: any;


  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;


  constructor(config: NgbPopoverConfig, private route: ActivatedRoute, private router: Router,
    private modalService: NgbModal, private projectService: ProjectService,
    private workitemService: WorkitemService, private data: DataserviceService) {
      config.placement = 'right';
      config.triggers = 'hover';
  }

  public ngOnInit() {
    this.filter = new TasksFilter();
    this.filter['isEnable'] = true;
    this.modalOption.backdrop = 'static';
    this.modalOption.keyboard = false;
    this.statusList = [];
    this.statusList.push('Working');
    this.filter = new TasksFilter();
    this.filter.isEnable = true;
    this.filter.working = true;
   this.userDetails = JSON.parse(sessionStorage.getItem('userDetails'));
   const rege = /%2(52)*B/g;
   console.log(this.route.parent.snapshot.paramMap);
   const urlData = this.router.url.split('/');

   if (urlData[urlData.length - 1] === 'Backlog') {
    const projectGuid: String = urlData[urlData.length - 3];
    this.selectedProjectGuid = projectGuid.replace(rege, '+');
    this.selectedsprintName = 'Backlog';
    this.renderTaskForBackLog(this.selectedProjectGuid);
   }
   if (urlData[urlData.length - 2] === 'Sprint') {
    const projectGuid: String = urlData[urlData.length - 4];
    this.selectedProjectGuid = projectGuid.replace(rege, '+');
    const sprint = new ProjectModel();
    const sprintGuid: String = urlData[urlData.length - 1];
    sprint.guid = sprintGuid.replace(rege, '+');
    this.taskId = sprint.guid;
    if (sprint.guid) {
      this.selectedSprint = sprint;
      const sprintData = new ProjectModel();
      sprintData.guid = this.taskId;
      sprintData.attributeMap = new Map();
      this.projectService.getSprintByUuid(sprintData).subscribe(
        (result: Response) => {
          const sprnt = result['list'][0];
          this.selectedsprintName = sprnt.name;

          if (sprnt.attributeMap['ats.Released'][0] === 'true') {
            this.renderTasks(sprint, ['Completed']);
          this.statusList.push('Completed');
          this.filter.Completed = true;
          this.filter.working = false;
          } else {
            this.renderTasks(sprint, ['Working']);
          }
        },
        (error) => {
            console.log('Error while feching sprint data');
        });
        
    }
   }
  
   this.subscription = this.router.events.subscribe((event) => {
    if (event instanceof NavigationEnd) {
        this.router.navigated = false;
    }
});
  }

  applyFilter(filterValue: string) {
    this.filterValue = filterValue.trim(); 
    this.filterValue = this.filterValue.toLowerCase(); 
    this.dataSource.filter = filterValue;
  }

  public ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  renderTasks(selectedSprint: any, statusList: Array<String>) {
    this.selectedSprint = selectedSprint;
    console.log('selectedSprint', selectedSprint);
    const artifact = new ProjectModel();
    artifact.parentGuid = selectedSprint['guid'];
    artifact.attributeMap = new Map();
    artifact.attributeMap['filter'] = statusList;
    console.log('Selected Project guid--->', this.selectedProjectGuid);
    this.workitemService.getAllTaskOFUserPerSprint(artifact).subscribe(
        (result: Response) => {
            console.log('getTasks', result);

            this.taskList = result['list'];
            this.rowData = this.taskList;
            if ( this.taskList.length === 0) {
              this.noTasksAssigned = true;
             }

            for (let i = 0; i <= this.taskList.length - 1; i++) {
              this.pageLength = this.taskList.length;
              this.noTasksAssigned = false;
              this.sprints.push(this.createNewSprint(this.taskList[i]));
            }
            this.dataSource = new MatTableDataSource(this.sprints);
            console.log(this.dataSource);
            this.dataSource.paginator = this.paginator;
            this.sort.sort(<MatSortable>({id: 'taskId', start: 'asc'}));
            this.dataSource.sort = this.sort;
        },
        (error) => {
            console.log('Error while feching sprint data');
        }
    );
}

  renderTaskForBackLog(selectedProjectGuid: any): void {
    this.sprintforbacklog = true;
    const project = new ProjectModel();
    project.guid = selectedProjectGuid;
    this.workitemService.getAllTaskForBackLog(project).subscribe(
      (result:  Response) => {
        console.log('getTasks', result);
        this.taskList = result['artifactList'];
        // mbh9kor empty assignee combo issue fixed
        this.rowData = this.taskList;
        if ( this.taskList.length === 0) {
          this.noBacklogTasksAssigned = true;
         }
        for (let i = 0; i <= this.taskList.length - 1; i++) {
          this.pageLength = this.taskList.length;
          this.noBacklogTasksAssigned = false;
          this.sprints.push(this.createNewSprint(this.taskList[i]));
        }
        this.dataSource = new MatTableDataSource(this.sprints);
        console.log(this.dataSource);
        this.dataSource.paginator = this.paginator;
        this.sort.sort(<MatSortable>({id: 'taskId', start: 'asc'}));
        this.dataSource.sort = this.sort;
      },
      (error) => {
        console.log('Error while feching sprint data');
      }
    );
  }

  createNewSprint(task: any): SprintData {
    const userData = [];
    const statusData = [];
    let nextstatusData = [];
    let selectedUsers: any;
    let story;
    const id = task.attributeMap['TaskId'][0];
    const name = task.attributeMap['Name'][0];
    const priority = task.attributeMap['ats.Priority'][0];
    if ( task.attributeMap['ats.Points Attribute Type'] === undefined) {
        story = '';
    } else {
        story = task.attributeMap['ats.Points Attribute Type'][0];
    }

    const type = task.attributeMap['agile.Change Type'][0];
    const status = task.attributeMap[ 'ats.Current State'][0].split(';')[0];
    const guid = task.guid;
    const createdBy = task.attributeMap['ats.Created By'];
    const createdDate = task.attributeMap['ats.Created Date'];
    const rank = task.attributeMap['agile.Rank'];
    const description = task.attributeMap['ats.Description'];
    const estimatedHours = task.attributeMap['ats.Estimated Hours'];
    const expectedDate = task.attributeMap['ats.Estimated Completion Date'];
    const currentState = task.attributeMap['ats.Current State'];
    const selusersIdList = task.attributeMap['ats.Current State'][0].split(';')[1].split('><');
    statusData.push(status);
    nextstatusData = task.attributeMap.states;
    for (let i = 0; i <= nextstatusData.length - 1 ; i++) {
      statusData.push(nextstatusData[i]);
    }
    selusersIdList[0] = selusersIdList[0].replace('<', '').replace('>', '');
    selusersIdList[selusersIdList.length - 1] = selusersIdList[selusersIdList.length - 1].replace('<', '').replace('>', '');
    console.log(selusersIdList);
   
    let selectedUser: any;
            this.userListBeforeFilter = task.relationMap['AssigneeForCombo'];
            this.userListBeforeFilter.forEach(element => {
                if (element != null) {
                  for ( let i = 0; i <= selusersIdList.length - 1 ; i++) {
                    if ( selusersIdList[i] === element.attributeMap['User Id'][0]) {
                      selectedUsers = element.name;
                        selectedUser = element.name;

                    }
                  }
                  console.log(selectedUsers);
                  const nameList = {};
                  nameList['id'] = element.attributeMap['User Id'][0];
                  nameList['name'] = element.name;
                  userData.push(nameList);
                }
            });
          // }
         return{
           taskId: id,
           name: name,
           priority: priority,
           type: type,
           status: status,
           users: userData,
           guid: guid,
           createdBy: createdBy,
           createdDate: createdDate,
           story: story,
           rank: rank,
           description: description,
           estimatedHours: estimatedHours,
           expectedDate: expectedDate,
           selectedUsers: selectedUsers,
           listStatus: statusData,
           currentState: currentState,
           selectedUser : selectedUser,
         };
  }

  statusUpdate(updatedValue: any, taskDetails: any) {
    if ('Cancelled' === updatedValue) {
      this.cancelOption(updatedValue, taskDetails);
      return;
    }

    this.updateStatus(updatedValue, taskDetails, '');
  }


  updateStatus(updatedValue: any, taskDetails: any, resone: any) {
    const updatedStatusList = [];
    const taskGuid = taskDetails.guid;
    const artifact = new ProjectModel();
    artifact.guid = taskDetails.guid;
    artifact.currentLoggedInUser = this.userDetails.userGuid;
    artifact.parentGuid = this.selectedProjectGuid;
    artifact.attributeMap = new Map<any, Array<String>>();
    artifact.attributeMap['reason'] = new Array<String>(resone);
    artifact.attributeMap['toState'] = new Array<String>(updatedValue.value);
    taskDetails.users.forEach(element => {
      if(taskDetails.selectedUser === element.name){
        artifact.attributeMap['toStateUserId'] = new Array(element.id);
      }
    });

    artifact.attributeMap['asUserId'] = new Array<String>(this.userDetails.username);
    artifact.attributeMap['isAdmin'] = new Array<String>('true');
    artifact.attributeMap['raplink'] = new Array<String>('');
    const urlData = location.origin;
    artifact.attributeMap['URLInfo'] = new Array(urlData);
    const utill = require('util');
    const artJson = utill.inspect(artifact);
    const ArtifactJson = artJson.split('\n').join('');
    // this.statusResponse=false;
    this.workitemService.updateTaskStatus(ArtifactJson).subscribe(
      (response: any) => {
        console.log(response);
        if ( response.list[0].attributeMap['status'][0] === 'failed') {
          for ( let i = 0 ; i <= this.dataSource.data.length - 1 ; i++ ) {
            const data = this.dataSource.data[i];
            let dataTaskGuid: any;
            dataTaskGuid = data.guid;
            if ( dataTaskGuid === taskGuid ) {
              this.dataSource.data[i].status = response.list[0].attributeMap['CurrentState'][0];
            break;
             }
            }

        } else {
        let userList: any;
        userList = response['list'];
        const userData = [];
        let dataTaskGuid: any;
        userList.forEach(element => {
          console.log('Inside for loop===', element);
          if (element.artifactType !== 'User') {
            updatedStatusList.push(updatedValue.value);
            let nextStateList = [];
            nextStateList = element.attributeMap.states;
            for (let i = 0; i <= nextStateList.length - 1 ; i++) {
              updatedStatusList.push(nextStateList[i]);
            }
            for (let i = 0 ; i <= this.dataSource.data.length - 1 ; i++ ) {
              const data = this.dataSource.data[i];
              dataTaskGuid = data.guid;
               if ( dataTaskGuid === taskGuid ) {
               this.dataSource.data[i].listStatus = updatedStatusList;
               break;
               }
             }
          } else {
            if (element != null) {
              const nameList = {};
              nameList['id'] = element.attributeMap['User Id'][0];
              nameList['name'] = element.name;
              userData.push(nameList);
            }
          }
        });
        for (let i = 0 ; i <= this.dataSource.data.length - 1 ; i++ ) {
          const data = this.dataSource.data[i];
          dataTaskGuid = data.guid;
           if ( dataTaskGuid === taskGuid ) {
           this.dataSource.data[i].users = userData;
           break;
           }
         }
        }
      },
      (error: any) => {
        console.error(error);
      }

    );
  }

  cancelOption(updatedValue: any, taskDetails: any) {
    const modalRef = this.modalService.open(NgbdModalContent);
    console.log(modalRef);

    modalRef.result.then((userResponse) => {
      if (userResponse === undefined || userResponse.length === 0) {
        userResponse = '';
      }
      this.updateStatus(updatedValue, taskDetails, userResponse);
    });
  }


  assignUser(item: any, task: any) {

    const taskData = new ProjectModel();
    taskData.currentLoggedInUser = this.userDetails.username;
    taskData.guid = task.guid;
    taskData.attributeMap = new Map<any, Array<String>>();
    let userString = task.status + ';';
      for (let j = 0; j <= task.users.length - 1; j++) {
        if ( task.users[j].name === item.value ) {
          userString = userString + '<' + task.users[j].id + '>';
        }
      }
    userString = userString + ';;';
    taskData.attributeMap['1152921504606847192'] = new Array(userString);
    const urlData = location.origin;
    taskData.attributeMap['URLInfo'] = new Array(urlData);
    this.workitemService.updateUser(taskData).subscribe(
      result => {
        console.log('User updated successfully');
      },
      error => {
        console.error('Error->While updating user', error);
      });
  }

  onUserSelect(item: any) {
    console.log(item);
    console.log(item.attributeMap['ats.Actionable Item']);
    console.log(this.selectedUsers);
    console.log(this.selectedUsers.map(option => this.usersList.find(o => o.id === option.id)));
  }



  saveComments(task: any, commentData: any, p) {
    console.log('taksDetails', task);
    console.log('commentdata', this.commentText);
    const commmntArtifact: ProjectModel = new ProjectModel();
    commmntArtifact.guid = task.guid;
    commmntArtifact.attributeMap = new Map<any, any>();
    commmntArtifact.attributeMap['userID'] = new Array(this.userDetails.username);
    commmntArtifact.attributeMap['comment'] = new Array<String>(commentData);
    commmntArtifact.attributeMap['currentState'] = task.currentState;
    this.workitemService.saveComment(commmntArtifact).subscribe(
      (response: any) => {
        console.log('Response', response);

      },
      (error: any) => {
        console.error('Error while saving comment', error);
      }
    );
    p.close();
  }
  filterActions() {
    this.filter['isEnable'] = !this.filter['isEnable'];
  }

  onClicked(event: Event) {

    this.statusList = [];
    if (this.filter.working) {
      this.statusList.push('Working');
    }
    if (this.filter.Completed) {
      this.statusList.push('Completed');
    }
    if (this.filter.Cancelled) {
      this.statusList.push('Cancelled');
    }
    for (let i = 0; i <= this.taskList.length; i++ ) {
     this.dataSource.data.splice(i);
    }
    if ( this.sprintforbacklog ) {
    } else {
      this.renderTasks(this.selectedSprint, this.statusList);
    }
  }

  outSideClick(type: Boolean) {
    console.log('type', type);
    type = false;

  }

}


export class TasksFilter {
  working: Boolean = false;
  Completed: Boolean = false;
  Cancelled: Boolean = false;
  isEnable: Boolean = false;
}

export interface SprintData {
  taskId: any;
  name: any;
  priority: any;
  type: any;
  status: any;
  users: any;
  guid: any;
  createdBy: any;
  createdDate: any;
  story: any;
  rank: any;
  description: any;
  estimatedHours: any;
  expectedDate: any;
  selectedUsers: any;
  listStatus: any;
  currentState: any;
  selectedUser: any;
}


