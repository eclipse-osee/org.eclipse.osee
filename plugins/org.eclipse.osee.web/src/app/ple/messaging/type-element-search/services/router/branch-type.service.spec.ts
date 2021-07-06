import { TestBed } from '@angular/core/testing';
import { branchType } from '../../types/BranchTypes';

import { BranchTypeService } from './branch-type.service';

describe('BranchTypeService', () => {
  let service: BranchTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BranchTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Core Functionality',()=> {
    describe('Branch Type', () => {
      describe('Valid States', () => {
        it('should set type to baseline', () => {
          service.type='product line'
          expect(service.type).toEqual('baseline')
        });
      
        it('should set type to working', () => {
          service.type='working'
          expect(service.type).toEqual('working')
        });    
      })
      describe('Invalid States', () => {
        it('should throw an error when an invalid value is passed', () => {
          expect(() => { service.type = 'asdf' as branchType }).toThrow(new Error('Type is not a valid value. Invalid Value:' + 'asdf' + ' Valid values: product line,working'));
        });  
      })
    })
  })
});
