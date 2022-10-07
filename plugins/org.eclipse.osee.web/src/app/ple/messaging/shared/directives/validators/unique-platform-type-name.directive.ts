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
import { Directive, forwardRef } from '@angular/core';
import { AbstractControl, AsyncValidator, NG_ASYNC_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import { debounceTime, iif, map, Observable, of, Subject, switchMap, take, tap } from 'rxjs';
import { ATTRIBUTETYPEID } from '../../../../../types/constants/AttributeTypeId.enum';
import { CurrentQueryService } from '../../services/ui/current-query.service';
import { andQuery, MimQuery, PlatformTypeQuery } from '../../types/MimQuery';
import { PlatformType } from '../../types/platformType';

@Directive({
  selector: '[oseeUniquePlatformTypeName]',
  providers:[{provide: NG_ASYNC_VALIDATORS,useExisting:forwardRef(()=>UniquePlatformTypeNameDirective), multi:true}]
})
export class UniquePlatformTypeNameDirective implements AsyncValidator {

  constructor (private queryService: CurrentQueryService) { }
  
  validate(control: AbstractControl<string,string>): Observable<ValidationErrors | null> {
    return of(control.value).pipe(
      debounceTime(500),
      switchMap(name => of(new andQuery(ATTRIBUTETYPEID.NAME, name)).pipe(
        map(nameQuery => new PlatformTypeQuery(undefined, [nameQuery])),
        switchMap(query => this.queryService.queryExact(query as MimQuery<PlatformType>)),
        switchMap(results => results.length > 0? of<ValidationErrors>({notUnique:{value:name}}) : of(null))
      )),
      take(1),
    )

  }

}
