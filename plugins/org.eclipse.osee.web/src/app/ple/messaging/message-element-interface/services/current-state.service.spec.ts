import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed} from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { response } from '../../connection-view/mocks/Response.mock';
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
        { provide: PlatformTypeService, useValue:platformTypeServiceMock}
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
      const expectedObservable = { a: [] };
      const expectedMarble = '500ms a'
      scheduler.expectObservable(service.structures).toBe(expectedMarble, expectedObservable);
    })
  });

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
});
