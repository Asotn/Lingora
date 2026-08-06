# Lingora

Lingora is an Android app for looking up a word or short phrase and seeing
every common way to say it in the language you're learning — each
translation in its own card, with real usage context and a button to hear
it spoken aloud.

## How it works

1. **Onboarding.** On first launch, Lingora asks why you're learning a
   language (travel, career, exams, and so on) and what level you're
   starting from. Both answers are stored on the device and can be redone
   later from Settings.
2. **Home screen.** Two language boxes sit at the top of the screen: the
   language you speak, and the language you're learning. Both are
   required — translation always runs against whichever pair is
   currently selected, and changing either box after a search
   re-translates automatically.
3. **Word lookup.** Type a word or short phrase (up to 50 characters)
   and translate it. Every distinct translation the backend returns comes
   back in its own card, labeled "Common usage" or "Less common" based on
   real usage data, with an example sentence where the backend has one.
4. **Pronunciation.** Each card has a speaker icon. Tapping it speaks the
   translated word — never the original — using your device's own
   text-to-speech engine, in the language it was translated into. If that
   language has no voice installed, Lingora offers a direct shortcut to
   your device's voice settings instead of failing silently.

## Design

Lingora's visual identity is built on two ideas: an animated aurora
backdrop (soft, drifting fields of color behind everything) and
glassmorphism (translucent, gently bordered "panes of glass" for every
card, field, and sheet). The whole app runs a single dark theme, since
the glow effect is designed for a dark canvas rather than a light one.
There are no emoji anywhere in the app — icons come from Material Symbols
instead.

## Tech stack

- **Kotlin** + **Jetpack Compose** (Material 3) for the entire UI
- **Navigation Compose** for the onboarding -> home -> settings flow
- **Retrofit** + **OkHttp** + **kotlinx.serialization** for networking
- **DataStore Preferences** for the small set of local settings
  (onboarding state, learning goal, last-used languages, speech rate)
- **Android's built-in `TextToSpeech`** for pronunciation — Lingora never
  bundles or downloads its own voices
- A small hand-written dependency container (`di/AppContainer.kt`)
  instead of a DI framework, since the dependency graph is short

## Translation backend

Lingora ships wired up to the [MyMemory Translation
API](https://mymemory.translated.net/doc/spec.php), a free service that
needs no API key and covers well over 50 languages. Its anonymous quota
is limited (roughly a few thousand words per day per IP address), which
is enough to use and demo the app but not to run it at scale.

Every network call goes through one interface, `MyMemoryApiService`, and
one repository, `TranslationRepository`. To raise the quota (MyMemory
supports an email parameter for a higher limit) or to switch to a paid
provider such as Google Cloud Translation, DeepL, or Azure Translator,
`data/remote/NetworkModule.kt` is the only file that needs to change.

## Languages

The full catalog lives in `data/model/SupportedLanguages.kt` — currently
73 languages, each with its translation code and the `Locale` used to
request its voice from the text-to-speech engine (the two are kept
separate because a few languages use different tags for each, such as
Filipino). Adding a language is a single line in that file.

Voice availability depends on what's installed on the learner's device,
not on Lingora itself. Settings and the home screen's "voice missing"
prompt both deep-link straight to the device's text-to-speech settings,
where Android lets you install more languages for the system voice
engine.

## Project structure

```
app/src/main/java/com/lingora/app/
  data/
    model/       Language, SupportedLanguages, TranslationOutcome, enums
    remote/      Retrofit service + DTOs + NetworkModule
    repository/  TranslationRepository
    local/       UserPreferencesRepository (DataStore)
    tts/         TtsManager (wraps android.speech.tts.TextToSpeech)
  di/            AppContainer (manual dependency container)
  ui/
    theme/       Color, Shape, Type, Theme (the aurora/glass design system)
    components/  AuroraBackground, GlassCard, LanguageSelector, etc.
    onboarding/  Purpose and level screens
    home/        The main two-language translator screen
    settings/    Voice, languages, learning goal, about
    navigation/  The NavHost and route definitions
  MainActivity.kt
  LingoraApplication.kt
```

## Versioning

Lingora's `versionName` follows the release date: `YEAR.MONTH.DAY`. The
version shipped in this repository is `2026.8.5`. `versionCode` is a
separate, ordinary incrementing integer as Android requires.

## Security and permissions

Lingora requests exactly one permission — `INTERNET`, to reach the
translation backend. Nothing else is requested: no contacts, storage,
microphone, or location access. `network_security_config.xml` explicitly
disables cleartext (plain HTTP) traffic app-wide, so all network calls
are forced over HTTPS.

## Building the project

1. Open the `Lingora/` folder in a recent stable Android Studio.
2. Let Gradle sync — this requires an internet connection to download the
   dependencies listed in `gradle/libs.versions.toml`.
3. Run on a device or emulator running Android 8.0 (API 26) or newer.

This project targets Android Gradle Plugin 8.13, Kotlin 2.3, and Compose
BOM 2026.04.01 — a current, stable toolchain. If Android Studio's Upgrade
Assistant offers a newer AGP (the 9.x line introduced a "built-in Kotlin"
mode with a different plugin setup), it's safe to accept, but not
required.

The Gradle wrapper jar itself isn't included in this repository (it's a
binary file). Android Studio will offer to regenerate it automatically on
first sync; alternatively, run `gradle wrapper --gradle-version 8.13`
once you have any Gradle installed locally.

## License

Lingora is released under the MIT License — see `LICENSE`.

## Contributing

Issues and pull requests are welcome. Two of the most useful places to
start:

- `data/model/SupportedLanguages.kt` — add a language
- `data/remote/NetworkModule.kt` — swap in a different translation
  provider
