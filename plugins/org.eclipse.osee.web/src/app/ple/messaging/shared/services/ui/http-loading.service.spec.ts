import { TestBed } from '@angular/core/testing';

import { HttpLoadingService } from './http-loading.service';

describe('HttpLoadingService', () => {
  let service: HttpLoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HttpLoadingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
