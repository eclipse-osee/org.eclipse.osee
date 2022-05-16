/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { Component, OnInit } from '@angular/core';
import { DiffReportService } from '../../shared/services/ui/diff-report.service';
import { branchSummary, diffReportSummaryItem} from '../../shared/types/DifferenceReport';
import { HeaderService } from '../../shared/services/ui/header.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-diff-report',
  templateUrl: './diff-report.component.html',
  styleUrls: ['./diff-report.component.sass']
})
export class DiffReportComponent implements OnInit {


  constructor(private diffReportService: DiffReportService, private headerService: HeaderService) {}

  ngOnInit(): void {}

  date = new Date();
  
  branchSummaryKey = 'branchSummary';
  branchSummaryHeaders:(keyof branchSummary)[] = [
    'pcrNo',
    'description',
    'compareBranch',
    'reportDate'
  ]
  
  reportSummaryKey = 'diffReportSummary'
  reportSummaryHeaders:(keyof diffReportSummaryItem)[] = [
    'changeType',
    'action',
    'name',
    'details'
  ]

  branchInfo = this.diffReportService.branchInfo;
  parentBranchInfo = this.diffReportService.parentBranchInfo;
  branchSummary = this.diffReportService.branchSummary;
  diffReportSummary = this.diffReportService.diffReportSummary;
  differenceReport = this.diffReportService.diffReport;
  nodes = this.diffReportService.nodes;

  isDifference = this.differenceReport.pipe(
    map(report => {
      return Object.keys(report.changeItems).length !== 0;
    })
  )

  getHeaderByName(value: keyof branchSummary|keyof diffReportSummaryItem, key: string) {
    return this.headerService.getHeaderByName(value, key);
  }

  scrollTo(id: string) {
    document.getElementById(id)?.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    });
  }

}
