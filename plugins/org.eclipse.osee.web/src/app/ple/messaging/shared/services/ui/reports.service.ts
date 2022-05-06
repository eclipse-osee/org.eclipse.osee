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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, iif, of } from 'rxjs';
import { map, shareReplay, switchMap, take } from 'rxjs/operators';
import { FilesService } from 'src/app/ple-services/http/files.service';
import { BranchUIService } from 'src/app/ple-services/ui/branch/branch-ui.service';
import { apiURL } from 'src/environments/environment';
import { connection, transportType } from '../../types/connection';
import { MimReport } from '../../types/Reports';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  constructor(private uiService: BranchUIService, private http: HttpClient, private fileService: FilesService) { }

  private _connection: BehaviorSubject<connection> = new BehaviorSubject<connection>({name: '', transportType: transportType.Ethernet});
  private _requestBody: BehaviorSubject<string> = new BehaviorSubject('');
  private _requestBodyFile: BehaviorSubject<File|undefined> = new BehaviorSubject<File|undefined>(undefined);

  getReports() {
    return this.http.get<MimReport[]>(apiURL+'/mim/reports').pipe(
      shareReplay(1)
    );
  }

  downloadReport(report: MimReport|undefined) {
      return combineLatest([this.branchId, this._connection]).pipe(
        take(1),
        switchMap(([branchId, connection]) => iif(()=>report !== undefined, this.getReport(report as MimReport, branchId, connection).pipe(
          map(res => {
            if (res.size !== 0) {
              const blob = new Blob([res], {type: report?.producesMediaType})
              const url = URL.createObjectURL(blob);
              const link = document.createElement('a');
              link.href = url;
              link.setAttribute('download', report?.fileNamePrefix+'_'+connection?.name+'.'+report?.fileExtension);
              document.body.appendChild(link);
              link.click();
              link.remove();
            }
          })
        ), of())),
      )
  }

  private getReport(report: MimReport, branchId: string, connection?: connection) {
    return combineLatest(([this.requestBody, this.requestBodyFile])).pipe(
      take(1),
      switchMap(([input, file]) => 
        iif(() => report !== undefined && report.url !== '' && branchId !== '' && connection !== undefined && connection.id !== '-1', 
          this.fileService.getFileAsBlob(report.httpMethod, report.url.replace('<branchId>', branchId).replace('<connectionId>', connection?.id!), file === undefined ? input : file),
          of(new Blob()))
    ))
  }

  private _diffReportRoute = combineLatest([this.uiService.id, this.uiService.type]).pipe(
    switchMap(([branchId, branchType]) => of("/ple/messaging/reports/"+branchType+"/"+branchId+"/differences"))
  )

  get diffReportRoute() {
    return this._diffReportRoute;
  }

  get branchId() {
    return this.uiService.id;
  }

  set BranchId(branchId: string) {
    this.uiService.idValue = branchId;
  }

  get branchType() {
    return this.uiService.type;
  }

  set BranchType(branchType: string) {
    this.uiService.typeValue = branchType;
  }

  get connection() {
    return this._connection;
  }

  set Connection(connection: connection) {
    this._connection.next(connection);
  }

  get requestBody() {
    return this._requestBody;
  }

  set RequestBody(requestBody: string) {
    this.requestBody.next(requestBody);
  }

  get requestBodyFile() {
    return this._requestBodyFile;
  }

  set RequestBodyFile(requestBodyFile: File) {
    this.requestBodyFile.next(requestBodyFile)
  }

}
