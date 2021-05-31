/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
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
import { Color } from "ng2-charts";
import { ProjectService } from "./../../service/project.service";
import { ChartsService } from "./../../service/charts.service";
import { ProjectModel } from "./../../model/projectModel";
import { ChartsComponent } from "./../charts.component";
import { ActivatedRoute, Params } from "@angular/router";

@Component({
  selector: "app-pointschart",
  templateUrl: "./pointschart.component.html",
  styleUrls: ["./pointschart.component.css"],
})
export class PointschartComponent implements OnInit {
  releaseList = [];
  dateData = [];
  selectedValue: string;
  lineChartData = [];
  lineChartLabels = [];
  remainingStoryPoints = [];
  practicalStoryPoints = [];
  noOfDays;
  totalStoryPoints;
  selectedProjectGuid: any;

  lineChartOptions = {
    responsive: true,
    scales: {
      xAxes: [
        {
          scaleLabel: {
            display: true,
            labelString: "Dates",
            fontStyle: "Bold",
            fontSize: 16,
            fontFamily: "Calibri",
          },
        },
      ],
      yAxes: [
        {
          scaleLabel: {
            display: true,
            labelString: "Story Points",
            fontStyle: "Bold",
            fontSize: 16,
            fontFamily: "Calibri",
          },
        },
      ],
    },
  };

  public chartType = "line";
  public lineChartLegend = true;
  lineChartColors: Color[] = [
    {
      borderColor: "black",
    },
  ];

  constructor(
    private projectService: ProjectService,
    private chartsService: ChartsService,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((projectId: Params) => {
      this.selectedProjectGuid = projectId.projectId;
    });
    this.getProjectsList();
  }

  getProjectsList() {
    this.projectService
      .getOpenReleaseForProject(this.selectedProjectGuid)
      .subscribe(
        (res: Response) => {
          this.releaseList = res["list"];
        },
        (error) => {
          console.error(error);
        }
      );
  }

  getBurndownData() {
    const sprintData = new ProjectModel();
    sprintData.guid = this.selectedValue["guid"];
    sprintData.attributeMap = new Map();
    this.chartsService.getStoryPointsBurnDownData(sprintData).subscribe(
      (res: Response) => {
        console.log("Dates " + res["datesSet"]);
        this.dateData = res["datesSet"];
        this.remainingStoryPoints = res["remainingStoryPoints"];
        this.totalStoryPoints = res["totalStoryPoints"];
        this.noOfDays = res["noOfDays"];
        this.practicalStoryPoints = res["practicalStoryPoints"];
      },
      (error) => {
        console.error(error);
      }
    );
  }

  generateBurndownChart() {
    let chartsComp: ChartsComponent = new ChartsComponent(null, null, null);
    this.lineChartData = [
      {
        data: this.remainingStoryPoints,
        label: "Remaining Story Points",
        lineTension: 0.2,
      },
      {
        data: chartsComp.constructIdealLineData(
          this.noOfDays,
          this.totalStoryPoints
        ),
        label: "Ideal Completion Story Points",
      },
      {
        data: this.practicalStoryPoints,
        label: "Practical Story Points",
        lineTension: 0.2,
      },
    ];

    this.lineChartLabels = this.dateData;
  }
}
