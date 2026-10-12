import { Routes } from "@angular/router";

const routes: Routes = [
    {
		path: '',
		loadChildren: () => import('@osee/toolbar'),
		outlet: 'toolbar',
		pathMatch: 'full',
	},
    { path: '', loadComponent: () => import('./gitlab-agile-board.component') }
]

export default routes;