package com.jpc.multithread.intentservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.jpc.multithread.R;
import java.util.List;

/**
 * Created by jpc on 2018/6/4.
 */

public class MyIntentServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 同一服务只会开启1个工作线程
        // 在onHandleIntent（）函数里，依次处理传入的Intent请求
        // 将请求通过Bundle对象传入到Intent，再传入到服务里

        // 请求1
        Intent i = new Intent("com.jpc.multithread.intentservice");
        //https://blog.csdn.net/shenzhonglaoxu/article/details/42708723
        i.setPackage(this.getPackageName());
        Bundle bundle = new Bundle();
        bundle.putString("taskName", "task1");
        i.putExtras(bundle);

        startService(i);


        // 请求2
        Intent i2 = new Intent("com.jpc.multithread.intentservice");
        Bundle bundle2 = new Bundle();
        bundle2.putString("taskName", "task2");
        i2.putExtras(bundle2);
        //https://blog.csdn.net/shenzhonglaoxu/article/details/42675287
        Intent eintent2 = new Intent(createExplicitFromImplicitIntent(this,i2));
        startService(eintent2);

        // 请求3
        Intent i3 = new Intent(MyIntentServiceActivity.this,MyIntentService.class);
        Bundle bundle3 = new Bundle();
        bundle3.putString("taskName", "task3");
        i3.putExtras(bundle3);
        startService(i3);
        startService(i);  //多次启动
    }

    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
