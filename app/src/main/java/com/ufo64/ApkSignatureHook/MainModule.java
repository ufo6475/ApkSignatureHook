package com.ufo64.ApkSignatureHook;

import android.content.Context;
import android.content.pm.Signature;
import android.util.Log;

import com.swift.sandhook.SandHook;
import com.swift.sandhook.SandHookConfig;
import com.swift.sandhook.xposedcompat.XposedCompat;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class MainModule {
    static final String TAG = "su";
    Context mContext;
    String mTargetPackageName;

    public MainModule(Context argv){
        this.mContext = argv;
        this.mTargetPackageName = argv.getPackageName();
        SandHookConfig.DEBUG =true;
        SandHook.disableVMInline();
        SandHook.tryDisableProfile(this.mTargetPackageName);
        SandHook.disableDex2oatInline(false);
        if(SandHookConfig.SDK_INT>=28){
            SandHook.passApiCheck();
        }

        XposedCompat.cacheDir = XposedCompat.getCacheDir();
        XposedCompat.context = argv;
        XposedCompat.classLoader = argv.getClassLoader();
        XposedCompat.isFirstApplication = true;

        this.startHook();
    }


    public  void startHook(){
        try{
            XposedHelpers.findAndHookMethod(Signature.class, "toByteArray", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] beforeSign = (byte[]) param.getResult();
                    Log.d(TAG,"before Signature"+beforeSign.toString());
                    //Scanner scanner = new Scanner(new File(""))
                    String tmp ="30820307308201efa00302010202046c3d094b300d06092a864886f70d01010b05003034310b30090603550406130246493111300f0603550407130848656c73696e6b6931123010060355040a1309537570657263656c6c301e170d3137303632303134313631325a170d3432303631343134313631325a3034310b30090603550406130246493111300f0603550407130848656c73696e6b6931123010060355040a1309537570657263656c6c30820122300d06092a864886f70d01010105000382010f003082010a0282010100aa3afb81611da441bb93008ab035ec000503e0cbd2d16c473d8618c8a2257840f22c68db25b51d5f11eb89a1ca669e7ab704c94fe37b468120451620131d8b3c34ebd199ca5a8ed5676c91e42f9406d62f57088d49fd702d60dcb65bc6b679491e0de0e864fabb60c987649509edfc5546eac300778e83f023c21031afb908867020d533f4e5937f22e5cd1471a4239fe93b6310d6bbb70e380700086075a942c684390bc779c262b2bd1b5541a2f1443d478064679e639387e675869c73c79929a8533f3678a1b2e77006bb04a99ccf783fc6bd0a8e61267efc843b480dc095bc33fb6e9bbf10a9c7264e4a4bdfe85be4c8c81421ae8e548adc545b671db4d70203010001a321301f301d0603551d0e0416041405f821509830fc525b3a4ab24db88aa5ff7514e0300d06092a864886f70d01010b0500038201010014f314e9217d4171caf747e9d9f69b998d80d4a7e12f1aa1b599972368b17631d6e92ba996b5e69d517287cbe0feb79abcf72c76c78907694626603cdf1da88e929e941704737781d5eec6adf38ad4a8079afb2129f55fd5a14e825393d26b384133eab2f1cecf81bfa221cb0f5049365455c043afa35751f9f44f53fb906ad96eff77482c97e0dd1286bed2316d8d2efcd58e9804ff38cc68257a4f72ac88acc942eede548192982f9b15823b531e16cdb53afc74c626061450e93c366f8439b2b3cbc1c440e3c76bcf48e233edd9226edc7a8ad31872116328ccc98f0a76de54da8ec9a421fa82c6bdc906a865ed471f4d19274146a7fb3b39aad181f767df";
                    //String tmp ="308202bf308201a7a0030201020204622e2e84300d06092a864886f70d01010b0500300f310d300b06035504031304737769743020170d3132303732353031323535385a180f32313132303730313031323535385a300f310d300b060355040313047377697430820122300d06092a864886f70d01010105000382010f003082010a028201010091114a0a36b2eb0b60e3bcdef275b7ea296d8c62cde6d9d3fff5849cc7d0a881e2ed43d687e49cef4c7a2164be628fe54338aadbdc67312da68b96394b0e96464d41431701b3fe44f0e41bce261fdef5eb92667ce27821069ccd9ec9b8f73397fa539ddf35823f60a014647c54c268172f8759929ac62a816dd08068db6ba7014ddbbf436ab02be2a109708963be4b2ff308b7d2fd6e06246fb8958fc21392de3518b1efb92a16de2e4b9a450b41d259197b2cf9929b917c1a54b736b7a0cb246713516cc9117cffe7506a651df9622fb03d92ee43d634e02ea03f522416b044e87f888f0f9cc476b57f5fe5e65159ace371c4eea6ee13c7cecb3ac05c67adc50203010001a321301f301d0603551d0e041604140888ec875b58967f0e6ed81c6c3227a4e6e389b0300d06092a864886f70d01010b05000382010100576bd4756b980113898e41b380f9a530566c3838013d8bf14958c47fdf51cb0c83df8bd972d8dd1a376464448c788de17f1f59187fad6c5cd6903fdd0711991954cd054e0bd9a08df255e46a637ed828fb72564a40847b21f4256ddb1844d6203fbfa15b2293e611b7cb49fbad4eb8b84a73ce1c07d9c486089a0bfa360aa7763bc96fe93e924f89f0c84f86f831d4ff13698b80efca399cf59964b6bedd9a475235f46b9f6bb27dac5a49135ce4657c70c5ac323d28bf3a291d3784427763084ee74e0510969b0666fa7e7e2f222c96101aa5bbbf0c8da63aed11bf13bc0bf85d62542b36511690c5ceb826bf8c885962718622ff3b6521464e4a76b14a2d76";

                    byte[] bytes = new java.math.BigInteger(tmp,16).toByteArray();
//                    beforeInfo.signatures[0]=new Signature(bytes);
                    Log.d(TAG,"after Signature"+bytes.toString());
                    param.setResult(bytes);
                }
            });
        }
        catch (Exception e){
            Log.d(TAG,e.toString());
        }
    }
}
