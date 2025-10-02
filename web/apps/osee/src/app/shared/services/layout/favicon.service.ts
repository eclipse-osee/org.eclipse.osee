// favicon.service.ts
import { Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Injectable({
	providedIn: 'root',
})
export class FaviconService {
	private defaultFavicon = 'assets/default-favicon.ico';

	constructor(private titleService: Title) {}

	setFavicon(faviconUrl: string) {
		const link: HTMLLinkElement =
			document.querySelector("link[rel*='icon']") ||
			document.createElement('link');
		link.type = 'image/x-icon';
		link.rel = 'icon';
		link.href = faviconUrl;
		document.getElementsByTagName('head')[0].appendChild(link);
	}

	resetFavicon() {
		this.setFavicon(this.defaultFavicon);
	}
}
