import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { of } from 'rxjs';
import { PlConfigActionService } from '../../services/pl-config-action.service';

import { TransitionActionToReviewDialogComponent } from './transition-action-to-review-dialog.component';

describe('TransitionActionToReviewDialogComponent', () => {
  let component: TransitionActionToReviewDialogComponent;
  let fixture: ComponentFixture<TransitionActionToReviewDialogComponent>;

  beforeEach(async () => {
    var testUsers = [
      {
        id: '123',
        name: 'user1',
        guid: null,
        active: true,
        description: null,
        workTypes: [],
        tags: [],
        userId: '123',
        email: "user1@user1domain.com",
        loginIds: ['123'],
        savedSearches: [],
        userGroups: [],
        artifactId: "",
        idString: "123",
        idIntValue: 123,
        uuid:123
      },
      {
        id: '456',
        name: 'user2',
        guid: null,
        active: true,
        description: null,
        workTypes: [],
        tags: [],
        userId: '456',
        email: "user2@user2domain.com",
        loginIds: ['456'],
        savedSearches: [],
        userGroups: [],
        artifactId: "",
        idString: "456",
        idIntValue: 456,
        uuid:456
      },
    ]
    await TestBed.configureTestingModule({
      declarations: [TransitionActionToReviewDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            actions: [
              {
                id: 123,
                Name: "123",
                AtsId: "195",
                ActionAtsId: "195",
                TeamWfAtsId: "TW195",
                ArtifactType: "string",
                actionLocation: "somewhere",
              },
              {
                id: 456,
                Name: "456",
                AtsId: "196",
                ActionAtsId: "196",
                TeamWfAtsId: "TW196",
                ArtifactType: "string",
                actionLocation: "somewhere",
              }
            ],
            selectedUser: {
              id: '123',
              name: 'user1',
              guid: null,
              active: true,
              description: null,
              workTypes: [],
              tags: [],
              userId: '123',
              email: "user1@user1domain.com",
              loginIds: ['123'],
              savedSearches: [],
              userGroups: [],
              artifactId: "",
              idString: "123",
              idIntValue: 123,
              uuid:123
            }
          }
        },
        {
          provide: PlConfigActionService, useValue: {
          users: of(testUsers)
        }}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransitionActionToReviewDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
