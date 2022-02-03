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
import { share, switchMap, repeatWhen, shareReplay, take } from 'rxjs/operators';
import { UiService } from '../../../../../ple-services/ui/ui.service';
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
}
