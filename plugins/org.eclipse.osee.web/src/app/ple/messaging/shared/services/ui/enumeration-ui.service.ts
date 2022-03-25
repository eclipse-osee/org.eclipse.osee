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
import { from, iif, of } from 'rxjs';
import { share, switchMap, shareReplay, take, mergeMap, tap } from 'rxjs/operators';
import { BranchUIService } from 'src/app/ple-services/ui/branch/branch-ui.service';
import { relation, transaction } from 'src/app/transactions/transaction';
import { enumeration, enumerationSet, enumSet } from '../../types/enum';
import { EnumerationSetService } from '../http/enumeration-set.service';

@Injectable({
  providedIn: 'root'
})
export class EnumerationUIService {
  
  private _enumSets = this.ui.id.pipe(
    share(),
    switchMap(id => this.enumSetService.getEnumSets(id).pipe(
      share(),
    )),
    shareReplay({ bufferSize: 1, refCount: true })
  )
  constructor (private enumSetService: EnumerationSetService, private ui: BranchUIService) { }
  createEnumSetToPlatformTypeRelation(sideA?: string) {
    return this.enumSetService.createEnumSetToPlatformTypeRelation(sideA);
  }
  createEnumSet(branchId: string, type: enumSet | Partial<enumSet>, relations: relation[], transaction?: transaction) {
    return this.enumSetService.createEnumSet(branchId,type,relations,transaction)
  }
  createPlatformTypeToEnumSetRelation(sideB?: string) {
    return this.enumSetService.createPlatformTypeToEnumSetRelation(sideB);
  }
  createEnumToEnumSetRelation(sideA?: string) {
    return this.enumSetService.createEnumToEnumSetRelation(sideA);
  }
  createEnum(branchId: string, type: enumeration | Partial<enumeration>, relations: relation[], transaction?: transaction) {
    return this.enumSetService.createEnum(branchId, type, relations, transaction);
  }
  get enumSets() {
    return this._enumSets;
  }
  getEnumSet(platformTypeId: string) {
    return this.ui.id.pipe(
      take(1),
      switchMap((branchId)=>this.enumSetService.getEnumSet(branchId,platformTypeId))
    )
  }
  changeEnumSet(changes:enumerationSet,enumerations?:enumeration[]) {
    return this.ui.id.pipe(
      take(1),
      switchMap((branchId) => this.enumSetService.changeEnumSet(branchId, changes).pipe(
        switchMap((transaction) => iif(() => enumerations !== undefined && enumerations?.length > 0, of(enumerations as enumeration[]).pipe(
          mergeMap(enumerations => from(enumerations).pipe(
            mergeMap((currentEnum) => this.enumSetService.createEnumToEnumSetRelation(changes.id).pipe(
              switchMap((currentEnumRelation)=>this.enumSetService.createEnum(branchId,currentEnum,[currentEnumRelation],transaction))
            ))
          ))
        ),
          of(transaction)
        )),
        switchMap((transactionFinal) => this.enumSetService.performMutation(transactionFinal))
      ))
    )
  }
}
