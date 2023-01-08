# skuld-android
Android frontend for the Skuld calendar and note-taking application

## Build system
This project uses gradle, a list of all runnable gradle tasks can be viewed by running:
```
./gradlew tasks
```

## Development environent
We recommend using IntelliJ Idea as we can generate formatting and linting configurations for it using ktlint.
```
./gradlew ktLintGenerateIdeaConfig
```

It is also recommended to regularly run formatting commands before committing as the CI job will fail.

A gradle task is available to generate pre-commit git hooks to either check for fix formatting on staged files.
```
./gradlew ktLintGenerateFormatPreCommitHook
```

## Project structure

### UI package

 - screens: every file contains a separate screen
 - theme: colors, fonts, shapes and a theme definition
 - utils: contains utility classes
 - App.kt: main entry point; contains navigation and login ui dispatch
