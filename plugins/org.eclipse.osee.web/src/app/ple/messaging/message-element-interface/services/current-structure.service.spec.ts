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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed} from '@angular/core/testing';
import { tap } from 'rxjs/operators';
import { TestScheduler } from 'rxjs/testing';
import { DiffReportBranchService } from 'src/app/ple-services/ui/diff/diff-report-branch.service';
import { diffReportBranchServiceMock } from 'src/app/ple-services/ui/diff/diff-report-branch.service.mock';
import { applicabilityListServiceMock } from '../../shared/mocks/ApplicabilityListService.mock';
import { MimPreferencesServiceMock } from '../../shared/mocks/MimPreferencesService.mock';
import { typesServiceMock } from '../../shared/mocks/types.service.mock';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { TypesService } from '../../shared/services/http/types.service';
import { platformTypes1 } from '../../type-element-search/testing/MockResponses/PlatformType';
import { elementsMock } from '../../shared/mocks/element.mock';
import { structuresMock, structuresMock2 } from '../../shared/mocks/structure.mock';
import { elementServiceMock } from '../mocks/services/element.service.mock';
import { messageServiceMock } from '../mocks/services/messages.service.mock';
import { structureServiceRandomMock } from '../mocks/services/structure.service.mock';

import { CurrentStructureService } from './current-structure.service';
import { ElementService } from '../../shared/services/http/element.service';
import { MessagesStructureService } from '../../shared/services/http/messages.structure.service';
import { StructuresService } from '../../shared/services/http/structures.service';
import { ElementUiService } from './ui.service';
import { transactionResultMock } from '../../../../transactions/transaction.mock';
import { QueryService } from '../../shared/services/http/query.service';
import { QueryServiceMock } from '../../shared/mocks/query.service.mock';
import { PlatformTypeQuery } from '../../shared/types/MimQuery';

describe('CurrentStateService', () => {
  let service: CurrentStructureService;
  let ui: ElementUiService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: ElementService, useValue: elementServiceMock },
        { provide: StructuresService, useValue: structureServiceRandomMock },
        { provide: MessagesStructureService, useValue: messageServiceMock },
        { provide: TypesService, useValue: typesServiceMock },
        { provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
        { provide: ApplicabilityListService, useValue: applicabilityListServiceMock },
        { provide: ElementUiService },
        { provide: DiffReportBranchService, useValue: diffReportBranchServiceMock },
        { provide: QueryService, useValue: QueryServiceMock},
        CurrentStructureService,
      ],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(CurrentStructureService);
    ui = TestBed.inject(ElementUiService);
    httpTestingController = TestBed.inject(HttpTestingController);
    ui.DiffMode = false;
    ui.difference = [];
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get filtered structures', () => {
    scheduler.run(() => {
      service.branchId = "0";
      service.filter = "0";
      service.messageId = "1";
      service.subMessageId = "2";
      service.connection = "3";
      const expectedObservable = { a: structuresMock };
      const expectedMarble = '500ms a'
      scheduler.expectObservable(service.structures).toBe(expectedMarble, expectedObservable);
    })
  });

  it('should change an element and get a transactionResultMock back', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.partialUpdateElement({},'10')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create an element and get a transactionResultMock back', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createNewElement({},'10','10')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create a structure and get a transactionResultMock back', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createStructure(structuresMock[0])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should change a structure and get a transactionResultMock back', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.partialUpdateStructure({})).toBe(expectedMarble, expectedObservable);
    })
  })
  it('should change element platform type', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.changeElementPlatformType('10', '20', {
        description: '',
        interfaceLogicalType: '',
        interfacePlatform2sComplement: false,
        interfacePlatformTypeAnalogAccuracy: '',
        interfacePlatformTypeBitsResolution: '',
        interfacePlatformTypeBitSize: '',
        interfacePlatformTypeCompRate: '',
        interfacePlatformTypeDefaultValue: '',
        interfacePlatformTypeMaxval: '',
        interfacePlatformTypeMinval: '',
        interfacePlatformTypeMsbValue: '',
        interfacePlatformTypeUnits: '',
        interfacePlatformTypeValidRangeDescription: '',
        name: ''
      })).toBe(expectedMarble, expectedObservable);
    })
  });

  it('should relate an element', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.relateElement('10','20')).toBe(expectedMarble, expectedObservable);
    })
  });

  it('should relate a structure', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.relateStructure('10')).toBe(expectedMarble, expectedObservable);
    })
  });

  it('should perform a mutation for deleting a submessage relation', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = 'a';
      expectObservable(service.removeStructureFromSubmessage('10', '20')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should perform a mutation for deleting a submessage relation', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = 'a';
      expectObservable(service.removeElementFromStructure(elementsMock[0], structuresMock[0])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should perform a mutation for deleting a submessage relation', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = 'a';
      expectObservable(service.deleteElement(elementsMock[0])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should perform a mutation for deleting a structure', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: transactionResultMock };
      let expectedMarble = 'a';
      expectObservable(service.deleteStructure(structuresMock[0].id)).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should update user preferences', () => {
    scheduler.run(() => {
      service.branchId='10'
      let expectedObservable = { a: [transactionResultMock,transactionResultMock] };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.updatePreferences({branchId:'10',allowedHeaders1:['name','description',],allowedHeaders2:['name','description',],allHeaders1:['name','description','applicability'],allHeaders2:['name','description','applicability'],editable:true,headers1Label:'',headers2Label:'',headersTableActive:false})).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should get applicabilities', () => {
    scheduler.run(() => {
      let expectedObservable = { a: [{id:'1',name:'Base'},{id:'2',name:'Second'}] };
      let expectedMarble = 'a';
      scheduler.expectObservable(service.applic).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should get types', () => {
    scheduler.run(() => {
      service.branchId = "0";
      let expectedObservable = { a: platformTypes1 };
      let expectedMarble = 'a';
      scheduler.expectObservable(service.types).toBe(expectedMarble, expectedObservable);
    })
  })
  it('should get available structures', () => {
    scheduler.run(() => {
      let expectedObservable = { a: structuresMock,b:[structuresMock[0],structuresMock2[0]], c:[structuresMock[0],structuresMock[0],structuresMock[0]],d:[structuresMock[0],structuresMock[0],structuresMock[0],structuresMock[0]],e:[structuresMock[0],structuresMock[0],structuresMock[0],structuresMock[0],structuresMock[0]], };
      let expectedMarble = '(a)';
      scheduler.expectObservable(service.availableStructures).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should get available elements', () => {
    scheduler.run(() => {
      let expectedObservable = { a: elementsMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.availableElements).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should get and set branch type', () => {
    expect(service.BranchType).toEqual('')
    scheduler.run(({expectObservable,cold}) => {
      let expectedObservable = { a: '10', b: '8',c:'' };
      let expectedMarble = 'c---a----b';
      const makeemissions = cold('----a----(b|)', { a: '10', b: '8' }).pipe(tap((t) => service.BranchType = t));
      expectObservable(makeemissions).toBe('----a----(b|)', { a: '10', b: '8' });
      expectObservable(service.branchType).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should get a connection path', () => {
    scheduler.run(({expectObservable}) => {
      service.branchId = "10"
      service.BranchType = "abc"
      expectObservable(service.connectionsRoute).toBe("a", {a: "/ple/messaging/connections/abc/10"})
    })
  })

  it('done should complete', () => {
    scheduler.run(({ expectObservable,cold }) => {
      const expectedFilterValues = { a: true, b: undefined, c: false };
      const expectedMarble = '-(b|)';
      let delayMarble = '-a';
      cold(delayMarble).subscribe(() => service.toggleDone=true);
      expectObservable(service.done).toBe(expectedMarble,expectedFilterValues)
    })
  })

  it('should return an update', () => {
    scheduler.run(({cold, expectObservable}) => {
      let expectedObservable = { a: true }
      let expectedMarble = '101ms a';
      let delayMarble = '-a';
      cold(delayMarble).subscribe(() => service.update = true);
      expectObservable(service.updated).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should return a structure', () => {
    scheduler.run(({cold, expectObservable}) => {
      let expectedObservable = { a: structuresMock[0] }
      let expectedMarble = 'a';
      let delayMarble = '- 100ms a ';
      cold(delayMarble).subscribe(() => service.update = true);
      expectObservable(service.getStructureRepeating('abcdef')).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should perform a query', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: "Hello" }
      let expectedMarble = "a";
      expectObservable(service.query(new PlatformTypeQuery())).toBe(expectedMarble, expectedObservable);
    })
  })
});
