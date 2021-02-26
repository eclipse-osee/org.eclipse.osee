import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { of } from 'rxjs';
import { PlConfigActionService } from '../../services/pl-config-action.service';

import { CreateActionDialogComponent } from './create-action-dialog.component';

describe('CreateActionDialogComponent', () => {
  let component: CreateActionDialogComponent;
  let fixture: ComponentFixture<CreateActionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateActionDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            originator: {
              id: '',
              name: '',
              guid: null,
              active: false,
              description: null,
              workTypes: [],
              tags: [],
              userId: '',
              email: '',
              loginIds: [],
              savedSearches: [],
              userGroups: [],
              artifactId: '',
              idString: '',
              idIntValue: 0,
              uuid:0
            },
            actionableItem: {
              id: '',
              name: '',
            },
            targetedVersion: '',
            title: '',
            description:''
          } 
        },
        {
          provide: PlConfigActionService, useValue: {
            ARB: of([{
              id: "123",
              name:"First ARB"
            },
              {
                id: "456",              
                name: "Second ARB"
            }
            ]),
            users: of([{
              id: "123",
              name: "user1",
              guid: null,
              active: true,
              description: null,
              workTypes: [],
              tags: [],
              userId: "123",
              email: "user1@user1domain.com",
              loginIds: ["123"],
              savedSearches: [],
              userGroups: [],
              artifactId: "",
              idString: "123",
              idIntValue: 123,
              uuid:123
            }]
            )
        }}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateActionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
