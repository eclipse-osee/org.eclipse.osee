import { TestBed } from '@angular/core/testing';

import { BranchCategoryService } from './branch-category.service';

describe('BranchCategoryService', () => {
  let service: BranchCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BranchCategoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
