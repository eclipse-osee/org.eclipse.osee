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
import { userDataAccountServiceMock } from 'src/app/ple/plconfig/testing/mockUserDataAccountService';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { response } from '../../connection-view/mocks/Response.mock';
import { applicabilityListServiceMock } from '../../shared/mocks/ApplicabilityListService.mock';
import { MimPreferencesServiceMock } from '../../shared/mocks/MimPreferencesService.mock';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { elementsMock } from '../mocks/ReturnObjects/element.mock';
import { platformTypesMock } from '../mocks/ReturnObjects/PlatformTypes.mock';
import { structuresMock } from '../mocks/ReturnObjects/structure.mock';
import { elementServiceMock } from '../mocks/services/element.service.mock';
import { messageServiceMock } from '../mocks/services/messages.service.mock';
import { platformTypeServiceMock } from '../mocks/services/platform-type.service.mock';
import { structureServiceMock } from '../mocks/services/structure.service.mock';

import { CurrentStateService } from './current-state.service';
import { ElementService } from './element.service';
import { MessagesService } from './messages.service';
import { PlatformTypeService } from './platform-type.service';
import { StructuresService } from './structures.service';

describe('CurrentStateService', () => {
  let service: CurrentStateService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: ElementService, useValue: elementServiceMock },
        { provide: StructuresService, useValue: structureServiceMock },
        { provide: MessagesService, useValue: messageServiceMock },
        { provide: PlatformTypeService, useValue: platformTypeServiceMock },
        { provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
        { provide: ApplicabilityListService, useValue: applicabilityListServiceMock}
      ],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(CurrentStateService);
    httpTestingController = TestBed.inject(HttpTestingController);
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

  it('should get a structure', () => {
    scheduler.run(({ expectObservable }) => {
      service.branchId = "0";
      service.filter = "0";
      service.messageId = "1";
      service.subMessageId = "2";
      service.connection = "3";
      const expectedObservable = { a: structuresMock[0] };
      const expectedMarble = 'a'
      expectObservable(service.getStructure('abcdef')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should change an element and get a response back', () => {
    scheduler.run(() => {
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.partialUpdateElement({},'10')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create an element and get a response back', () => {
    scheduler.run(() => {
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createNewElement({},'10','10')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create a structure and get a response back', () => {
    scheduler.run(() => {
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createStructure(structuresMock[0])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should change a structure and get a response back', () => {
    scheduler.run(() => {
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.partialUpdateStructure({})).toBe(expectedMarble, expectedObservable);
    })
  })
  it('should change element platform type', () => {
    scheduler.run(() => {
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.changeElementPlatformType('10', '20', '30')).toBe(expectedMarble, expectedObservable);
    })
  });

  it('should relate an element', () => {
    scheduler.run(() => {
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.relateElement('10','20')).toBe(expectedMarble, expectedObservable);
    })
  });

  it('should relate a structure', () => {
    scheduler.run(() => {
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.relateStructure('10')).toBe(expectedMarble, expectedObservable);
    })
  });

  it('should perform a mutation for deleting a submessage relation', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: response };
      let expectedMarble = 'a';
      expectObservable(service.removeStructureFromSubmessage('10', '20')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should perform a mutation for deleting a submessage relation', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: response };
      let expectedMarble = 'a';
      expectObservable(service.removeElementFromStructure(elementsMock[0], structuresMock[0])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should perform a mutation for deleting a submessage relation', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: response };
      let expectedMarble = 'a';
      expectObservable(service.deleteElement(elementsMock[0])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should perform a mutation for deleting a structure', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: response };
      let expectedMarble = 'a';
      expectObservable(service.deleteStructure(structuresMock[0].id)).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should update user preferences', () => {
    scheduler.run(() => {
      service.branchId='10'
      let expectedObservable = { a: [response,response] };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.updatePreferences({branchId:'10',allowedHeaders1:['hello','hello3'],allowedHeaders2:['hello2','hello3'],allHeaders1:['hello'],allHeaders2:['hello2'],editable:true,headers1Label:'',headers2Label:'',headersTableActive:false})).toBe(expectedMarble, expectedObservable);
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
      let expectedObservable = { a: platformTypesMock };
      let expectedMarble = 'a';
      scheduler.expectObservable(service.types).toBe(expectedMarble, expectedObservable);
    })
  })
  it('should get available structures', () => {
    scheduler.run(() => {
      let expectedObservable = { a: structuresMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.availableStructures).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should complete immediately due to a lack of elements in structuresMock', () => {
    scheduler.run(() => {
      let expectedObservable = { a: elementsMock };
      let expectedMarble = '|';
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

  it('should return structure multiple times', () => {
    scheduler.run(({cold, expectObservable}) => {
      let expectedObservable = { a: structuresMock[0] }
      let expectedMarble = 'a 200ms a';
      let delayMarble = '- 100ms a';
      cold(delayMarble).subscribe(() => service.update = true);
      expectObservable(service.getStructureRepeating('abcdef')).toBe(expectedMarble,expectedObservable)
    })
  })
});
