import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { PlatformTypeService } from './platform-type.service';

describe('PlatformTypeService', () => {
  let service: PlatformTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(PlatformTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
