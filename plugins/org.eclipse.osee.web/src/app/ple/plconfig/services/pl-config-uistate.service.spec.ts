import { TestBed } from '@angular/core/testing';

import { PlConfigUIStateService } from './pl-config-uistate.service';

describe('PlConfigUIStateService', () => {
  let service: PlConfigUIStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({providers:[PlConfigUIStateService]});
    service = TestBed.inject(PlConfigUIStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  
  it('#get branchId should return string from observable when given a string', (done: DoneFn) => {
    service.branchIdNum = "string";
    service.branchId.subscribe(value => {
      expect(value).toBeInstanceOf(String);
      expect(value).toBe("string");
      done();
    })
    service.branchIdNum = "string";
  })
  it('#get viewBranchType should return string "all" from observable when given a string "all"', (done: DoneFn) => {
    service.viewBranchTypeString = "all";
    service.viewBranchType.subscribe(value => {
      expect(value).toBeInstanceOf(String);
      expect(value).toBe("all")
      done();
    })
    service.viewBranchTypeString = "all";
  })
  it('#get viewBranchType should return string "all" from observable when given a string "All"', (done: DoneFn) => {
    service.viewBranchTypeString = "All";
    service.viewBranchType.subscribe(value => {
      expect(value).toBeInstanceOf(String);
      expect(value).toBe("all")
      done();
    })
    service.viewBranchTypeString = "All";
  })
  it('#get viewBranchType should return string "working" from observable when given a string "Working"', (done: DoneFn) => {
    service.viewBranchTypeString = "Working";
    service.viewBranchType.subscribe(value => {
      expect(value).toBeInstanceOf(String);
      expect(value).toBe("working")
      done();
    })
    service.viewBranchTypeString = "Working";
  })
  it('#get viewBranchType should return string "baseline" from observable when given a string "Baseline"', (done: DoneFn) => {
    service.viewBranchTypeString = "Baseline";
    service.viewBranchType.subscribe(value => {
      expect(value).toBeInstanceOf(String);
      expect(value).toBe("baseline")
      done();
    })
    service.viewBranchTypeString = "Baseline";
  })
  it('#get updateReq should return boolean "true" from observable when given a bool "true"', (done: DoneFn) => {
    service.updateReq.subscribe(value => {
      expect(value).toBeInstanceOf(Boolean);
      expect(value).toBe(true)
      done();
    })
    service.updateReqConfig = true;
  })
});
