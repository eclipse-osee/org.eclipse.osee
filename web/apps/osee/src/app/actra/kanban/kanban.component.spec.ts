import { provideHttpClient } from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';
import { apiURL } from '@osee/environments';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { KanbanComponent } from './kanban.component';

describe('KanbanComponent', () => {
	let component: KanbanComponent;
	let fixture: ComponentFixture<KanbanComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [KanbanComponent, NoopAnimationsModule],
			providers: [provideHttpClient(), provideHttpClientTesting()],
		}).compileComponents();

		fixture = TestBed.createComponent(KanbanComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		TestBed.tick();
	});

	it('shows columns from the selected work definition', async () => {
		expect(component).toBeTruthy();
		const http = TestBed.inject(HttpTestingController);
		http.expectOne(apiURL + '/ats/config').flush({
			topTeamDefinition: '1',
			idToTeamDef: { '100': { name: 'SAW SW', parentId: '1' } },
		});
		await fixture.whenStable();
		fixture.debugElement
			.query(By.css('#team-definition-filter'))
			.triggerEventHandler('selectionChange', { value: '100' });
		TestBed.tick();
		http.expectOne(apiURL + '/ats/workdef/teamdef/100').flush({
			states: [{ name: 'Open' }, { name: 'Complete' }],
		});
		http.expectOne(apiURL + '/ats/world/search').flush({
			results: [
				{
					id: '500',
					values: ['Requirements update', 'Open', 'SAW SW', 'Taylor'],
				},
			],
		});
		await fixture.whenStable();

		const headings = Array.from<HTMLElement>(
			fixture.nativeElement.querySelectorAll('h2')
		);
		expect(headings.map((heading) => heading.textContent.trim())).toEqual([
			'Open (1)',
			'Complete (0)',
		]);
		http.verify();
	});
});
