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
import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { TestBed } from '@angular/core/testing';
import { iif, of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';

import { LayoutNotifierService } from './layout-notifier.service';

describe('LayoutNotifierService', () => {
  let service: LayoutNotifierService;
  let scheduler: TestScheduler
  Breakpoints.XSmall
  let observer: Partial<BreakpointObserver>={
    observe(value: string | readonly string[]) {
      let state1:BreakpointState={matches:true,breakpoints:{'(min-width: 1920px)':true}}
      
      return iif(()=>value==='(min-width: 1920px)',of(state1),of({matches:false,breakpoints:{value:false}}))
      // return of<BreakpointState>({
      //   matches: true, breakpoints: {
      //     '(max-width: 599.98px)': false,
      //     '(min-width: 600px) and (max-width: 959.98px)': false,
      //     '(min-width: 960px) and (max-width: 1279.98px)': false,
      //     '(min-width: 1280px) and (max-width: 1919.98px)': false,
      //     '(min-width: 1920px)':true
      // }})
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:BreakpointObserver,useValue:observer}]
    });
    service = TestBed.inject(LayoutNotifierService);
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));
  beforeEach(() => {
    spyOnProperty(window,'innerWidth').and.returnValue(1921)
  })
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return recommended table column width', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: '380px' }
      let expectedMarble = '(a|)'
      expectObservable(service.recommendedTableColumnWidth).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should return recommended table column count', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: 12 }
      let expectedMarble = '(a|)'
      expectObservable(service.recommendedTableColumnCount).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should return recommended card column count', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: 5 }
      let expectedMarble = '(a|)'
      expectObservable(service.recommendedCardColumnCount).toBe(expectedMarble, expectedObservable);
    })
  })
});
