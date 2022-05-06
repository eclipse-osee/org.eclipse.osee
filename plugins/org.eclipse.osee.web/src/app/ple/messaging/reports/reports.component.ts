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
import { MatSelectChange } from '@angular/material/select';
import { ActivatedRoute } from '@angular/router';
import { iif, of } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { ConnectionService } from '../connection-view/services/connection.service';
import { RouteStateService } from '../connection-view/services/route-state-service.service';
import { ReportsService } from '../shared/services/ui/reports.service';
import { connection, transportType } from '../shared/types/connection';
import { MimReport } from '../shared/types/Reports';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.sass']
})
export class ReportsComponent implements OnInit {

  constructor(private route: ActivatedRoute, private routerState: RouteStateService, private reportsService: ReportsService, private connectionService: ConnectionService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.routerState.branchId = params.get('branchId') || '';
      this.routerState.branchType = params.get('branchType') || '';
    })
  }

  selectedReport: MimReport|undefined = undefined;
  
  branchId = this.reportsService.branchId;
  branchType = this.reportsService.branchType;
  reports = this.reportsService.getReports();
  diffReportRoute = this.reportsService.diffReportRoute;

  reportSelectionText = this.reports.pipe(
    switchMap(reports => iif(() => reports.length > 0, of("Select a Report"), of("No reports available")))
  )
  
  connections = this.branchId.pipe(
    tap(_=>{this.reportsService.Connection={id: '-1', name: '', transportType: transportType.Ethernet}}),
    filter(v => v !== ''),
    switchMap(branchId => this.connectionService.getConnections(branchId))
  )
    
  connectionSelectionText = this.connections.pipe(
    switchMap(connections => iif(() => connections.length > 0, of("Select a Connection"), of("No connections available")))
  )

  selectReport(event: MatSelectChange) {
    this.selectedReport = event.value;
  }

  getSelectedReport() {
    this.reportsService.downloadReport(this.selectedReport).subscribe();
  }

  selectConnection(event: MatSelectChange) {
    this.selectedConnection = event.value;
  }

  selectFile(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const file: File = target.files[0];
      this.reportsService.RequestBodyFile = file;
    }
  }

  get requestBody() {
    return this.reportsService.requestBody.getValue();
  }

  set requestBody(requestBody: string) {
    this.reportsService.RequestBody = requestBody;
  }

  get selectedConnection() {
    return this.reportsService.connection.getValue();
  }

  set selectedConnection(connection: connection) {
    this.reportsService.Connection = connection;
  }

}
