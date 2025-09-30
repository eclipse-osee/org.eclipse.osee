import { TestBed } from '@angular/core/testing';

import { AttachmentTestingService } from './attachment-testing.service';

describe('AttachmentTestingService', () => {
  let service: AttachmentTestingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AttachmentTestingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
