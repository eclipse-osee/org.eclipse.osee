import { TestBed } from '@angular/core/testing';

import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';

describe('PlMessagingTypesUIService', () => {
  let service: PlMessagingTypesUIService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlMessagingTypesUIService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
