import { TestBed } from '@angular/core/testing';

import { CurrentGraphService } from './current-graph.service';

describe('CurrentGraphService', () => {
  let service: CurrentGraphService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CurrentGraphService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
