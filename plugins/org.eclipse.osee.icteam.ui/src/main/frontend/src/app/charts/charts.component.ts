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
import { ProjectService } from "./../service/project.service";
import { Color } from "ng2-charts";
import { ChartsService } from "./../service/charts.service";
import { ProjectModel } from "./../model/projectModel";
import { ActivatedRoute, Params } from "@angular/router";

@Component({
  selector: "app-charts",
  templateUrl: "./charts.component.html",
  styleUrls: ["./charts.component.css"],
})
export class ChartsComponent implements OnInit {
  releaseList = [];
  datesData = [];
  totaltime;
  noOfDays;
  remainingTimeData = [];
  selectedValue: string;
  lineChartData = [];
  lineChartLabels = [];
  selectedProjectGuid: any;
  dropDownNav: any;
  userDetails: any;

  public lineChartOptions = {
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
            labelString: "Effort (in hours)",
            fontStyle: "Bold",
            fontSize: 16,
            fontFamily: "Calibri",
          },
        },
      ],
    },
  };

  lineChartPlugins = [];
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

  generateBurndownChart() {
    let idealData = this.constructIdealLineData(this.noOfDays, this.totaltime);

    this.lineChartData = [
      {
        data: this.remainingTimeData,
        label: "Actual Effort",
        lineTension: 0.2,
      },
      {
        data: idealData,
        label: "Ideal",
      },
    ];

    this.lineChartLabels = this.datesData;
  }

  getBurndownData() {
    const sprintData = new ProjectModel();
    sprintData.guid = this.selectedValue["guid"];
    sprintData.attributeMap = new Map();
    this.chartsService.getBurnDownData(sprintData).subscribe(
      (res: Response) => {
        console.log("Dates " + res["datesSet"]);
        console.log("Remaining Time " + res["remainingTimeSet"]);
        console.log("Total Time " + res["totalTime"]);
        console.log("No of Days  " + res["noOfDays"]);
        this.datesData = res["datesSet"];
        this.remainingTimeData = res["remainingTimeSet"];
        this.totaltime = res["totalTime"];
        this.noOfDays = res["noOfDays"];
      },
      (error) => {
        console.error(error);
      }
    );
  }

  public constructIdealLineData(noOfDays, totaltime) {
    let idealData = [];
    idealData.push(totaltime);
    let interpolationPoint = totaltime / noOfDays;
    for (let index = 1; index < noOfDays; index++) {
      totaltime = totaltime - interpolationPoint;
      idealData.push(totaltime);
    }
    idealData.push(0);
    return idealData;
  }
}
