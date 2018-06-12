package com.gkzxhn.autoespresso.build.impl;

import android.content.SharedPreferences;

import com.gkzxhn.autoespresso.build.IBuildTestClass;
import com.gkzxhn.autoespresso.build.IBuildTestMethod;
import com.gkzxhn.autoespresso.code.PermissionCode;
import com.gkzxhn.autoespresso.config.ClassConfig;
import com.gkzxhn.autoespresso.config.Config;
import com.gkzxhn.autoespresso.config.TableConfig;
import com.gkzxhn.autoespresso.entity.MergedRegionEntity;
import com.gkzxhn.autoespresso.entity.ModuleEntity;
import com.gkzxhn.autoespresso.util.ExcelUtil;
import com.gkzxhn.autoespresso.util.TUtils;

import org.apache.poi.ss.usermodel.Sheet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Raleigh.Luo on 18/3/7.
 */

public class BuildTestClass implements IBuildTestClass {
    private int mRow= Config.MODULE_FIRST_ROW;
    private Map<String,Integer> mModuleColNames =new HashMap<>();
    private String mFilePath="";
    private ModuleEntity header;
    private String mClassName="";
    private String mPackageName;
    private String mTestClassPath;
    private IBuildTestMethod mBuildTestMethod= new BuildTestMethod();
    private  Sheet mSheet;
    private int mMaxRow=0;
    @Override
    public void init(String driverFilePath,String packagename, String testClassDir,  int sheetIndex) {
        mPackageName=packagename;
        mTestClassPath=testClassDir;
        File file=new File(driverFilePath);
        mSheet=ExcelUtil.getSheet(file,sheetIndex);
        mRow= Config.MODULE_FIRST_ROW;
        mMaxRow=mSheet.getPhysicalNumberOfRows();
        //读取Module Name
        readModuleNames();
        mRow++;//Module Value行
    }

    @Override
    public void readModuleNames() {
        List headers=Arrays.asList(TableConfig.MODULE_HEADERS);
        int maxCol=mSheet.getRow(mRow).getLastCellNum();
        for (int col = 0; col < maxCol; col++) {
            String value = ExcelUtil.getCellValue(mSheet, mRow, col);
            if (headers.contains(value)) {
                mModuleColNames.put(value, col);
            }
        }
    }

    @Override
    public void build() {
        if(mModuleColNames.size()>0) {
            String classname=getValue(TableConfig.CLASS_NAME);
            boolean isCreate=TUtils.valueToBoolean(getValue(TableConfig.IS_CREATE));
            //测试类名不能为空
            if(isCreate&&classname.length()>0) {
                //获取ModuleNumber 合并的单元格
                MergedRegionEntity moduleNumber = ExcelUtil.isMergedRegion(mSheet, mRow,mModuleColNames.get(TableConfig.MODULE_NUMBER));
                int firstRow = moduleNumber.getLastRow()+2;
                int lastRow = mMaxRow;

                header = new ModuleEntity();
                header.setModuleNumber(moduleNumber.getValue());
                header.setModuleName(getValue(TableConfig.MODULE_NAME));
                header.setClassName(classname);
                header.setClassPackageName(getValue(TableConfig.CLASS_PACKAGE_NAME));
                header.setFirstRow(moduleNumber.getFirstRow());
                header.setLastRow(moduleNumber.getLastRow());
                header.setSharedPreferencesName(getValue(TableConfig.SHAREDPREFERENCES_NAME));
                createFile();
                mBuildTestMethod.init(mSheet, header.getModuleNumber(),firstRow, lastRow);
                String classContent = mBuildTestMethod.build();
                write(classContent);
            }
        }
    }


    private String getValue(String headername){
        return ExcelUtil.getCellValue(mSheet,mRow,mModuleColNames.get(headername));
    }

    @Override
    public void createFile() {
        if(header!=null) {

            String fileDir = mTestClassPath;
            TUtils.createDir(fileDir);

            String name=String.format(ClassConfig.TEST_CLASS_NAME, header.getClassName());
            //已有重复名，则生成添加Module编号module
            if(Config.MODULE_NAMES.contains(name)){
                name=String.format(ClassConfig.TEST_CLASS_NAME, header.getClassName()+header.getModuleNumber());
            }
            mClassName=name;
            Config.MODULE_NAMES.add(mClassName);
            String fileName = mClassName+ Config.TEST_CLASS_SUFFIX;

            //创建文件
            mFilePath = fileDir + "/" + fileName;
            File file = new File(mFilePath);
            //如果有则删除
            if (file.exists()) file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void write(String classContent) {
        File file = new File(mFilePath);
        try {
            StringBuffer sb = new StringBuffer();
            //写入import
            sb.append(ClassConfig.getImports(mPackageName,
                    header.getClassPackageName(),header.getClassName()));
            //头部注视
            sb.append(ClassConfig.getHeaders(header.getModuleName()+" " +header.getModuleNumber()));
            //创建类
            sb.append(ClassConfig.getClassModule(mClassName,header.getClassName(),getIntent()));


            /***********************所需权限*********************************/

            /********************************************************/
            //before method
            sb.append(ClassConfig.getBeforeMethod(getPermissions(),header.getSharedPreferencesName(),getSharedPreferences()));
            //after method
            sb.append(ClassConfig.getAfterMethod());
            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
            sb.append(classContent);
            sb.append("\n}");
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**所需权限
     * @return
     */
    private String getIntent(){
        StringBuffer extras=new StringBuffer();
        int col=mModuleColNames.get(TableConfig.INTENT_EXTRA);
        for(int row=header.getFirstRow();row<=header.getLastRow();row++){
            String putExtra= ExcelUtil.getCellValue(mSheet,row,col);
            if(putExtra.length()>0){
                String key=ExcelUtil.getCellValue(mSheet,row,col+1);
                String value=ExcelUtil.getCellValue(mSheet,row,col+2);
                extras.append(PermissionCode.getIntentExtras(putExtra,key,value));
            }
        }
        return  extras.toString();
    }

    /**所需权限
     * @return
     */
    private String getSharedPreferences(){
        StringBuffer mSharedPreferences=new StringBuffer();
        int col=mModuleColNames.get(TableConfig.SHAREDPREFERENCES);
        for(int row=header.getFirstRow();row<=header.getLastRow();row++){
            String putExtra= ExcelUtil.getCellValue(mSheet,row,col);
            if(putExtra.length()>0){
                String extra="";
                String key=ExcelUtil.getCellValue(mSheet,row,col+1);
                String value=ExcelUtil.getCellValue(mSheet,row,col+2);
                mSharedPreferences.append(PermissionCode.getSharedpreference(putExtra,key,value));
            }
        }
        return  mSharedPreferences.toString();
    }
    /**所需权限
     * @return
     */
    private String getPermissions(){
        String permissions="";
        List<String> permissionArray= new ArrayList();
        for(int row=header.getFirstRow();row<=header.getLastRow();row++){
            String permission= ExcelUtil.getCellValue(mSheet,row,mModuleColNames.get(TableConfig.PREMISSIONS));
            if(permission.length()>0)permissionArray.add(permission);
        }
        if(permissionArray.size()>0){
            String[] a=new String[permissionArray.size()];
            permissionArray.toArray(a);
            permissions= PermissionCode.getPermissionShell(a);
        }
        return  permissions;
    }

}
