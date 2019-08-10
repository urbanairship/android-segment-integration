ChangeLog
=========

Version 2.1.0 - August 9, 2019
==============================
- Update track integration to set the transaction ID.
- Update identify to also set boolean attributes as tags in the tag group `segment-integration`

Version 2.0.0 - July 17, 2019
=============================
- Update Airship Android SDK to 11.0.1
- Add sample app

Applications are required to migrate to Android X before using this version. For more info, see
[Migrating to AndroidX](https://developer.android.com/jetpack/androidx/migrate)


Version 1.2.1 - Mar 14, 2019
==============================
- Updated Urban Airship Android SDK to 9.7.1.

Fixed a security issue within Android Urban Airship SDK, that could allow trusted
URL redirects in certain edge cases. All applications that are using version 1.2.0
on Android should update as soon as possible. For more details, please email
security@urbanairship.com.

Version 1.2.0 - Apr 25, 2018
==============================
- Updated Urban Airship Android SDK to 9.1.0.

Version 1.1.0 - Sep 30, 2016
=============================
- Updated Urban Airship Android SDK to 8.0.+.

Version 1.0.2 - Aug 3, 2016
============================
- Changed the project minSdkVersion to 16.

Version 1.0.1 - May 10, 2016
============================
- Fixed Factory key.
- Updated used event fields in the screen() and group() implementations.
