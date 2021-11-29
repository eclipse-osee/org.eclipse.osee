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
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { testBranchApplicability, testBranchListing } from '../testing/mockBranchService';
import { testBranchActions, testDataResponse, testWorkFlow } from '../testing/mockTypes';
import { view } from '../types/pl-config-applicui-branch-mapping';
import { configGroup } from '../types/pl-config-configurations';
import { PlConfigActionService } from './pl-config-action.service';
import { PlConfigBranchService } from './pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';
import { PlConfigUIStateService } from './pl-config-uistate.service';


describe('PlConfigCurrentBranchService', () => {
  let service: PlConfigCurrentBranchService;
  let ui: PlConfigUIStateService;
  let branchServiceSpy: jasmine.SpyObj<PlConfigBranchService>;
  let actionServiceSpy: jasmine.SpyObj<PlConfigActionService>;
  let scheduler: TestScheduler;

  beforeEach(() => {
    branchServiceSpy = jasmine.createSpyObj(
      'PlConfigBranchService',
      { //functions required to test
        getBranchApplicability:of(testBranchApplicability),
        getBranchState:of(testBranchListing),
        modifyConfiguration:of(testDataResponse),
        synchronizeGroup:of(testDataResponse),
        addFeature:of(testDataResponse),
        modifyFeature:of(testDataResponse),
        deleteFeature:of(testDataResponse),
        commitBranch:of(testDataResponse)
      }
    );
    actionServiceSpy = jasmine.createSpyObj(
      'PlConfigActionService',
      { //functions required to test
        'getAction':of(testBranchActions),
        'getWorkFlow':of(testWorkFlow)
      }
    );
    TestBed.configureTestingModule({
      providers: [
        { provide: PlConfigBranchService, useValue: branchServiceSpy },
        { provide: PlConfigActionService, useValue: actionServiceSpy },
      ]
    });
    service = TestBed.inject(PlConfigCurrentBranchService);
    ui = TestBed.inject(PlConfigUIStateService)
  });
  beforeEach(() => {
    ui.branchIdNum = '10';
    ui.updateReqConfig = true;
  })
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return the group list', () => {
    scheduler.run(({ expectObservable }) => {
      const expectedValues: { a: configGroup[] } = { a: testBranchApplicability.groups }
      expectObservable(service.groupList).toBe('a',expectedValues)
    })
  })

  it('should return the group count', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.groupCount).toBe('(aab)',{a:3,b:6})
    });
  })

  it('should return the view count', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.viewCount).toBe('(a)',{a:2})
    });
  })

  it('should return the grouping', () => {
    scheduler.run(({ expectObservable }) => {
      const expectedValues: { a: { group: configGroup, views: view[] }[] ,b:{ group: configGroup, views: view[] }[]} = {
        a:
          [  
            {   
              group: { id: '736857919', name: 'abGroup', configurations: ['200045', '200046'] },  
              views: [{ id: '200045', name: 'Product A', hasFeatureApplicabilities: true }, { id: '200046', name: 'Product B', hasFeatureApplicabilities: true }]  
            }
          ],
        b:
          [
            {
              group: { id: '-1', name: 'No Group', configurations: [] },
              views:[{ id: '200047', name: 'Product C', hasFeatureApplicabilities: true },{ id: '200048', name: 'Product D', hasFeatureApplicabilities: true }]
            },
            {
              group: { id: '736857919', name: 'abGroup', configurations: ['200045', '200046'] },
              views: [{ id: '200045', name: 'Product A', hasFeatureApplicabilities: true }, { id: '200046', name: 'Product B', hasFeatureApplicabilities: true }]
            },

          ]
      }
      expectObservable(service.grouping).toBe('(ab)',expectedValues)
    })
  })

  it('should return the headers', () => {
    scheduler.run(({ expectObservable }) => {
      const expectedValues: { a: string[],b: string[], c: string[], d: string[], e: string[], f: string[], g: string[], h:string[] } = {
        a: ['feature', 'abGroup'],
        b: ['feature', 'abGroup', 'Product A'],
        c: ['feature', 'abGroup', 'Product A', 'Product B'],
        d: ['feature', 'Product C'],
        e: ['feature', 'Product C', 'Product D'],
        f: ['feature', 'Product C', 'Product D', 'abGroup'],
        g: ['feature', 'Product C', 'Product D', 'abGroup', 'Product A'],
        h: ['feature', 'Product C', 'Product D', 'abGroup', 'Product A', 'Product B']
      }
      expectObservable(service.headers).toBe('(abcdefgh)',expectedValues)
    })
  })

  it('should return the secondary header length', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.secondaryHeaderLength).toBe('(abb)',{a:[1,3],b:[1,2,3]})
    });
  })

  it('should get the secondary headers', () => {
    scheduler.run(({ expectObservable }) => {
      const expectedValues: { a: string[],b: string[], c: string[] } = {
        a: ['   ', 'abGroup '],
        b: ['   ', 'No Group '],
        c: ['    ', 'No Group  ', 'abGroup '],
      }
      expectObservable(service.secondaryHeaders).toBe('(abc)',expectedValues)
    })
  })

  it('should get the top level headers', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.topLevelHeaders).toBe('(aa)',{a:[' ', 'Configurations', 'Groups'],b:[' ', 'Configurations']})
    });
  })

  it('should find a view by name', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.findViewByName('Product A')).toBe('(aaaaaa)', {
        a: {
          id: "200045",
          name: "Product A",
          hasFeatureApplicabilities: true
        }
      })
    });
  })

  it('should find a view by id', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.findViewById("200045")).toBe('(aa|)', {
        a: {
          id: "200045",
          name: "Product A",
          hasFeatureApplicabilities: true
        }
      })
    });
  })

  it('should find a group by name', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.findGroup('abGroup')).toBe('(a)', {
        a: {
          id: '736857919',
          name: 'abGroup',
          configurations:['200045','200046']
      }})
    })
  })
  // it('#branchApplicability should return expected branch applicability', done => {
  //   const expectedBranchApplicability = testBranchApplicability;
  //   branchServiceSpy.getBranchApplicability.and.returnValue(of(expectedBranchApplicability))
  //   service.branchApplicability.subscribe((value) => {
  //     expect(value).toEqual(testBranchApplicability);
  //     done();
  //   })
  // })
});
