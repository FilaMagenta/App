# Filà Magenta App
The official app for Android and iOS.

[![CodeFactor](https://www.codefactor.io/repository/github/filamagenta/app/badge)](https://www.codefactor.io/repository/github/filamagenta/app)
[![Crowdin](https://badges.crowdin.net/fila-magenta-app/localized.svg)](https://crowdin.com/project/fila-magenta-app)
[![Publish Internal Release](https://github.com/FilaMagenta/App/actions/workflows/internal-release.yml/badge.svg)](https://github.com/FilaMagenta/App/actions/workflows/internal-release.yml)

<div style="text-align: center; width: 100%">
<img src="https://raw.githubusercontent.com/Volorf/Badges/master/Google%20Play/Google%20Play%20Badge.png" alt="Google Play Badge" style="width: 150px" />
<img src="https://raw.githubusercontent.com/Volorf/Badges/master/App%20Store/App%20Store%20Badge.png" alt="App Store Badge" style="width: 150px" />
</div>

## Performance considerations
Since WooCommerce is not the fastest thing in the world, there are some optimizations to be considered.
1. Not all the events are fetched when synchronizing.
   Only the ones in the current working year will be considered.
   The working year is taken from August to August, so let's say it's currently 13th of October 2023, only events
   created after the 1st of August 2023 will be loaded.
   And if it's currently 23rd of April 2024, events will still be loaded from the 1st of August 2023.
2. When loading events, only the ones in the "EVENTOS" category will be loaded.
   This is hardcoded in [WooCommerce.kt](/shared/src/commonMain/kotlin/com/arnyminerz/filamagenta/network/woo/WooCommerce.kt)
   as `CATEGORY_ID_EVENTS`.
   If the ID is modified, it must be updated, and the app distributed again.

# Release Process
To build production release, set the `buildkonfig.flavor` property to `production`:
```shell
gradlew build -Pbuildkonfig.flavor=production
```

# Translation
The project is available on [Crowdin](https://crowdin.com/project/fila-magenta-app) for whoever that wants to translate
the app into their own language.
