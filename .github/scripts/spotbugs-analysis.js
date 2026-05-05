// SpotBugs PR Analysis Script
// Runs scoped SpotBugs analysis on changed files and posts results to the PR.
// Usage: node .github/scripts/spotbugs-analysis.js
// Requires environment: GITHUB_TOKEN, GITHUB_REPOSITORY, GITHUB_RUN_ID,
//   GITHUB_SERVER_URL, GITHUB_EVENT_PATH (all provided by GitHub Actions)

const fs = require('fs');
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

  // Use split-based parsing instead of a single regex over the entire file.
  // The [\s\S]*? regex fails on multi-MB XML due to backtracking limits.
  const chunks = xml.split('<BugInstance ');
  for (let i = 1; i < chunks.length; i++) {
    const endIdx = chunks[i].indexOf('</BugInstance>');
    if (endIdx === -1) continue;
    const b = '<BugInstance ' + chunks[i].substring(0, endIdx + '</BugInstance>'.length);

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
// GitHub API helpers (using curl for reliable networking in Alpine containers)
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

function ghGet(url) {
  const out = execSync(
    `curl -fsSL ` +
      `-H "Authorization: token ${token}" ` +
      `-H "Accept: application/vnd.github+json" ` +
      `-H "X-GitHub-Api-Version: 2022-11-28" ` +
      `"${url}"`,
    { encoding: 'utf8', maxBuffer: 50 * 1024 * 1024 }
  );
  return JSON.parse(out);
}

function ghPost(url, body) {
  const tmp = '/tmp/_gh_post_body.json';
  fs.writeFileSync(tmp, JSON.stringify(body));
  const out = execSync(
    `curl -fsSL -X POST ` +
      `-H "Authorization: token ${token}" ` +
      `-H "Accept: application/vnd.github+json" ` +
      `-H "X-GitHub-Api-Version: 2022-11-28" ` +
      `-H "Content-Type: application/json" ` +
      `-d @${tmp} ` +
      `"${url}"`,
    { encoding: 'utf8', maxBuffer: 50 * 1024 * 1024 }
  );
  return JSON.parse(out);
}

function ghPatch(url, body) {
  const tmp = '/tmp/_gh_patch_body.json';
  fs.writeFileSync(tmp, JSON.stringify(body));
  const out = execSync(
    `curl -fsSL -X PATCH ` +
      `-H "Authorization: token ${token}" ` +
      `-H "Accept: application/vnd.github+json" ` +
      `-H "X-GitHub-Api-Version: 2022-11-28" ` +
      `-H "Content-Type: application/json" ` +
      `-d @${tmp} ` +
      `"${url}"`,
    { encoding: 'utf8', maxBuffer: 50 * 1024 * 1024 }
  );
  return JSON.parse(out);
}

/** Paginate through all changed files in the PR. */
function getChangedFiles() {
  const files = [];
  let page = 1;
  while (true) {
    const batch = ghGet(
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

function main() {
  // --- Run full SpotBugs analysis ---
  console.log('Running SpotBugs full analysis on org.eclipse.osee.-');
  try {
    execSync(
      `${spotbugsBin} -textui -low -effort:max ` +
        `-xml:withMessages -output spotbugs-report-full.xml ` +
        `-auxclasspathFromInput -onlyAnalyze "org.eclipse.osee.-" ` +
        `-nested:false compiled-classes/`,
      { stdio: 'inherit' }
    );
  } catch (e) {
    console.warn(`Full SpotBugs (xml) exited with code ${e.status}`);
  }
  try {
    execSync(
      `${spotbugsBin} -textui -low -effort:max ` +
        `-html:fancy-hist.xsl -output spotbugs-report-full.html ` +
        `-auxclasspathFromInput -onlyAnalyze "org.eclipse.osee.-" ` +
        `-nested:false compiled-classes/`,
      { stdio: 'inherit' }
    );
  } catch (e) {
    console.warn(`Full SpotBugs (html) exited with code ${e.status}`);
  }

  // --- Get changed files and build class list ---
  const changedFiles = getChangedFiles();

  const classes = new Set();
  const packages = new Set();
  const srcToRepo = {};
  for (const f of changedFiles) {
    if (!f.filename.endsWith('.java')) continue;
    for (const marker of ['/src/', '/src-gen/']) {
      const idx = f.filename.indexOf(marker);
      if (idx !== -1) {
        const rel = f.filename.substring(idx + marker.length);
        const fqcn = rel.replace(/\//g, '.').replace(/\.java$/, '');
        classes.add(fqcn);
        // Use package-level pattern (with trailing .-) for -onlyAnalyze
        const pkg = fqcn.substring(0, fqcn.lastIndexOf('.'));
        if (pkg) packages.add(pkg + '.-');
        srcToRepo[rel] = f.filename;
        break;
      }
    }
  }

  // --- Run scoped SpotBugs on changed classes ---
  let changedBugs = [];
  if (classes.size > 0) {
    const analyzeList = [...packages].join(',');
    console.log(`Running SpotBugs on ${classes.size} changed class(es) in ${packages.size} package(s)`);
    console.log(`Packages: ${analyzeList}`);
    console.log(`srcToRepo mappings:`);
    for (const [rel, repo] of Object.entries(srcToRepo)) {
      console.log(`  ${rel} → ${repo}`);
    }
    const cmd =
      `${spotbugsBin} -textui -low -effort:max ` +
      `-xml:withMessages -output spotbugs-report-changed.xml ` +
      `-auxclasspathFromInput -onlyAnalyze "${analyzeList}" ` +
      `-nested:false compiled-classes/`;
    console.log(`SpotBugs command: ${cmd}`);
    try {
      execSync(cmd, { stdio: 'inherit' });
    } catch (e) {
      console.warn(`Scoped SpotBugs (xml) exited with code ${e.status}`);
    }
    try {
      execSync(
        `${spotbugsBin} -textui -low -effort:max ` +
          `-html:fancy-hist.xsl -output spotbugs-report-changed.html ` +
          `-auxclasspathFromInput -onlyAnalyze "${analyzeList}" ` +
          `-nested:false compiled-classes/`,
        { stdio: 'inherit' }
      );
    } catch (e) {
      console.warn(`Scoped SpotBugs (html) exited with code ${e.status}`);
    }
    if (fs.existsSync('spotbugs-report-changed.xml')) {
      const raw = fs.readFileSync('spotbugs-report-changed.xml', 'utf8');
      console.log(`Changed report size: ${raw.length} bytes`);
      console.log(`First 500 chars: ${raw.substring(0, 500)}`);
    } else {
      console.warn('spotbugs-report-changed.xml was NOT created');
    }
    changedBugs = parseBugs('spotbugs-report-changed.xml');
    console.log(`Parsed ${changedBugs.length} bug(s) from changed report`);
  } else {
    console.log('No changed Java classes found — skipping scoped analysis');
    console.log(`Total changed files from API: ${changedFiles.length}`);
    const javaFiles = changedFiles.filter((f) => f.filename.endsWith('.java'));
    console.log(`Java files in PR: ${javaFiles.map((f) => f.filename).join(', ')}`);
  }

  const fullCount = countBugs('spotbugs-report-full.xml');

  // --- Artifact download URL (uses nightly.link for direct download without auth) ---
  const runUrl = `${serverUrl}/${owner}/${repo}/actions/runs/${runId}`;
  const artifactUrl = `https://nightly.link/${owner}/${repo}/actions/runs/${runId}/spotbugs-report.zip`;

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
  const commentsData = ghGet(
    `https://api.github.com/repos/${owner}/${repo}/issues/${pullNumber}/comments`
  );
  const existing = commentsData.find((c) => c.body.includes(marker));
  if (existing) {
    ghPatch(
      `https://api.github.com/repos/${owner}/${repo}/issues/comments/${existing.id}`,
      { body: prBody }
    );
  } else {
    ghPost(
      `https://api.github.com/repos/${owner}/${repo}/issues/${pullNumber}/comments`,
      { body: prBody }
    );
  }

  // =============================================
  // 2. FILE-LEVEL REVIEW COMMENTS (on each file)
  // =============================================
  if (changedBugs.length === 0) return;

  // Only post review comments on files that are actually in the PR diff.
  // SpotBugs analyzes entire packages, so it may report bugs in files not
  // changed by this PR — GitHub rejects review comments on those (422).
  const changedPaths = new Set(changedFiles.map((f) => f.filename));

  const bugsByFile = {};
  for (const bug of changedBugs) {
    const repoPath = srcToRepo[bug.sourcepath];
    if (!repoPath) continue;
    if (!changedPaths.has(repoPath)) continue;
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

  // Fetch the current PR head SHA to avoid 422 if the PR was updated after
  // the workflow was triggered (stale event payload).
  let commitId = headSha;
  try {
    const prData = ghGet(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}`
    );
    commitId = prData.head.sha;
  } catch (e) {
    console.warn(`Could not fetch current PR head SHA, using event payload: ${e.message}`);
  }

  try {
    ghPost(
      `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/reviews`,
      {
        commit_id: commitId,
        event: 'COMMENT',
        body: ':beetle: **SpotBugs** found issues in changed files.',
        comments: reviewComments,
      }
    );
  } catch (e) {
    console.warn(`Batch review failed: ${e.message}`);
    for (const c of reviewComments) {
      try {
        ghPost(
          `https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/comments`,
          {
            commit_id: commitId,
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

main();