import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { SubMessagesService } from './sub-messages.service';

describe('SubMessagesService', () => {
  let service: SubMessagesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientModule]
    });
    service = TestBed.inject(SubMessagesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
