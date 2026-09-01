---
summary: "Local web development setup: serving, auth, and proxy configuration"
tags: [web, development, auth, serve, proxy, demo]
fileMatch: "**/environments/**,**/proxy.conf*"
---

# Local Web Development

## Serving the App

From `web/apps/osee/`:

```bash
npx ng serve --configuration demo_local_debug
```

The app runs at `http://localhost:4200`.

## Authentication (Demo Mode)

The `demo_local_debug` configuration uses demo auth which reads the user identity from browser localStorage.

In DevTools → Application → Local Storage → `http://localhost:4200`, add:

| Key | Value |
|---|---|
| `osee.account.id` | Your OSEE user account ID |

Refresh the page after setting it.

## Proxy Configuration

The proxy config at `src/environments/proxy.conf.json` routes API calls to the backend server. The default port is `8089`. If your OSEE server runs on a different port, update the `target` values in the proxy config to match.
