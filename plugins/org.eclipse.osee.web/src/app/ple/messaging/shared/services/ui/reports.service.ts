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
import { Injectable } from '@angular/core';
import { combineLatest, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BranchUIService } from 'src/app/ple-services/ui/branch/branch-ui.service';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  constructor(private uiService: BranchUIService) { }

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
}
