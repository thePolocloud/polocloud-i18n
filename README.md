# 🌍 PoloCloud-i18n

> Dynamic translation system for the PoloCloud ecosystem.


PoloCloud-i18n is a lightweight internationalization module that supports translation packs,
automatic GitHub updates, in-memory caching, and `.properties` based language files.

It is designed to provide flexible and scalable localization for PoloCloud nodes and plugins.

## ✨ Features

- 🌐 Multi-language support
- 📦 Translation Packs
- 🧠 In-memory caching (per pack + language)
- 💾 Local file caching
- 🔄 Automatic GitHub updates
- 📄 `.properties` based translations
- ⚡ Lazy loading
- 🔁 Fallback language support
- 🧩 Placeholder formatting


## 🏗 Translation Pack Structure

Each translation pack contains:

```
my-pack/
 ├── pack.json
 ├── en.properties
 ├── de.properties
 ├── fr.properties
 └── ...
```

### 📄 pack.json

Contains metadata about the translation pack:

```json
{
  "name": "default-pack",
  "version": "1.0.0",
  "defaultLanguage": "en_US",
  "languages": ["en_US", "de_DE"]
}
```

### 🌐 Language Files

Each language is stored as a `.properties` file:

**en.properties**
```
welcome.message=Welcome to PoloCloud!
node.starting=Node is starting...
```

**de.properties**
```
welcome.message=Willkommen bei PoloCloud!
node.starting=Node startet...
```

## 🚀 How It Works

1. You initialize the TranslationService.
2. When a translation is requested:
    - The system checks the in-memory cache.
    - If not cached, it loads:
        - `pack.json`
        - The requested `.properties` file
3. If the pack does not exist locally:
    - It is downloaded from GitHub.
4. Translations are cached per `pack + language`.
5. All further lookups are served from memory.

> [!WARNING]
> Only the requested language is loaded — not all languages of a pack.

## 📦 Installation

Add the dependency to your project:

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("dev.httpmarco:polocloud-i18n:<version>")
}
```

Replace `<version>` with the latest release.

## 🚀 Quick Start

### 1️⃣ Initialize the Translation Service

```kotlin
TranslationService.init()
```

or

```kotlin
TranslationService.init(
    baseUrl = "https://raw.githubusercontent.com/thePolocloud/polocloud-translations/refs/heads/main",
    cacheDir = File(".translations")
)
```

to overwrite the defaults.

### 2️⃣ Translate a Key

Using default language:

```kotlin
val message = TranslationService.tr(
    pack = "default-pack",
    key = "welcome.message"
)
```

Using specific language:

```kotlin
val message = TranslationService.tr(
    pack = "default-pack",
    language = "de_DE",
    key = "welcome.message"
)
```

### 3️⃣ Using Placeholders

```kotlin
val message = TranslationService.tr(
    pack = "default-pack",
    language = "en_US",
    key = "player.join",
    "name" to "Marco"
)
```

### 4️⃣ Using the Fluent API

```kotlin
val message = TranslationService
    .pack("default-pack")
    .language("en_US")
    .format("player.join", "name" to "Marco")
```

### 5️⃣ Preloading (Optional)

Preload synchronously:

```kotlin
TranslationService.preload("default-pack")
```

Preload asynchronously:

```kotlin
TranslationService.preloadAsync("default-pack")
```

## 🔁 Fallback Behavior

If a key is missing:

1. The system tries the pack's default language.
2. If still missing:
    - A warning is logged.
    - The key itself is returned.

## 🧠 Caching System

- Cached per `pack + language`
- Lazy loaded
- Stored locally
- Served from memory
- GitHub is only accessed if:
    - The pack is missing
    - A new version is available

## 📁 Local Storage

Translation packs are stored in:

```
.translations/<pack-name>/
```

## 🔄 Update Mechanism

When loading a pack:

- The system compares local version with remote version.
- If a newer version exists on GitHub:
    - The pack is re-downloaded.
    - Cache is refreshed.
- If up-to-date:
    - Local cached version is used.

This allows centralized updates without manual file management.

## 🤝 Contributing

You can contribute by:

1. Fork the repository
2. Create a feature branch
3. Submit a Pull Request

## 📄 License

polocloud-i18n is licensed under the [Apache 2.0 License](LICENSE).

---

<p align="center">
    This project was created by <a href="https://github.com/RECHERGG/">RECHERGG</a> and the <a href="https://polocloud.de">polocloud.de</a> team.
    <br>
    ©️ 2026 thePolocloud. All rights reserved.
</p>