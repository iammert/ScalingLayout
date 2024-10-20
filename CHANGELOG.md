# Changelog

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
