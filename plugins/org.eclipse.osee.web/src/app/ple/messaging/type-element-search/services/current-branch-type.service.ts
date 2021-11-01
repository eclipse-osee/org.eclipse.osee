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
import { iif, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BranchListing } from '../../../../types/branches/BranchListing';
import { BranchService } from './http/branch.service';
import { BranchTypeService } from './router/branch-type.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentBranchTypeService {
  private _branches = this.typeService.BranchType.pipe(
    switchMap((val)=>iif(()=>val!==''&& (val === 'baseline' || val === 'working'),this.branchService.getBranches(val),of<BranchListing[]>([])))
  )
  constructor (private branchService: BranchService, private typeService: BranchTypeService) { }
  
  get branches() {
    return this._branches;
  }
}
