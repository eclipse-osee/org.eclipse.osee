// SpotBugs PR Analysis Script
// Runs scoped SpotBugs analysis on changed files and posts results to the PR.
// Usage: node .github/scripts/spotbugs-analysis.js
// Requires environment: GITHUB_TOKEN, GITHUB_REPOSITORY, GITHUB_RUN_ID,
//   GITHUB_SERVER_URL, GITHUB_EVENT_PATH (all provided by GitHub Actions)

const fs = require('fs');
const https = require('https');
const { execSync } = require('child_process');

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function countBugs(file) {
  if (!fs.existsSync(file)) return -1;
  const m = fs.readFileSync(file, 'utf8').match(/total_bugs="(\d+)"/);
  return m ? parseInt(m[1], 10) : 0;
}

function parseBugs(file) {
  if (!fs.existsSync(file)) return [];
  const xml = fs.readFileSync(file, 'utf8');
  const bugs = [];
  const re = /<BugInstance[\s\S]*?<\/BugInstance>/g;
  let m;
  while ((m = re.exec(xml)) !== null) {
    const b = m[0];
    const tag = (t) => {
      const x = b.match(new RegExp(`<${t}>([^<]*)</${t}>`));
      return x ? x[1] : '';
    };
    const opening = b.substring(0, b.indexOf('>') + 1);
    const sl = b.match(/<SourceLine[^>]*>/)?.[0] || '';
    bugs.push({
      type: opening.match(/type="([^"]*)"/)?.[1] || '',
      priority: opening.match(/priority="([^"]*)"/)?.[1] || '',
      category: opening.match(/category="([^"]*)"/)?.[1] || '',
      sourcepath: sl.match(/sourcepath="([^"]*)"/)?.[1] || '',
      methodName: (b.match(/<Method[^>]*name="([^"]*)"/) || [])[1] || '',
      message: tag('ShortMessage'),
      longMsg: tag('LongMessage'),
    });
  }
  return bugs;
}

const PRIORITY_LABELS = {
  '1': ':red_circle: High',
  '2': ':orange_circle: Medium',
  '3': ':yellow_circle: Low',
};

// ---------------------------------------------------------------------------
// GitHub API helpers (using built-in https module)
// ---------------------------------------------------------------------------

const token = process.env.GITHUB_TOKEN;
const [owner, repo] = process.env.GITHUB_REPOSITORY.split('/');
const runId = process.env.GITHUB_RUN_ID;
const serverUrl = process.env.GITHUB_SERVER_URL || 'https://github.com';
const eventPath = process.env.GITHUB_EVENT_PATH;
const spotbugsBin =
  process.env.SPOTBUGS_BIN || '/root/spotbugs/current/bin/spotbugs';

const event = JSON.parse(fs.readFileSync(eventPath, 'utf8'));
const pullNumber = event.pull_request?.number;
const headSha = event.pull_request?.head?.sha;

if (!pullNumber) {
  console.log('Not a pull request event — skipping.');
  process.exit(0);
}

/** Low-level HTTPS request using Node built-in module. */
function request(method, url, body) {
  return new Promise((resolve, reject) => {
    const parsed = new URL(url);
    const opts = {
      hostname: parsed.hostname,
      path: parsed.pathname + parsed.search,
      method,
      headers: {
        Authorization: `token ${token}`,
        Accept: 'application/vnd.github+json',
        'X-GitHub-Api-Version': '2022-11-28',
        'User-Agent': 'spotbugs-analysis-script',
      },
    };
    if (body) {
      opts.headers['Content-Type'] = 'application/json';
    }
    const req = https.request(opts, (res) => {
      const chunks = [];
      res.on('data', (chunk) => chunks.push(chunk));
      res.on('end', () => {
        const text = Buffer.concat(chunks).toString();
        if (res.statusCode >= 400) {
          reject(
            new Error(`${method} ${url} → ${res.statusCode}: ${text}`)
          );
        } else {
          resolve(JSON.parse(text));
        }
      });
    });
    req.on('error', reject);
    if (body) req.write(JSON.stringify(body));
    req.end();
  });
}

function ghGet(url) {
  return request('GET', url);
}
function ghPost(url, body) {
  return request('POST', url, body);
}
function ghPatch(url, body) {
  return request('PATCH', url, body);
}

/** Paginate through all changed files in the PR. */
async function getChangedFiles() {
  const files = [];
  let page = 1;
  while (true) {
    const batch = await ghGet(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/files?per_page=100&page=${page}`
    );
    files.push(...batch);
    if (batch.length < 100) break;
    page++;
  }
  return files;
}

// ---------------------------------------------------------------------------
// Main
// ---------------------------------------------------------------------------

async function main() {
  // --- Get changed files and build class list ---
  const changedFiles = await getChangedFiles();

  const classes = new Set();
  const srcToRepo = {};
  for (const f of changedFiles) {
    if (!f.filename.endsWith('.java')) continue;
    for (const marker of ['/src/', '/src-gen/']) {
      const idx = f.filename.indexOf(marker);
      if (idx !== -1) {
        const rel = f.filename.substring(idx + marker.length);
        classes.add(rel.replace(/\//g, '.').replace(/\.java$/, ''));
        srcToRepo[rel] = f.filename;
        break;
      }
    }
  }

  // --- Run scoped SpotBugs on changed classes ---
  let changedBugs = [];
  if (classes.size > 0) {
    const analyzeList = [...classes].join(',');
    console.log(`Running SpotBugs on ${classes.size} changed class(es)`);
    try {
      execSync(
        `${spotbugsBin} -textui ` +
          `-xml:withMessages -output spotbugs-report-changed.xml ` +
          `-auxclasspathFromInput -onlyAnalyze "${analyzeList}" ` +
          `-nested:false compiled-classes/`,
        { stdio: 'inherit' }
      );
      execSync(
        `${spotbugsBin} -textui ` +
          `-html:fancy-hist.xsl -output spotbugs-report-changed.html ` +
          `-auxclasspathFromInput -onlyAnalyze "${analyzeList}" ` +
          `-nested:false compiled-classes/`,
        { stdio: 'inherit' }
      );
    } catch (e) {
      console.warn(`Scoped SpotBugs exited with code ${e.status}`);
    }
    changedBugs = parseBugs('spotbugs-report-changed.xml');
  }

  const fullCount = countBugs('spotbugs-report-full.xml');

  // --- Artifact download URL ---
  const runUrl = `${serverUrl}/${owner}/${repo}/actions/runs/${runId}`;
  let artifactUrl = `${runUrl}#artifacts`;
  try {
    const data = await ghGet(
      `https://api.github.com/repos/${owner}/${repo}/actions/runs/${runId}/artifacts`
    );
    const rpt = data.artifacts.find((a) => a.name === 'spotbugs-report');
    if (rpt) artifactUrl = `${runUrl}/artifacts/${rpt.id}`;
  } catch (e) {
    console.warn(`Could not fetch artifact URL: ${e.message}`);
  }

  // =============================================
  // 1. PR COMMENT (in the conversation thread)
  // =============================================
  const marker = '<!-- spotbugs-analysis-comment -->';
  const lines = [marker, `## :beetle: SpotBugs Analysis`, ``];

  if (changedBugs.length > 0) {
    lines.push(
      `### :pushpin: Issues in Changed Files — ${changedBugs.length} issue(s)`
    );
    lines.push(``);
    lines.push(`| File | Method | Priority | Message |`);
    lines.push(`|------|--------|----------|---------|`);
    for (const bug of changedBugs) {
      const file = bug.sourcepath
        ? `\`${bug.sourcepath.split('/').pop()}\``
        : '';
      const method = bug.methodName ? `\`${bug.methodName}()\`` : '';
      const msg = bug.message.replace(/\|/g, '\\|');
      lines.push(
        `| ${file} | ${method} | ${PRIORITY_LABELS[bug.priority] || bug.priority} | ${msg} |`
      );
    }
    lines.push(``);
  } else {
    lines.push(`### :pushpin: Changed Files`);
    lines.push(
      `> :white_check_mark: No issues in files changed by this PR.`
    );
    lines.push(``);
  }

  if (fullCount > 0) {
    lines.push(`### :file_folder: Full Analysis`);
    lines.push(
      `> :warning: **${fullCount}** total issue(s) across all \`org.eclipse.osee\` packages.`
    );
  } else if (fullCount === 0) {
    lines.push(`### :file_folder: Full Analysis`);
    lines.push(`> :white_check_mark: No issues found.`);
  } else {
    lines.push(`> :x: SpotBugs report was not generated.`);
  }

  lines.push(``);
  lines.push(
    `[:arrow_down: Download the SpotBugs HTML reports](${artifactUrl})`
  );

  const prBody = lines.join('\n');

  // Upsert the PR comment
  const commentsData = await ghGet(
    `https://api.github.com/repos/${owner}/${repo}/issues/${pullNumber}/comments`
  );
  const existing = commentsData.find((c) => c.body.includes(marker));
  if (existing) {
    await ghPatch(
      `https://api.github.com/repos/${owner}/${repo}/issues/comments/${existing.id}`,
      { body: prBody }
    );
  } else {
    await ghPost(
      `https://api.github.com/repos/${owner}/${repo}/issues/${pullNumber}/comments`,
      { body: prBody }
    );
  }

  // =============================================
  // 2. FILE-LEVEL REVIEW COMMENTS (on each file)
  // =============================================
  if (changedBugs.length === 0) return;

  const bugsByFile = {};
  for (const bug of changedBugs) {
    const repoPath = srcToRepo[bug.sourcepath];
    if (!repoPath) continue;
    if (!bugsByFile[repoPath]) bugsByFile[repoPath] = [];
    bugsByFile[repoPath].push(bug);
  }

  const reviewComments = [];
  for (const [filePath, bugs] of Object.entries(bugsByFile)) {
    const fileName = filePath.split('/').pop();
    const body = [];
    body.push(
      `## :beetle: SpotBugs — ${bugs.length} issue${bugs.length !== 1 ? 's' : ''} in \`${fileName}\``
    );
    body.push(``);

    for (const bug of bugs) {
      const docsUrl = `https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#${bug.type.toLowerCase()}`;
      const method = bug.methodName
        ? `\`${bug.methodName}()\``
        : '_unknown_';
      const desc =
        bug.longMsg && bug.longMsg !== bug.message
          ? bug.longMsg
          : bug.message;

      body.push(`**${desc || bug.type}**`);
      body.push(``);
      body.push(
        `${PRIORITY_LABELS[bug.priority] || bug.priority} &nbsp;&nbsp; **Method:** ${method} &nbsp;&nbsp; **Type:** [\`${bug.type}\`](${docsUrl})`
      );
      body.push(``);
    }

    reviewComments.push({
      path: filePath,
      body: body.join('\n'),
      subject_type: 'file',
    });
  }

  if (reviewComments.length === 0) return;
  console.log(
    `Posting file-level comments on ${reviewComments.length} file(s)`
  );

  try {
    await ghPost(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/reviews`,
      {
        commit_id: headSha,
        event: 'COMMENT',
        body: ':beetle: **SpotBugs** found issues in changed files.',
        comments: reviewComments,
      }
    );
  } catch (e) {
    console.warn(`Batch review failed: ${e.message}`);
    for (const c of reviewComments) {
      try {
        await ghPost(
          `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/comments`,
          {
            commit_id: headSha,
            path: c.path,
            body: c.body,
            subject_type: 'file',
          }
        );
      } catch (err) {
        console.warn(`Could not comment on ${c.path}: ${err.message}`);
      }
    }
  }
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});