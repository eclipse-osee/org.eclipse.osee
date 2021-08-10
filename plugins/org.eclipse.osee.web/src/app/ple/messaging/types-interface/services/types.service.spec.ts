import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { TransactionBuilderService } from 'src/app/transactions/transaction-builder.service';
import { transactionBuilderMock } from 'src/app/transactions/transaction-builder.service.mock';
import { transactionMock } from 'src/app/transactions/transaction.mock';
import { apiURL } from 'src/environments/environment';
import { platformTypesMock } from '../../message-element-interface/mocks/ReturnObjects/PlatformTypes.mock';

import { TypesService } from './types.service';

describe('TypesService', () => {
  let service: TypesService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:TransactionBuilderService,useValue:transactionBuilderMock}],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(TypesService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a platform type transaction', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: transactionMock };
      const expectedMarble = '(a|)'
      scheduler.expectObservable(service.createPlatformType('10', platformTypesMock[0],[])).toBe(expectedMarble, expectedfilterValues)
    })
  })

  it('should create a transaction to change a platform type', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: transactionMock };
      const expectedMarble = '(a|)'
      scheduler.expectObservable(service.changePlatformType('10', platformTypesMock[0])).toBe(expectedMarble, expectedfilterValues)
    })
  })

  it('should perform a mutation', () => {
    service.performMutation({ branch: '10', txComment: '' },'10').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/txs');
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })

  it('should fetch logical types', () => {
    service.logicalTypes.subscribe();
    const req = httpTestingController.expectOne(apiURL+"/mim/logicalType");
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })

  it('should fetch logical type details', () => {
    service.getLogicalTypeFormDetail('10').subscribe();
    const req = httpTestingController.expectOne(apiURL+"/mim/logicalType/"+10);
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })

  it('should get filtered types', () => {
    service.getFilteredTypes('', '10').subscribe();
    const req = httpTestingController.expectOne(apiURL+"/mim/branch/" + 10+ "/types/filter/" + "");
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })
});
