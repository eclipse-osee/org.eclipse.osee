import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ConfigGroupDialogComponent } from './config-group-dialog.component';

describe('ConfigGroupDialogComponent', () => {
  let component: ConfigGroupDialogComponent;
  let fixture: ComponentFixture<ConfigGroupDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatDialogModule,MatFormFieldModule,MatInputModule,MatListModule,NoopAnimationsModule,FormsModule],
      declarations: [ConfigGroupDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            editable: true,
            configGroup: {
              id: "1",
              name: "Group 1",
              views: [
                {
                  id: "2",
                  name: "View 1",
                  hasFeatureApplicabilities:true,
                },
                {
                  id: "3",
                  name: "View 2",
                  hasFeatureApplicabilities:true,
                },
                {
                  id: "4",
                  name: "View 3",
                  hasFeatureApplicabilities:true,
                }
              ]
            }
        }}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigGroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
