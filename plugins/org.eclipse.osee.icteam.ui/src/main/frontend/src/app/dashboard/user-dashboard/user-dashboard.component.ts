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
import { Component, OnInit, Input, ViewChild } from "@angular/core";
import { NgbModal, NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { ProjectModel } from "../../model/projectModel";
import { UserDashboardService } from "../../service/userdashborad.service";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort, MatSortable } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";

@Component({
  selector: "app-user-dashboard",
  templateUrl: "./user-dashboard.component.html",
  styleUrls: ["./user-dashboard.component.scss"],
})
export class UserDashboardComponent implements OnInit {
  showStyle: false;
  selectedIndex: number;
  projectListData = [];
  selected: any;
  itemList = [];
  settings: any;
  selectedProject: any;
  mileStoneList: any = [];
  userGuid: String;
  userDetails: any;
  userName: String;
  isProjListSpinnerActive: Boolean;
  selectedUsers = [];
  userListBeforeFilter: any;

  taskData: any;
  usersForGroupData: any;
  isSprintsSpinnerActive: Boolean;
  releaseShow: any;
  chartHovered: any;
  chartClicked: any;
  newData = 0;
  inProgressData = 0;
  public pieChartLabels: string[] = ["New", "InProgress"];
  public pieChartData: number[];
  public pieChartType: string = "pie";

  filterValue: string;
  noTasksAssigned: boolean;
  taskList: any;
  rowData: any;
  selectedProjectGuid: any;
  sprints: SprintData[] = [];
  pageLength: number;
  dataSource: MatTableDataSource<SprintData>;
  cleardataSource: MatTableDataSource<SprintData>;
  sprintData: any;
  displayedColumns = [
    "taskid",
    "name",
    "type",
    "story",
    "status",
    "sprint",
    "project",
  ];
  currentStatus: any;
  showChart = false;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(private userdashboradservice: UserDashboardService) {}

  ngOnInit() {
    this.userDetails = JSON.parse(sessionStorage.getItem("userDetails"));
    this.userGuid = JSON.parse(sessionStorage.getItem("userDetails")).userGuid;
    this.userName = JSON.parse(sessionStorage.getItem("userDetails")).username;

    const userDashboradArtifact = new ProjectModel();
    userDashboradArtifact.guid = this.userGuid;
    userDashboradArtifact.name = JSON.parse(
      sessionStorage.getItem("userDetails")
    ).username;
    this.userdashboradservice
      .getUserSpecificTasks(userDashboradArtifact)
      .subscribe(
    (result: Response) => {
          console.log("getTasks", result);
          this.taskList = result["list"];
      this.rowData = this.taskList;
          if (this.taskList.length === 0) {
        this.noTasksAssigned = true;
       }
      for (let i = 0; i <= this.taskList.length - 1; i++) {
        this.pageLength = this.taskList.length;
        this.noTasksAssigned = false;
          this.sprints.push(this.createNewTask(this.taskList[i]));
      }
      this.dataSource = new MatTableDataSource(this.sprints);
      console.log(this.dataSource);
      this.dataSource.paginator = this.paginator;
          this.sort.sort(<MatSortable>{ id: "taskid", start: "asc" });
      this.dataSource.sort = this.sort;
      this.pieChartData = [this.newData, this.inProgressData];
      this.showChart = true;
     },
        (error) => {}
      );
  }

  createNewTask(task: any): SprintData {
    let sprint: any;
    let story: any;
    let status: any;
    const userData = [];
    const statusData = [];
    const selectedUsrs = [];
    const id = task.attributeMap['TaskId'][0];
    const name = task.attributeMap['Name'][0];
    const priority = task.attributeMap['ats.Priority'][0];
    const type = task.attributeMap['agile.Change Type'][0];
    status = task.attributeMap[ 'ats.Current State'][0].split(';')[0];
    if ( status === "New" ) {
      this.newData = this.newData + 1;
    } else if (status === "In Progress") {
      this.inProgressData = this.inProgressData + 1;
    }
    const guid = task.guid;
    const createdBy = task.attributeMap["ats.Created By"];
    const createdDate = task.attributeMap["ats.Created Date"];
    const rank = task.attributeMap["agile.Rank"];
    const description = task.attributeMap["ats.Description"];
    const estimatedHours = task.attributeMap["ats.Estimated Hours"];
    const expectedDate = task.attributeMap["ats.Estimated Completion Date"];
    const currentState = task.attributeMap["ats.Current State"];
    // const selusersIdList = task.attributeMap['ats.Current State'][0].split(';')[1].split('><');

    if ( task.attributeMap['ats.Points Attribute Type'] === undefined) {
        story = '';
    } else {
        story = task.attributeMap['ats.Points Attribute Type'][0];
    }

    if (task.attributeMap['Backlog'][0] === 'false' ) {
     sprint =  task.attributeMap['SprintName'];
    } else {
      sprint = "Backlog";
    }
    const project = task.attributeMap["ProjectName"];
    // Returns a new task of a sprint
    return {
           taskid: id,
           name: name,
           priority: priority,
           type: type,
           status: status,
           users: userData,
           guid: guid,
           createdBy: createdBy,
           createdDate: createdDate,
           rank: rank,
           story: story,
           description: description,
           estimatedHours: estimatedHours,
           expectedDate: expectedDate,
           selectedUsers: selectedUsrs,
           listStatus: statusData,
           currentState: currentState,
           sprint: sprint,
           project: project,
         };
  }

  applyFilter(filterValue: string) {
    this.filterValue = filterValue.trim(); // Remove whitespace
    this.filterValue = this.filterValue.toLowerCase(); // Datasource defaults to lowercase matches
    this.dataSource.filter = filterValue;
  }
}

@Component({
  selector: "ngbd-modal-content",
  template: `
    <div class="modal-header">
      <h4 class="modal-title">Cancel</h4>
      <button
        type="button"
        class="close"
        aria-label="Close"
        (click)="activeModal.dismiss('Cross click')"
      >
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="modal-body">
      <div class="form-group">
        <label for="">Enter cancellation reson*</label>
        <textarea
          class="form-control"
          name=""
          id=""
          rows="3"
          [(ngModel)]="resonvalue"
        ></textarea>
      </div>
    </div>
    <div class="modal-footer">
      <button
        type="button"
        class="btn btn-outline-info"
        (click)="activeModal.close(resonvalue)"
      >
        Ok
      </button>
      <button
        type="button"
        class="btn btn-outline-dark"
        (click)="activeModal.dismiss('close')"
      >
        Cancel
      </button>
    </div>
  `,
})
export class NgbdModalContent {
  // @Input() name;
  resonvalue: any;
  constructor(public activeModal: NgbActiveModal) {}
}

export interface SprintData {
  taskid: any;
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
  sprint: any;
  project: any;
}
