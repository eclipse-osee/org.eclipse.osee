import { TestBed } from '@angular/core/testing';

import { PlConfigTypesService } from './pl-config-types.service';

describe('PlConfigTypesService', () => {
  let service: PlConfigTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlConfigTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
