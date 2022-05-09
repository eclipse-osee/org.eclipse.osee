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
import { map, switchMap, take } from 'rxjs/operators';
import { BranchTransactionService } from '../http/branch-transaction.service';
import { UiService } from '../ui/ui.service';
import { UpdateService } from '../ui/update/update.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentBranchTransactionService {

  private _undoLatest = this._uiService.id.pipe(
    take(1),
    switchMap(id => this._branchTransactionService.undoLatest(id).pipe(
      map(result => { this._updateService.updated = true; return result; })
    ))
  );
  constructor (private _uiService: UiService, private _branchTransactionService: BranchTransactionService, private _updateService: UpdateService) { }
  
  get undoLatest() {
    return this._undoLatest;
  }
}
