import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { TypesApiResponse } from '../types/ApiResponse';
import { logicalType, logicalTypeFormDetail } from '../types/logicaltype';
import { PlatformType } from '../types/platformType.d';

import { CurrentTypesService } from './current-types.service';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';

class PlatformTypeInstance implements PlatformType{
  id?: string | undefined ='';
  interfaceLogicalType: string='';
  interfacePlatform2sComplement: boolean=false;
  interfacePlatformTypeAnalogAccuracy: string | null='';
  interfacePlatformTypeBitsResolution: string | null='';
  interfacePlatformTypeBitSize: string | null='';
  interfacePlatformTypeCompRate: string | null='';
  interfacePlatformTypeDefaultValue: string | null='';
  interfacePlatformTypeEnumLiteral: string | null='';
  interfacePlatformTypeMaxval: string | null='';
  interfacePlatformTypeMinval: string | null='';
  interfacePlatformTypeMsbValue: string | null='';
  interfacePlatformTypeUnits: string | null='';
  interfacePlatformTypeValidRangeDescription: string | null='';
  name: string='';
  constructor () {}
  
}

describe('CurrentTypesServiceService', () => {
  let service: CurrentTypesService;
  let uiService: PlMessagingTypesUIService;
  let httpTestingController:HttpTestingController

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(CurrentTypesService);
    uiService = TestBed.inject(PlMessagingTypesUIService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch data from backend', fakeAsync(() => {
    let testData: PlatformType[] = []
    uiService.BranchIdString = "10";
    service.typeData.subscribe();
    uiService.filterString = "A filter";
    tick(500);
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + "10" + "/types/filter/" + "A filter");
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  }));

  it('should set singleLineAdjustment to 0', fakeAsync(() => {
    let testData: PlatformType[] = [new PlatformTypeInstance(), new PlatformTypeInstance(), new PlatformTypeInstance()]
    uiService.columnCountNumber = 2;
    uiService.BranchIdString = "10";
    service.typeData.subscribe();
    uiService.filterString = "A filter";
    tick(500);
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + "10" + "/types/filter/" + "A filter");
    //expect(req.request.method).toEqual('GET');
    req?.flush(testData);
    httpTestingController.verify();
    expect(uiService.singleLineAdjustment.getValue()).toEqual(0);
  }));

  it('should send a patch request', () => {
    uiService.BranchIdString = "10";
    service.partialUpdate({}).subscribe();
    let testData: TypesApiResponse = {
      empty: false,
      errorCount: 0,
      errors: false,
      failed: false,
      ids: [],
      infoCount: false,
      numErrors: 0,
      numErrorsViaSearch: 0,
      numWarnings: 0,
      numWarningsViaSearch: 0,
      results: [],
      success: true,
      tables: [],
      title: '',
      txId: '',
      warningCount: 0
    };
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + "10" + "/types");
    expect(req.request.method).toEqual('PATCH');
    req.flush(testData);
    httpTestingController.verify();
  });

  it('should send a post request', () => {
    uiService.BranchIdString = "10";
    service.createType({}).subscribe();
    let testData: TypesApiResponse = {
      empty: false,
      errorCount: 0,
      errors: false,
      failed: false,
      ids: [],
      infoCount: false,
      numErrors: 0,
      numErrorsViaSearch: 0,
      numWarnings: 0,
      numWarningsViaSearch: 0,
      results: [],
      success: true,
      tables: [],
      title: '',
      txId: '',
      warningCount: 0
    };
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + "10" + "/types");
    expect(req.request.method).toEqual('POST');
    req.flush(testData);
    httpTestingController.verify();
  });

  it('should fetch logical types', () => {
    service.logicalTypes.subscribe();
    let testData: logicalType[] = [];
    const req = httpTestingController.expectOne(apiURL + "/mim/logicalType");
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  });

  it('should fetch a specific logical type', () => {
    service.getLogicalTypeFormDetail("1").subscribe();
    let testData: logicalTypeFormDetail = {
      fields: [],
      id: '1',
      name: 'name',
      idString: '1',
      idIntValue:1
    }
    const req = httpTestingController.expectOne(apiURL + "/mim/logicalType/1");
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  });
});
