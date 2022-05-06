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
import { ActivatedRoute } from '@angular/router';
import { RouteStateService } from '../connection-view/services/route-state-service.service';
import { ReportsService } from '../shared/services/ui/reports.service';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.sass']
})
export class ReportsComponent implements OnInit {

  constructor(private route: ActivatedRoute, private routerState: RouteStateService, private reportsService: ReportsService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.routerState.branchId = params.get('branchId') || '';
      this.routerState.branchType = params.get('branchType') || '';
    })
  }

  branch = this.reportsService.branchId;
  branchType = this.reportsService.branchType;
  diffReportRoute = this.reportsService.diffReportRoute;

}
