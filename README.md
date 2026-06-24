# Unifycode Java

Unifycode is a Gradle code-quality plugin for Java.

The plugin applies a preconfigured set of Java quality tools with strict
defaults. Its goal is to help keep Java code quality high and coding standards
uniform across different projects.

The plugin currently configures:

- [PMD](https://pmd.github.io/) for static code analysis.
- [Checkstyle](https://checkstyle.org/) for code style checks.
- [Spotless](https://github.com/diffplug/spotless) for formatting.

## Motivation

**Why does this plugin exist?**

1. I wanted to configure my own code-quality standards.

   I started using Java quality tools like PMD, Checkstyle, and Spotless across
   several Java projects. They gave me a good foundation for defining my own
   coding standards.
2. I needed one source of truth for every project.

   Over time, I realized how messy it was to maintain the same configs in
   several projects. I decided to make a plugin that combines the basic rules
   from those quality tools and can still be configured for project-specific
   needs.
3. I wanted to write custom rules.

   A dedicated plugin creates room for custom rules. Fortunately, PMD and
   Checkstyle both provide APIs for writing custom linter rules.

This plugin was inspired by [Qulice](https://www.qulice.com/).

## Usage

Apply the `org.unifycode` Gradle plugin to a Java project.

### Gradle Setup

```gradle
// @todo #2:20min Replace the JitPack setup guide with the release setup guide
// once Unifycode is published to its long-term plugin repository.
```

Add JitPack plugin resolution to `settings.gradle`:

```gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = 'https://jitpack.io' }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == 'org.unifycode') {
                useModule("com.github.c71n93.unifycode-java:unifycode-gradle-plugin:${requested.version}")
            }
        }
    }
}
```

Then apply the plugin in `build.gradle`:

```gradle
plugins {
    id 'java'
    id 'org.unifycode' version '<commit-or-release>'
}
```

### Configuration

Unifycode is strict by default. Checkstyle and PMD violations fail the build
unless their policy is configured as non-strict:

```gradle
unifycode {
    checkstyle {
        strict = false
    }
    pmd {
        strict = false
    }
}
```

Spotless formatting is configured by the plugin. Formatting changes remain
explicit through formatting tasks.

### Tasks

The plugin integrates with Gradle's normal quality lifecycle:

- `unifycodeCheck` runs the aggregated Unifycode quality gate.
- `unifycodeFormat` applies configured formatting.
- `check` depends on `unifycodeCheck`.

Tool-specific Gradle tasks such as `checkstyleMain`, `pmdMain`,
`spotlessCheck`, and `spotlessApply` remain available.

## Project Structure

This repository is a Gradle multi-project build:

- `unifycode-core/checkstyle`
  - Gradle path: `:unifycode-checkstyle`
  - reusable Checkstyle configuration assets
- `unifycode-core/pmd`
  - Gradle path: `:unifycode-pmd`
  - reusable PMD ruleset assets
- `unifycode-core/spotless`
  - Gradle path: `:unifycode-spotless`
  - reusable Spotless formatter assets
- `unifycode-gradle-plugin`
  - Gradle path: `:unifycode-gradle-plugin`
  - Gradle plugin implementation, extension types, lifecycle task wiring,
    resource copying, and functional tests

Reusable tool configuration belongs in the tool-specific modules under
`unifycode-core`. Gradle-specific behavior belongs in `unifycode-gradle-plugin`.
