# compose-agent-render

A Kotlin Compose Multiplatform library for rendering AI agent and LLM streaming output.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.0xzhangke/agent-render)](https://central.sonatype.com/artifact/io.github.0xzhangke/agent-render)

Compose Agent Render provides a flexible UI component for displaying structured streaming events such as:

* Assistant messages
* Reasoning process
* Tool calls
* Tool results
* Errors
* Custom agent events

It is designed to work with modern AI frameworks and supports incremental rendering as new events arrive, making it suitable for chat applications and agent-based user interfaces.

## Features

* 🚀 Compose Multiplatform
* 💬 Streaming-first rendering
* 🧩 Extensible event model
* 🔧 Customizable UI components
* 🤖 Designed for AI agents and LLMs

## Status

This project is under active development, and APIs may change before the first stable release.



## Dependency

```kts
implementation("io.github.0xzhangke:agent-render:${LAST_VERSION}")
// Optional, for Koog support
implementation("io.github.0xzhangke:agent-render-koog:${LAST_VERSION}")
```
