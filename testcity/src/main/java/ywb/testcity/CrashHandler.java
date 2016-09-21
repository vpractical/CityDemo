package ywb.testcity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告. 
 *  
 * @author user 
 *  
 */   
public class CrashHandler implements UncaughtExceptionHandler {   
       
    //系统默认的UncaughtException处理类     
    private UncaughtExceptionHandler defaultHandler;
    //CrashHandler实例    
    private static CrashHandler instance = new CrashHandler();   
    //程序的Context对象    
    private Context context;   
    //用来存储设备信息和异常信息    
    private Map<String, String> infos = new HashMap<String, String>();   
   
    //用于格式化日期,作为日志文件名的一部分    
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");   
   
    public static String crashPath = "/sdcard/crash/";
   
    /** 获取CrashHandler实例 ,单例模式 */   
    public static CrashHandler getInstance() {   
        return instance;   
    }   
   
    /** 
     * 初始化 
     *  
     * @param context 
     */   
    public void init(Context context) {   
        this.context = context;   
        //获取系统默认的UncaughtException处理器    
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();   
        //设置该CrashHandler为程序的默认处理器    
        Thread.setDefaultUncaughtExceptionHandler(this);   
    }   
   
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */   
    @Override   
    public void uncaughtException(Thread thread, Throwable ex) {   
        if (!handleException(ex) && defaultHandler != null) {   
            //如果用户没有处理则让系统默认的异常处理器来处理    
        	defaultHandler.uncaughtException(thread, ex);   
        } else {   
            try {   
                Thread.sleep(2000);   
            } catch (InterruptedException e) {   
            	e.printStackTrace();
            }  
            
            //退出程序    
            android.os.Process.killProcess(android.os.Process.myPid());   
            System.exit(0);   
        }   
    }   
   
    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     *  
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false. 
     */   
    private boolean handleException(Throwable ex) {   
        if (ex == null) {   
            return false;   
        }
        //使用Toast来显示异常信息    
        new Thread() {   
            @Override   
            public void run() {   
                Looper.prepare();   
                Toast.makeText(context, "很抱歉,程序出现异常!即将退出", Toast.LENGTH_SHORT).show();
                Looper.loop();   
            }   
        }.start();   
        //收集设备参数信息     
        collectDeviceInfo(context);   
        //保存日志文件     
        saveCrashInfo2File(ex);   
        return true;   
    }   
       
    /** 
     * 收集设备参数信息 
     */
    public void collectDeviceInfo(Context c) {   
        try {   
            PackageManager pm = c.getPackageManager();   
            PackageInfo info = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);   
            if (info != null) {   
                String versionName = info.versionName == null ? "null" : info.versionName;   
                String versionCode = info.versionCode + "";   
                infos.put("versionName", versionName);   
                infos.put("versionCode", versionCode);   
            }
        } catch (NameNotFoundException e) {   
            e.printStackTrace(); 
        }   
        Field[] fields = Build.class.getDeclaredFields();   
        for (Field field : fields) {   
            try {   
                field.setAccessible(true);   
                infos.put(field.getName(), field.get(null).toString());   
            } catch (Exception e) {   
            	  e.printStackTrace(); 
            }   
        }  
    }   
   
    /** 
     * 保存错误信息到文件中 
     *  
     * @param ex 
     * @return  返回文件名称,便于将文件传送到服务器 
     */   
    private String saveCrashInfo2File(Throwable ex) {   
        StringBuffer sb = new StringBuffer();   
        for (Map.Entry<String, String> entry : infos.entrySet()) {   
            String key = entry.getKey();   
            String value = entry.getValue();   
            sb.append(key + "=" + value + "\n");   
        }   
        Writer writer = new StringWriter();   
        PrintWriter printWriter = new PrintWriter(writer);   
        ex.printStackTrace(printWriter);   
        Throwable cause = ex.getCause();   
        while (cause != null) {   
            cause.printStackTrace(printWriter);   
            cause = cause.getCause();   
        }   
        printWriter.close();   
        String result = writer.toString();   
        sb.append(result);   
        try {   
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());   
            String fileName = time + "-" + timestamp + ".txt";   
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {   
                File dir = new File(crashPath);   
                if (!dir.exists()) {   
                    dir.mkdirs();   
                }   
                FileOutputStream fos = new FileOutputStream(crashPath + fileName);   
                fos.write(sb.toString().getBytes());   
                fos.close();   
            }   
            return fileName;   
        } catch (Exception e) {   
        	  e.printStackTrace(); 
        }   
        return null;   
    }

    /**
     * 获取栈顶activity名字
     * @param context2
     * @return
     */
/*    private String getTopActivity(Context context2)
    {
    	
    	 ActivityManager manager = (ActivityManager) context2.getSystemService(Context.ACTIVITY_SERVICE);
         RunningTaskInfo info = manager.getRunningTasks(1).get(0);
         String shortClassName = info.topActivity.getShortClassName();    //类名
         String className = info.topActivity.getClassName();              //完整类名
         String packageName = info.topActivity.getPackageName();          //包名
         return shortClassName;
    	 //获取ComponentName
//         List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
//         if(runningTaskInfos != null)
//           return (runningTaskInfos.get(0).topActivity);
//              else
//           return null ;
    }*/
}
