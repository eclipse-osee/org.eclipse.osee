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
import { Component, OnInit, ViewChild, OnDestroy } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NgForm } from "@angular/forms";
import { FileUploader, FileUploaderOptions } from "ng2-file-upload";
import { CookieService } from "ngx-cookie-service";
import { DragulaService } from "ng2-dragula";
import {
  NgbModal,
  ModalDismissReasons,
  NgbActiveModal,
} from "@ng-bootstrap/ng-bootstrap";
import { WorkitemService } from "../../../service/workitem.service";
import { ProjectModel } from "../../../model/projectModel";
@Component({
  selector: "app-link-task",
  templateUrl: "./link-task.component.html",
  styleUrls: ["./link-task.component.scss"],
})
export class LinkTaskComponent implements OnInit {
  tasks = [];
  selectedRow: any;
  guid: any;
  newVal: string;
  selectedType = "Epic";
  SearchTask: any;
  taskId: string;
  taskName: string;
  taskGuid: any;
  DropdownList = [];
  typeList = [];
  projectList = [];
  rowEnabled = true;
  selectedProject: string;

  constructor(
    public activeModal: NgbActiveModal,
    private workitemService: WorkitemService
  ) {}
  ngOnInit() {
    let taskId = new ProjectModel();
    console.log("inside-link task");
    taskId.attributeMap = new Map<any, any>();
    taskId.guid = this.guid;
    this.taskGuid = this.guid;
    taskId.attributeMap["filter"] = new Array(this.selectedType);

    this.workitemService.getselectedProject(taskId).subscribe(
      (result: Response) => {
        this.selectedProject = result["list"][0].attributeMap["Project"][0];
        taskId.attributeMap["project"] = new Array(this.selectedProject);
        this.workitemService.getTaskInfoForLinking(taskId).subscribe(
          (result: Response) => {
            this.DropdownList = result["list"];
            let i = 1;
            this.tasks = [];
            this.DropdownList.forEach((element) => {
              let idList = element.attributeMap["TaskId"];
              let nameList = element.attributeMap["TaskName"];

              let j = 0;
              idList.forEach((element) => {
                let taskIdssList = {};
                taskIdssList["Id"] = element;
                taskIdssList["Title"] = nameList[j];
                this.tasks.push(taskIdssList);
                j++;
              });
              i++;
            });

            // this.activeModal.close("TaskLinked");
          },
          (error) => {
            console.error(error);
          }
        );
      },
      (error) => {
        console.error(error);
      }
    );

    this.workitemService
      .getTaskTypeAndPriorities("1152921504606851584")
      .subscribe(
        (result: Response) => {
          let typeListTemp = [];
          typeListTemp = result["artifactList"];
          typeListTemp.forEach((element) => {
            this.typeList.push(element.name);
          });
        },
        (error) => {
          console.error(error);
        }
      );

    this.workitemService.getAllProjectsForTaskLinking(taskId).subscribe(
      (result: Response) => {
        let projectListTemp = [];
        projectListTemp = result["list"];
        projectListTemp.forEach((element) => {
          this.projectList.push(element.name);
        });
      },
      (error) => {
        console.error(error);
      }
    );
  }

  selection(event: any, t: any, index: any) {
    // this.rowEnabled = false;
    // event.data.rowEnabled=false;
    this.selectedRow = index;
    let taskId = new ProjectModel();
    taskId.attributeMap = new Map<any, any>();
    taskId.guid = this.guid;
    this.taskGuid = this.guid;
    taskId.attributeMap["TaskId"] = new Array(t.Id);
    taskId.attributeMap["TaskName"] = new Array(t.Title);
    this.workitemService.linkTasks(taskId).subscribe(
      (result: Response) => {
        result["list"];
      },
      (error) => {
        console.error(error);
      }
    );
    this.tasks.splice(index, 1);
  }

  getSelectedOption(event: any) {
    let taskId = new ProjectModel();
    taskId.attributeMap = new Map<any, any>();
    taskId.guid = this.guid;
    this.taskGuid = this.guid;
    taskId.attributeMap["filter"] = new Array(this.selectedType);
    taskId.attributeMap["project"] = new Array(this.selectedProject);

    this.workitemService.getTaskInfoForLinking(taskId).subscribe(
      (result: Response) => {
        this.DropdownList = result["list"];
        let i = 1;
        this.tasks = [];
        this.DropdownList.forEach((element) => {
          let idList = element.attributeMap["TaskId"];
          let nameList = element.attributeMap["TaskName"];

          let j = 0;

          idList.forEach((element) => {
            let taskIdssList = {};
            taskIdssList["Id"] = element;
            console.log("inside success", element);
            taskIdssList["Title"] = nameList[j];
            this.tasks.push(taskIdssList);
            j++;
          });
          i++;
        });
      },
      (error) => {
        console.error(error);
      }
    );
  }
}
