import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddConfigurationGroupDialogComponent } from './add-configuration-group-dialog.component';

describe('AddConfigurationGroupDialogComponent', () => {
  let component: AddConfigurationGroupDialogComponent;
  let fixture: ComponentFixture<AddConfigurationGroupDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddConfigurationGroupDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddConfigurationGroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
