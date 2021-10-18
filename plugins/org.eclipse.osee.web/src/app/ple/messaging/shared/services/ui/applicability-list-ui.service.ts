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
import { share, switchMap, repeatWhen, shareReplay } from 'rxjs/operators';
import { UiService } from 'src/app/ple-services/ui/ui.service';
import { ApplicabilityListService } from '../http/applicability-list.service';

@Injectable({
  providedIn: 'root'
})
export class ApplicabilityListUIService {

  private _applics = this.ui.id.pipe(
    share(),
    switchMap(id => this.applicabilityService.getApplicabilities(id).pipe(
      repeatWhen(_ => this.ui.update),
      share(),
    )),
    shareReplay({ bufferSize: 1, refCount: true }),
  )
  constructor (private ui: UiService, private applicabilityService: ApplicabilityListService) { }
  
  /**
   * @todo update the updating observable to have some specificity
   */
  get applic() {
    return this._applics;
  }
}
