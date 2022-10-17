package com.ufo64.ApkSignatureHook;

import android.content.Context;
import android.content.pm.Signature;

import android.util.Log;

import com.swift.sandhook.SandHook;
import com.swift.sandhook.SandHookConfig;
import com.swift.sandhook.xposedcompat.XposedCompat;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;


import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class MainModule {
    static final String TAG = "su";
    Context mContext;
    String mTargetPackageName;
    byte[] afterSignature;
    String fileName="CERT.RSA";


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

        try {
            afterSignature = getSignatureFromRSA(argv);
        } catch (CertificateException e) {
            Log.d(TAG,e.toString());
        } catch (Exception e) {
            Log.d(TAG,e.toString());
        }
        this.startHook();
    }

    private byte[] getSignatureFromRSA(Context context) throws CertificateException, IOException {
        //int id = context.getResources().getIdentifier("CERT","raw",context.getPackageName());
        int id = context.getResources().getIdentifier("CERT", "raw", context.getPackageName());
        InputStream is = context.getResources().openRawResource(id);
        byte[] data = new byte[is.available()];
        is.read(data);
        is.close();

        Log.d("data",byteArrayToHex(data));
        int startIdx= findStart(data);
        if(startIdx==-1){
            Log.d(TAG,"Can't find PKCS7");
            return null;
        }
        int size=256*Byte.toUnsignedInt(data[startIdx+2])+Byte.toUnsignedInt(data[startIdx+3])+4;

        Log.d("size",size+"");
        byte[] signature = new byte[size];

        for(int i=0;i<size;i++){
            signature[i]=data[startIdx+i];
        }
        Log.d("signauture",byteArrayToHex(signature));
        return signature;
    }


    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }

    private int findStart(byte[] data){
        byte[] cmp ={(byte)0x06,(byte)0x09,(byte)0x2A,(byte)0x86,(byte)0x48,(byte)0x86,(byte)0xF7,(byte)0x0D,(byte)0x01,(byte)0x07,(byte)0x01};
        for(int i=0;i<data.length-cmp.length;i++){
            boolean found = true;
            for(int j=0;j<cmp.length;j++){
                if(data[i+j]!=cmp[j]){
                    found=false;
                    break;
                }
            }
            if(found)
                return i+15;
        }
        return -1;
    }


    public  void startHook(){
        try{
            XposedHelpers.findAndHookMethod(Signature.class, "toByteArray", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    byte[] beforeSign = (byte[]) param.getResult();
                    Log.d(TAG,"before Signature"+byteArrayToHex(beforeSign));
                    Log.d(TAG,"after Signature"+byteArrayToHex(afterSignature));
                    param.setResult(afterSignature);
                }
            });
        }
        catch (Exception e){
            Log.d(TAG,e.toString());
        }
    }
}
