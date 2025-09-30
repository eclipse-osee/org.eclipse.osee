import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowAttachmentsComponent } from './workflow-attachments.component';

describe('WorkflowAttachmentsComponent', () => {
  let component: WorkflowAttachmentsComponent;
  let fixture: ComponentFixture<WorkflowAttachmentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkflowAttachmentsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WorkflowAttachmentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
