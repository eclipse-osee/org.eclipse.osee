import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ApplicabilityListService } from './applicability-list.service';

describe('ApplicabilityListService', () => {
  let service: ApplicabilityListService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(ApplicabilityListService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
