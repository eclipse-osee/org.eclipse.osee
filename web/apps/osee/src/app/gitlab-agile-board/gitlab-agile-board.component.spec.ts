import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GitlabAgileBoardComponent } from './gitlab-agile-board.component';

describe('GitlabAgileBoardComponent', () => {
  let component: GitlabAgileBoardComponent;
  let fixture: ComponentFixture<GitlabAgileBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GitlabAgileBoardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GitlabAgileBoardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
