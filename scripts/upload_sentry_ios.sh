#!/usr/bin/env sh

# Install Sentry CLI: curl -sL https://sentry.io/get-cli/ | sh

sentry-cli debug-files upload --auth-token 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 \
  --include-sources \
  --org fila-magenta \
  --project apple-ios \
  build/ios/Debug-iphonesimulator/
