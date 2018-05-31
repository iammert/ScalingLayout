# ScalingLayout 
[![Backers on Open Collective](https://opencollective.com/ScalingLayout/backers/badge.svg)](#backers)
 [![Sponsors on Open Collective](https://opencollective.com/ScalingLayout/sponsors/badge.svg)](#sponsors) 

Scale your layout on user interaction. [Live Demo](https://www.youtube.com/watch?v=wA41H0UMoHQ)

Motivated by layout in [Blinkist](https://play.google.com/store/apps/details?id=com.blinkslabs.blinkist.android) app and search bar in [Spotify](https://play.google.com/store/apps/details?id=com.spotify.music) app.

<img src="https://raw.githubusercontent.com/iammert/ScalingLayout/master/art/cover_scaling.png"/>

## Demo
<img src="https://github.com/iammert/ScalingLayout/blob/master/art/gif_behavior.gif"/>

## Fab Demo
<img src="https://github.com/iammert/ScalingLayout/blob/master/art/gif_fab.gif"/>

## Spotify Search Demo
<img src="https://github.com/iammert/ScalingLayout/blob/master/art/gif_searchbar.gif"/>

## Usage
```xml
<iammert.com.view.scalinglib.ScalingLayout
        android:id="@+id/scalingLayout"
        android:layout_width="300dp"
        android:layout_height="48dp"
        app:radiusFactor="1">
        
        <!-- Your content here -->
        
</iammert.com.view.scalinglib.ScalingLayout>
```

```java
scalingLayout.expand(); //use this if you want to expand all
scalingLayout.collapse(); //user this if you want to collapse view to initial state.
scalingLayout.setProgress(float progress); //1 is fully expanded, 0 is initial state.
```

## Listener
```java
scalingLayout.setListener(new ScalingLayoutListener() {
    @Override
    public void onCollapsed() {}

    @Override
    public void onExpanded() {}

    @Override
    public void onProgress(float progress) {}
});
```

## Attribute
```app:radiusFactor``` value is between 0 and 1 float value. 1 = full rounded corner. 0 = no rounded corner.

## ScalingLayoutBehaviour
<img src="https://github.com/iammert/ScalingLayout/blob/master/art/gif_behavior.gif"/>

```xml
<iammert.com.view.scalinglib.ScalingLayout
        android:id="@+id/scalingLayout"
        android:layout_width="300dp"
        android:layout_height="48dp"
        app:radiusFactor="1"
        app:layout_behavior="iammert.com.view.scalinglib.ScalingLayoutBehavior">
        
        <!-- Your content here -->
        
</iammert.com.view.scalinglib.ScalingLayout>
```

## Download
<a href='https://ko-fi.com/P5P872LP' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi1.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>


```gradle
maven { url 'https://jitpack.io' }
```

```gradle
dependencies {
  compile 'com.github.iammert:ScalingLayout:1.2.1'
}
```
License
--------


    Copyright 2017 Mert Şimşek.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.






## Contributors

This project exists thanks to all the people who contribute. 
<a href="graphs/contributors"><img src="https://opencollective.com/ScalingLayout/contributors.svg?width=890&button=false" /></a>


## Backers

Thank you to all our backers! 🙏 [[Become a backer](https://opencollective.com/ScalingLayout#backer)]

<a href="https://opencollective.com/ScalingLayout#backers" target="_blank"><img src="https://opencollective.com/ScalingLayout/backers.svg?width=890"></a>


## Sponsors

Support this project by becoming a sponsor. Your logo will show up here with a link to your website. [[Become a sponsor](https://opencollective.com/ScalingLayout#sponsor)]

<a href="https://opencollective.com/ScalingLayout/sponsor/0/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/0/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/1/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/1/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/2/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/2/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/3/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/3/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/4/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/4/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/5/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/5/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/6/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/6/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/7/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/7/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/8/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/8/avatar.svg"></a>
<a href="https://opencollective.com/ScalingLayout/sponsor/9/website" target="_blank"><img src="https://opencollective.com/ScalingLayout/sponsor/9/avatar.svg"></a>


