import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddAttachmentsDialogComponent } from './add-attachments-dialog.component';

describe('AddAttachmentsDialogComponent', () => {
  let component: AddAttachmentsDialogComponent;
  let fixture: ComponentFixture<AddAttachmentsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddAttachmentsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddAttachmentsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
