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
import { combineLatest, iif, of } from 'rxjs';
import { share, switchMap } from 'rxjs/operators';
import { BranchInfoService } from '../../ple-services/http/branch-info.service';
import { BranchUIService } from '../../ple-services/ui/branch/branch-ui.service';
import { BranchCategoryService } from '../../shared-services/ui/branch-category.service';

@Injectable({
  providedIn: 'root'
})
export class BranchListService {

  private _branches = combineLatest([this.ui.type, this.categoryService.branchCategory, this.categoryService.actionSearch]).pipe(
    switchMap(([type, category, searchType]) => of(this.updateType(type)).pipe(
      switchMap((viewBranchType)=> iif(
        ()=>(viewBranchType === 'all' || viewBranchType === 'working' || viewBranchType === 'baseline') && category !=='' && category !=='0',this.branchService.getBranches(viewBranchType,category, searchType),of([])))
    )),
    share()
  )

  constructor (private branchService: BranchInfoService, private ui: BranchUIService, private categoryService: BranchCategoryService) { }
  
  private updateType(value: string) {
    if (value === 'product line') {
      return 'baseline'
    } else if (value === 'working') {
      return 'working'
    } else {
      return value;
    }
    //"product line , working"
  }
  get branches() {
    return this._branches;
  }
}
