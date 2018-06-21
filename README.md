# cordova-plugin-filepath-generic

This plugin allows you to resolve the native filesystem path for Android content 
URIs and is based on code in the plugin [cordova-plugin-filepath](https://github.com/hiddentao/cordova-plugin-filepath).

This version comes from a necessity because of the error "column data does not exist" when getting the file path on some phones.

I basicaly edited the getPath method and when the uri scheme is "content" it always call's the method getDriveFilePath that forces the creation of a temporary file with the content of uri received. then it returns the filepath of the temporary file.

## Installation

```bash
$ cordova plugin add cordova-plugin-filepath-generic
```

## Supported Platforms

* Android

## Usage

Once installed the plugin defines the `window.FilePath` object. To resolve a 
file path:

```js
window.FilePath.resolveNativePath('content://...', successCallback, errorCallback);
```

##### successCallback
Returns the ``file://`` file path.

##### errorCallback
Returns the following object:
```js
{ code: <integer>, message: <string> }
```
Possible error codes are:
* ``-1`` - describes an invalid action
* ``0`` - ``file://`` path could not be resolved
* ``1`` - the native path links to a cloud file (e.g: from Google Drive app)

## LICENSE

Apache (see LICENSE.md)


