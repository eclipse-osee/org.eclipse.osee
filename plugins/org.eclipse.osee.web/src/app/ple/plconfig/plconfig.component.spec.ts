import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of } from 'rxjs';

import { PlconfigComponent } from './plconfig.component';

describe('PlconfigComponent', () => {
  let component: PlconfigComponent;
  let fixture: ComponentFixture<PlconfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PlconfigComponent],
      providers: [{ provide: Router, useValue: { navigate: () => { }}},
        {
          provide: ActivatedRoute, useValue: {
            paramMap: of(
              convertToParamMap(
                {
                  branchId: '10',
                  branchType: 'all'
                }
              )
            )
          }
        },
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlconfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
