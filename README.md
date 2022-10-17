# ApkSignatureHook

## Intro
안드로이드 앱의 apk 서명값 기반 무결성 검증 기법 회피 모듈

## Description
많은 안드로이드 앱들이 위변조를 방지하기 위해 apk 서명값(공개키)를 추출해서 서버 또는 클라이언트에서 이를 실제 서명값과 비교하는 방식을 사용한다.
이에 사용되는 구문이
    Signature[] arrayOfSignature = (this.b.getPackageManager().getPackageInfo(this.b.getPackageName(), 64)).signatures;
    byte[] arrayOfByte = arrayOfSignature[0].toByteArray();
와 같은 방식이다.
이는 apk 서명에 사용된 서명값을 bytearray로 얻어낸 후 실제 값과 비교하는 방식인데 이 과정에서 후킹이 가능하다.
이 프로젝트에서는 sandhook과 Xposed 라이브러리를 활용하여 위변조된 서명값을 정상 서명값으로 변경하는 모듈을 개발한다.
우선 signature class의 toByteArray() method를 후킹하여 정상 서명값을 return 하는 방식으로 후킹이 진행된다.
정상 서명값은 res/raw 경로 아래에 정상 CERT.RSA 값으로부터 파싱하여 얻어낸다.

## How to use
우선 target apk를
    apktool d target.apk 
명령어를 통해 디컴파일한다. 
그 후 original/META-INF/ 하에 존재하는 .RSA 확장자의 파일을 /res/raw 폴더로 옮기고 이를 CERT.RSA로 파일명을 변경 한다.
(raw 폴더가 없을시 생성)
그 후 AndroidManifest.xml 파일을 열어 가장 먼저 실행되는 activity를 찾는다. (최상단의 <activity> tag를 찾는다) 해당 activity의 name을 통해 이에 해당하는 smali 파일을 찾아간다.
(예: com.a.b.c가 activity 명일 경우 smali/com/a/b/c.smali 파일) 
이 smali 파일의 onCreate 부분을 찾아 아래와 같이 호출 직후에 코드를 추가한다.

    .method public final onCreate(Landroid/os/Bundle;)V
    .locals 4

    new-instance v0, Lcom/ufo64/ApkSignatureHook/MainModule;
    invoke-virtual {p0}, Lcom/supercell/titan/GameApp;->getApplicationContext()Landroid/content/Context;
    move-result-object v1
    invoke-direct {v0, v1}, Lcom/ufo64/ApkSignatureHook/MainModule;-><init>(Landroid/content/Context;)V
