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

@Injectable({
  providedIn: 'root'
})
export class DifferenceBranchInfoService {

  constructor (private diffService: DifferenceReportService, private branchInfoService: BranchInfoService) { }
  
  differences(branchId: string | number) {
    return this.parentBranch(branchId).pipe(
      switchMap((parentBranch)=>this.diffService.getDifferences(parentBranch,branchId))
    )
  }
  parentBranch(branchId: string | number) {
    return this.branchInfoService.getBranches(branchId as string).pipe(
      take(1),
      map((branches)=>branches.parentBranch.id)
    )
  }
}
