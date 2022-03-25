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
import { share, switchMap, repeatWhen, shareReplay, take, tap } from 'rxjs/operators';
import { transaction } from 'src/app/transactions/transaction';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { PlatformType } from '../../types/platformType';
import { TypesService } from '../http/types.service';

@Injectable({
  providedIn: 'root'
})
export class TypesUIService {

  private _types = this._ui.id.pipe(
    share(),
    switchMap(x => this._typesService.getTypes(x).pipe(
      repeatWhen(_ => this._ui.update),
      share(),
    )),
    shareReplay({ bufferSize: 1, refCount: true }),
  )
  constructor (private _ui: UiService, private _typesService: TypesService) { }
  get types() {
    return this._types;
  }

  getFilteredTypes(filter: string) {
    return this._ui.id.pipe(
      take(1),
      share(),
      switchMap((branch) => this._typesService.getFilteredTypes(filter, branch).pipe(
        share(),
      )),
      shareReplay({bufferSize:1,refCount:true})
    )
  }
  getType( typeId: string) {
    return this._ui.id.pipe(
      take(1),
      share(),
      switchMap(branch => this._typesService.getType(branch, typeId).pipe(
        share()
      )),
      shareReplay({ bufferSize: 1, refCount: true })
    )
  }

  getTypeFromBranch(branchId: string, typeId: string) {
    return this._typesService.getType(branchId,typeId)
  }
  changeType(type:Partial<PlatformType>) {
    return this._ui.id.pipe(
      take(1),
      switchMap((branchId)=>this._typesService.changePlatformType(branchId,type))
    )
  }
  performMutation(body: transaction) {
    return this._ui.id.pipe(
      take(1),
    switchMap((branchId)=>this._typesService.performMutation(body))
    )
  }
}
