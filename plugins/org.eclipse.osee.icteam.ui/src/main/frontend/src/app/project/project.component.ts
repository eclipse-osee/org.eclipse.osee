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
import { Component, OnInit } from "@angular/core";
import { NgForm } from "@angular/forms";
import {
  NgbModal,
  ModalDismissReasons,
  NgbActiveModal,
} from "@ng-bootstrap/ng-bootstrap";
import { ProjectModel } from "../model/projectModel";
import { ProjectService } from "../service/project.service";
import { DataserviceService } from "../service/dataservice.service";

@Component({
  selector: "app-project",
  templateUrl: "./project.component.html",
  styleUrls: ["./project.component.scss"],
})
export class ProjectComponent implements OnInit {
  projectData: ProjectModel;
  closeResult: String;
  projectResponse: Boolean;
  userGuid: String;
  userDetails: any;
  message: any;
  isUnique = false;

  constructor(
    private modalService: NgbModal,
    private projectService: ProjectService,
    public activeModal: NgbActiveModal,
    private data: DataserviceService
  ) {}

  ngOnInit() {
    this.userDetails = JSON.parse(sessionStorage.getItem("userDetails"));
    this.userGuid = JSON.parse(sessionStorage.getItem("userDetails")).userGuid;
    this.projectResponse = false;
    this.isUnique = true;
  }

  projectSubmit(projectForm: NgForm) {
    console.log(this.projectData);
    if (projectForm.form.valid) {
      this.projectData = new ProjectModel();
      this.projectData.relationMap = new Map();
      const relationMapRelated = new Array<Map<String, String>>();
      const mapdata = new Map();
      mapdata["guid"] = this.userGuid;
      mapdata["artifactType"] = "User";
      relationMapRelated.push(mapdata);
      this.projectData.relationMap[
        "RelationTypeSide - uuid=[2305843009213694308] type=[Users] side=[SIDE_B]"
      ] = relationMapRelated;
      this.projectData.artifactType = "Agile Project";
      this.projectData.currentUserId = this.userDetails.username;
      this.projectData.name = projectForm.form.value["name"];
      this.projectData.attributeMap = new Map<any, Array<String>>();
      this.projectData.attributeMap["1152921504606847340"] = new Array(
        projectForm.form.value["shortname"]
      );
      this.projectData.attributeMap["ShortName"] = new Array(
        projectForm.form.value["shortname"]
      );
      console.log(this.projectData);
      this.projectService.save(this.projectData).subscribe(
        (result) => {
          let status = result["artifactList"][0].attributeMap["Status"][0];
          if (status == "Success") {
            this.isUnique = true;
            this.data.addProject(result["artifactList"]);
            this.activeModal.close();
          } else {
            this.isUnique = false;
            this.message = result["artifactList"][0].attributeMap["Message"][0];
          }
        },
        (error) => {
          console.error("Error->While saving project to database", error);
          this.projectResponse = true;
          this.activeModal.close();
        }
      );
    } else {
      console.log("Project Submittion");
    }
  }

  open(content) {
    this.modalService.open(content).result.then(
      (result) => {
        this.closeResult = `Closed with: ${result}`;
        console.log(this.closeResult);
      },
      (reason) => {
        this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
      }
    );
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return "by pressing ESC";
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return "by clicking on a backdrop";
    } else {
      return `with: ${reason}`;
    }
  }
}
