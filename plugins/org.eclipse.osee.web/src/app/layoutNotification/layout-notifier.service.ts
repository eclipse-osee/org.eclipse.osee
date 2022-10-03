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
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Injectable } from '@angular/core';
import { asyncScheduler, combineLatest, from, iif, Observable, of, range } from 'rxjs';
import { map, mergeMap, pairwise, reduce, share, shareReplay, startWith, switchMap, tap } from 'rxjs/operators';
import { pageState, tableRecommendations } from './layout.recommendations';

@Injectable({
  providedIn: 'root'
})
export class LayoutNotifierService {

  private _isXSmall = this.observer.observe(Breakpoints.XSmall).pipe(
    switchMap((states) => from(Object.entries(states.breakpoints)).pipe(
      switchMap(([query, matched]) => of(matched).pipe(
      )),
    )),
  )
  /**
   * helpful debug utilities to see if layout falls within ranges
   */
  /* istanbul ignore next */
  private _smallMatchStrings = range(0, 9600, asyncScheduler).pipe(
    startWith(undefined),
    pairwise(),
    mergeMap(([num1, num2]) => iif(() => num1 !== undefined && num2 !== undefined, of(`(min-width: ${num1 as number /10}px) and (max-width: ${num2 as number /10}px)`), of(`(max-width: ${num2 as number/100}px)`)).pipe(
    )),
    reduce((acc, curr) => [...acc, curr], [] as string[]),
    share(),
    shareReplay(1)
  )
  /* istanbul ignore next */
  private _mediumMatchStrings = range(9600, 3200,asyncScheduler).pipe(
    startWith(undefined),
    pairwise(),
    mergeMap(([num1, num2]) => iif(() => num1 !== undefined && num2 !== undefined, of(`(min-width: ${num1 as number /10}px) and (max-width: ${num2 as number /10}px)`), of(`(max-width: ${num2 as number/100}px)`)).pipe(
    )),
    reduce((acc, curr) => [...acc, curr], [] as string[]),
    share(),
    shareReplay(1)
  )
  /* istanbul ignore next */
  private _largeMatchStrings = range(12800,6400,asyncScheduler).pipe(
    startWith(undefined),
    pairwise(),
    mergeMap(([num1, num2]) => iif(() => num1 !== undefined && num2 !== undefined, of(`(min-width: ${num1 as number /10}px) and (max-width: ${num2 as number /10}px)`), of(`(max-width: ${num2 as number/100}px)`)).pipe(
    )),
    reduce((acc, curr) => [...acc, curr], [] as string[]),
    share(),
    shareReplay(1)
  )
  private _isXLarge = this.observer.observe(Breakpoints.XLarge).pipe(
    switchMap((states) => from(Object.entries(states.breakpoints)).pipe(
      switchMap(([query, matched]) => of(matched).pipe(
      )),
    )),
  )
  private _isLarge = of([Breakpoints.Large]).pipe(
    switchMap((queries) => this.observer.observe(queries).pipe(
      switchMap((states) => from(Object.entries(states.breakpoints)).pipe(
        switchMap(([query, matched]) => of(matched).pipe(
        )),
        reduce((acc, curr) => [...acc, curr], [] as boolean[]),
      )),
    )),
    map(results => results.includes(true)),
  )
  private _isMedium=of([Breakpoints.Medium]).pipe(
    switchMap((queries) => this.observer.observe(queries).pipe(
      switchMap((states) => from(Object.entries(states.breakpoints)).pipe(
        switchMap(([query, matched]) => of(matched).pipe(
        )),
        reduce((acc, curr) => [...acc, curr], [] as boolean[]),
      )),
    )),
    map(results => results.includes(true)),
  )
  private _isSmall = of([Breakpoints.Small]).pipe(
    switchMap((queries) => this.observer.observe(queries).pipe(
      switchMap((states) => from(Object.entries(states.breakpoints)).pipe(
        switchMap(([query, matched]) => of(matched).pipe(
        )),
        reduce((acc, curr) => [...acc, curr], [] as boolean[]),
      )),
    )),
    map(results => results.includes(true)),
  )

  private _recommendedCardColumnCount = combineLatest([this.isXSmall,this.isSmall, this.isMedium, this.isLarge, this.isXLarge]).pipe( //should be a number 1,2,3,4,5
    switchMap(([xsmall, small, medium, large, xlarge]) =>
      iif(() => xsmall,of(1),
        iif(() => small, of(2),
          iif(() => medium, of(3),
            iif(() => large, of(4),
              iif(() => xlarge, of(5),
              of(5))
            )
          )
        )
      )
    )
  )
  private _tableColumnRecommendations:Observable<tableRecommendations> = combineLatest([this.isXSmall,this.isSmall, this.isMedium, this.isLarge, this.isXLarge]).pipe(
    switchMap(([xsmall, small, medium, large, xlarge]) =>
      iif(() => xsmall, of({ width:'80px',columns:4}),
        iif(() => small, of({ width: '120px', columns: 4 }),
          iif(() => medium, of({ width: '137px', columns: 7 }),
            iif(() => large, of({ width: '300px', columns: 9 }),
              iif(() => xlarge, of({ width: '380px', columns: 12 }),
              of({ width: '380px', columns: 12 }))
            )
          )
        )
      )
    )
  )
  private _tableColumnRecommendationWidth = this.recommendedTableColumns.pipe(
    map((x)=>x.width)
  )
  private _tableColumnRecommendationColumnCount = this.recommendedTableColumns.pipe(
    map((x)=>x.columns)
  )
  private _pageState:Observable<pageState> = combineLatest<[boolean,boolean,boolean,boolean,boolean,number,tableRecommendations]>([this.isXSmall,this.isSmall, this.isMedium, this.isLarge, this.isXLarge,this.recommendedCardColumnCount, this.recommendedTableColumns]).pipe(
    switchMap(([xsmall, small, medium, large, xlarge, cardColumnCount, tableRecommendations]) => of({ xsmall: xsmall, small: small, medium: medium, large: large, xlarge: xlarge, recommendedCardColumnCount: cardColumnCount, tableRecommendations: tableRecommendations })
    ),
    share(),
    shareReplay({refCount:true,bufferSize:1})
  )
  constructor (private observer: BreakpointObserver) { }
  
  get isSmall() {
    return this._isSmall;
  }
  get isMedium() {
    return this._isMedium;
  }
  get isLarge() {
    return this._isLarge;
  }
  get isXLarge() {
    return this._isXLarge;
  }
  get isXSmall() {
    return this._isXSmall;
  }
  get layout() {
    return this._pageState;
  }
  get recommendedCardColumnCount() {
    return this._recommendedCardColumnCount;
  }
  get recommendedTableColumns() {
    return this._tableColumnRecommendations;
  }
  get recommendedTableColumnWidth() {
    return this._tableColumnRecommendationWidth;
  }
  get recommendedTableColumnCount() {
    return this._tableColumnRecommendationColumnCount;
  }
}
