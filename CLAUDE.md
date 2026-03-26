# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Scala 2.13 microservice that fetches page titles from URLs. Built with Apache Pekko HTTP + Pekko Streams. POST a JSON array of URLs to `localhost:8080`, get back titles or structured errors. Handles deduplication, redirects (returned as errors, not followed), and invalid inputs.

## Build Commands

- `sbt run` — run the server on localhost:8080
- `sbt compile` — compile
- `sbt styleFix` — auto-fix formatting (scalafmt) and linting (scalafix)
- `sbt styleCheck` — check formatting and linting without fixing (CI uses this)
- `sbt dev` — switch to dev mode (warnings don't fail compilation)
- `sbt ci` — switch to CI mode (strict compiler warnings)

CI runs: `sbt ci compile styleCheck`

## Architecture

Three files in `src/main/scala/crawler/`:

- **CrawlerServer** — Entry point. Creates ActorSystem, binds HTTP to port 8080, wires routes.
- **CrawlerRoutes** — Pekko HTTP route definitions. GET returns help text. POST accepts JSON array of URL strings, asks CrawlerActor, returns JSON response. 60s timeout.
- **CrawlerActor** — Core logic. Receives `ProcessList(urlList)`, deduplicates URLs, processes them concurrently (100 parallel via Pekko Streams), extracts `<title>` tags via regex, and returns results in original order. Handles bad URLs, inaccessible hosts, HTTP errors, redirects, and missing titles.

## Code Style Enforcement

- **scalafix** (`.scalafix.conf`): Enforces pure functional style — no vars, throws, nulls, casts, returns, while loops, or universal equality. Import organization is also enforced.
- **scalafmt** (`.scalafmt.conf`): Max 120 columns, Scala 2.13 dialect.

Always run `sbt styleFix` before committing to ensure compliance.

## Dependencies

Versions are centralized in `project/Versions.scala`. Core stack is Apache Pekko HTTP + Pekko Streams + spray-json.
