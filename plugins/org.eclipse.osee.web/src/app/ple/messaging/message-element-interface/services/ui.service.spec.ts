import { TestBed } from '@angular/core/testing';
import { count } from 'rxjs/operators';
import {TestScheduler} from 'rxjs/testing'
import { UiService } from './ui.service';

describe('UiService', () => {
  let service: UiService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UiService);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  
  it('should set filter value', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: 'a', b: 'b' };
      const expectedMarble = 'a'
      scheduler.expectObservable(service.filter).toBe(expectedMarble, expectedfilterValues);
      service.filterString = "a";
      service.filterString = "a";
      service.filterString = "b";
      service.filterString = "a";
    })
  });
  
  it('should set branch value', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: '1', b: '2' };
      const expectedMarble = 'a'
      scheduler.expectObservable(service.BranchId).toBe(expectedMarble, expectedfilterValues);
      service.BranchIdString='1'
      service.BranchIdString='1'
      service.BranchIdString='2'
      service.BranchIdString='1'
    })
  });

  it('should set message value', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: '1', b: '2' };
      const expectedMarble = 'a'
      scheduler.expectObservable(service.messageId).toBe(expectedMarble, expectedfilterValues);
      service.messageIdString='1'
      service.messageIdString='1'
      service.messageIdString='2'
      service.messageIdString='1'
    })
  });

  it('should set sub message value', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: '1', b: '2' };
      const expectedMarble = 'a'
      scheduler.expectObservable(service.subMessageId).toBe(expectedMarble, expectedfilterValues);
      service.subMessageIdString='1'
      service.subMessageIdString='1'
      service.subMessageIdString='2'
      service.subMessageIdString='1'
    })
  });
});
