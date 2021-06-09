import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { PlConfigUserService } from './pl-config-user.service';

describe('PlConfigUserService', () => {
  let service: PlConfigUserService;

  beforeEach(() => {
    const httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'put', 'post']);
    TestBed.configureTestingModule({
      providers: [
      {provide: HttpClient, useValue:httpClientSpy}
    ]});
    service = TestBed.inject(PlConfigUserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
