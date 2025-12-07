# Changelog

## 2.1.0

### Added
- `addListener` and `removeListener` to register multiple listeners.
- Accessibility actions to expand and collapse, and state description for screen readers.
- RTL support for `layout_marginStart` and `layout_marginEnd`.

### Changed
- The layout expands to the width available inside its parent instead of the screen width.
- `ScalingLayoutBehavior` uses the toolbar height of the theme instead of a fixed 56dp.
- `init(Context, AttributeSet)` is no longer public.
- The library no longer ships an `app_name` string resource.

### Fixed
- `expand()`, `collapse()` and `setProgress()` called before the first layout were ignored.
- A running animation overwrote progress set with `setProgress()`.
- Negative `app:radiusFactor` values produced a negative corner radius.

### Build
- Robolectric tests for the library and instrumented tests for the sample app.
- Fixed lint warnings in the sample app.

## 2.0.0

### Breaking changes
- Migrated from the Android Support Library to AndroidX and Material Components.
- minSdk is now 21.
- `ScalingLayoutListener.onProgress` reports 0 for collapsed and 1 for expanded, matching `setProgress`.

### Added
- `app:animationDuration` attribute, `setAnimationDuration(long)` and `setInterpolator(TimeInterpolator)`.
- `getProgress()`.
- Expanded/collapsed state is saved and restored across configuration changes.
- Consumer ProGuard rules for `ScalingLayoutBehavior`.

### Fixed
- `app:radiusFactor="0"` collapsed the layout to zero width.
- Division by zero in `ScalingLayoutBehavior` when the `AppBarLayout` has no scroll range.
- `onCollapsed` and `onExpanded` were called repeatedly instead of once per state change.
- Calling `expand()` or `collapse()` during an animation made the layout jump.
- Running animation was not cancelled when the view was detached.
- Max radius lost precision on odd layout heights.
- Crash when the parent's `LayoutParams` are not `MarginLayoutParams`.
- Elevation set at runtime was overwritten on every update.

### Build
- Gradle 8.7, Android Gradle Plugin 8.5, compileSdk/targetSdk 34, Java 17.
- Publishing via `maven-publish` for JitPack.
- GitHub Actions CI.

## 1.2.1
- StateListAnimator support.
- Disable view outline provider on pre-Lollipop devices.

## 1.2
- Elevation shadow support.
- minSdk lowered to 15.

## 1.1
- Anti-aliased rounded corners.
- Updated build dependencies.

## 1.0
- Initial release.
