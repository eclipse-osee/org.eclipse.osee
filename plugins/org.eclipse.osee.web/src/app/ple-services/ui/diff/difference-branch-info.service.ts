/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { BranchInfoService } from '../../http/branch-info.service';
import { DifferenceReportService } from '../../http/difference-report.service';
import { CurrentBranchInfoService } from '../../httpui/current-branch-info.service';

@Injectable({
  providedIn: 'root'
})
export class DifferenceBranchInfoService {

  constructor (private diffService: DifferenceReportService, private branchInfoService: CurrentBranchInfoService) { }
  
  differences(branchId: string | number) {
    return this.parentBranch.pipe(
      take(1),
      switchMap((parentBranch)=>this.diffService.getDifferences(parentBranch,branchId))
    )
  }
  get parentBranch() {
    return this.branchInfoService.currentBranchDetail.pipe(
      //take(1),
      map((branches)=>branches.parentBranch.id)
    )
  }

  differenceReport(branchId: string | number) {
    return this.parentBranch.pipe(
      take(1),
      switchMap((parentBranch)=>this.diffService.getDifferenceReport(parentBranch, branchId))
    )
  }
}
