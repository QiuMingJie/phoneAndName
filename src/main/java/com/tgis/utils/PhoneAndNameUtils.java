package com.tgis.utils;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author QiuMingJie
 * @date 2021/8/18 14:44
 * @description
 */
public class PhoneAndNameUtils {

    private static String createRandomNameTable = "create database if not exists wuyilin;\r\n" +
            "use wuyilin;\r\n " +
            "drop table if exists randomName;\r\n" +
            "CREATE  TABLE if not exists randomName(id INT(11) PRIMARY KEY,name VARCHAR(200) COMMENT '姓名') COMMENT='随机生成名字表'character set utf8;\r\n";

    private static String insertRandomNameTable = "insert into randomName(id,name) values ";

    private static String createRandomPhoneTable = "create database if not exists wuyilin;\r\n" +
            "use wuyilin;\r\n" +
            "drop table if exists randomPhone;\r\n" +
            "CREATE  TABLE if not exists randomPhone(id INT(11) PRIMARY KEY,phone VARCHAR(200) COMMENT '联系方式') COMMENT='随机生成联系方式表'character set utf8;\r\n";

    private static String insertRandomPhoneTable = "insert into randomPhone(id,phone) values ";

    public static void main(String[] args) {
        //生成联系方式条数
        int contactCount = 1000000;
        //生成手机号的几率,其他为座机0202995234
        int mobilePer = 95;
        //生成带星号的几率百分之几
        int withStar = 5;
        generatePhoneSqlFile(contactCount, withStar, mobilePer);

        //生成联系方式条数
        int nameCount = 1000000;
        //比例关系调整入下
        //##明星名字 权重
        Integer starNamePercent = 10;
        // #字典名
        Integer dictNamePercent = 30;
        // #英语名
        Integer englishNamePercent = 5;
        //   #随机名
        Integer randomNamePercent = 45;
        //先生女士名
        Integer xianShengNvShiPercent = 10;
        generateNameSqlFile(nameCount,starNamePercent, dictNamePercent, englishNamePercent, randomNamePercent, xianShengNvShiPercent);

    }


    private static void generatePhoneSqlFile(int contactNum, int withStar,int mobilePer) {
        StringBuilder contactSql = new StringBuilder();
        contactSql.append(createRandomPhoneTable);
        contactSql.append(insertRandomPhoneTable);
        for (int i = 0; i < contactNum; i++) {
            if (PhoneUtils.randomAB(0, 100) < mobilePer) {
                contactSql.append(String.format("(%s,'%s'),",i+1, PhoneUtils.generate(true,withStar)));
            } else {
                contactSql.append(String.format("(%s,'%s'),",i+1, PhoneUtils.generate(false,withStar)));
            }
        }
        String insertSqlResult = contactSql.toString().substring(0, contactSql.length() - 1);
        MySqlOperUtils.batchInsertSql(insertSqlResult);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("randomPhone.sql"));
            out.write(contactSql.toString());
            out.close();
            System.out.println("randomPhone文件创建成功！");
        } catch (IOException ignored) {
        }
    }

    private static void generateNameSqlFile(int contactNum, Integer starNamePercent, Integer dictNamePercent, Integer englishNamePercent, Integer randomNamePercent, Integer xianShengNvShiPercent) {
        StringBuilder nameSql = new StringBuilder();
        nameSql.append(createRandomNameTable);
        nameSql.append(insertRandomNameTable);
        for (int i = 0; i < contactNum; i++) {
            nameSql.append(String.format("(%s,'%s'),",i+1, NameUtils.generate(starNamePercent, dictNamePercent, englishNamePercent, randomNamePercent, xianShengNvShiPercent)));
        }
        String insertSqlResult = nameSql.toString().substring(0, nameSql.length() - 1);
        MySqlOperUtils.batchInsertSql(insertSqlResult);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("randomName.sql"));
            out.write(nameSql.toString());
            out.close();
            System.out.println("randomName文件创建成功！");
        } catch (IOException ignored) {
        }
    }

}
