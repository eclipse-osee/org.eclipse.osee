import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { PlConfigActionService } from './pl-config-action.service';

describe('PlConfigActionService', () => {
  let service: PlConfigActionService;

  beforeEach(() => {
    const httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'put', 'post']);
    TestBed.configureTestingModule({
      providers: [
      {provide: HttpClient, useValue:httpClientSpy}
    ]});
    service = TestBed.inject(PlConfigActionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
