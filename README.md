# Image Hash Remote Calculator

Demo application for calculating the hash of an image. The hash is calculating on a remote server and storing in local database. You can find server source code here [Ktor Hash Calculator Server][KtorHashCalculator].
The image is sent to the server in base64 format. The user can select an image from the gallery or take a new photo. Images, hash and number of clicks on image are stored in a local Room database.

To demonstrate the operation of the application, the server is available at http://dikoresearch.ru/api/ktorhash/.
Also this url is harcoded in Android App, but you can download server sources and run your own server.

#### Used technologies
- RxJava
- Retrofit
- CameraX
- Room
- ViewModel with LiveData
- DI based on Application Class
- Fragment Navigation based on Fragment Manager

[KtorHashCalculator]: https://github.com/Kodmia/Ktor_hash_calculator