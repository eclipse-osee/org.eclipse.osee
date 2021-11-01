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
import { shareReplay, switchMap, take } from 'rxjs/operators';
import { BranchUIService } from '../branch/branch-ui.service';
import { DifferenceBranchInfoService } from './difference-branch-info.service';

@Injectable({
  providedIn: 'root'
})
export class DiffReportBranchService {

  constructor (private differenceService: DifferenceBranchInfoService, private branchService: BranchUIService) { }
  
  get differences() {
    return this.branchService.id.pipe(
      take(1),
      switchMap((id) => this.differenceService.differences(id).pipe(
        shareReplay({bufferSize:5,refCount:true})
      ))
    )
  }
}
