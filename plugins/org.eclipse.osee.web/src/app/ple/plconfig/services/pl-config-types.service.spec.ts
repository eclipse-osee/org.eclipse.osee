import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { PlConfigTypesService } from './pl-config-types.service';

describe('PlConfigTypesService', () => {
  let service: PlConfigTypesService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'put', 'post']);
    TestBed.configureTestingModule({
      providers: [
        {provide: HttpClient, useValue:httpClientSpy}
      ]
    });
    service = TestBed.inject(PlConfigTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
