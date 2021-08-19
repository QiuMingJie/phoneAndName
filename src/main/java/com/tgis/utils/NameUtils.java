package com.tgis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author QiuMingJie
 * @date 2021/8/18 9:30
 * @description
 */
public class NameUtils {


    //##明星名字 权重
    private static Integer starNamePercent = 10;
    // #字典名
    private static Integer dictNamePercent = 70;
    // #英语名
    private static Integer englishNamePercent = 10;
    //   #随机名
    private static Integer randomNamePercent = 10;
    //   ##随机起名20%是单字名
    private static Integer xianShengNvShiPercent = 20;

    static {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String[] XianShengNvShiList = new String[]{"先生","女士","小姐","帅哥","仙女"};

    public static String generate(Integer starNamePercent, Integer dictNamePercent, Integer englishNamePercent, Integer randomNamePercent, Integer xianShengNvShiPercent) {
        NameUtils.starNamePercent = starNamePercent;
        NameUtils.dictNamePercent = dictNamePercent;
        NameUtils.englishNamePercent = englishNamePercent;
        NameUtils.randomNamePercent = randomNamePercent;
        NameUtils.xianShengNvShiPercent = xianShengNvShiPercent;
        return generate();
    }

    public static String generate() {
        try {
            return getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<Integer, String> firstNameMap;
    private static List<Integer> TFSorted;
    private static List<String> starName;
    private static List<String> dictName;
    private static List<String> englishName;


    private static void init() throws IOException {
        firstNameMap = loadAllFirstName("/asserts/所有姓.txt");
        starName = loadNames("/asserts/女明星.txt");
        starName.addAll(loadNames("/asserts/男明星.txt"));
        dictName = loadNames("/asserts/女性.txt");
        dictName.addAll(loadNames("/asserts/男性.txt"));
        englishName = loadNames("/asserts/英文名.txt");
        List<Integer> TF = new ArrayList<Integer>(firstNameMap.keySet());
        TFSorted = TF.stream().sorted().collect(Collectors.toList());
    }

    private static String getName() throws IOException {
        Integer allPercent = starNamePercent + randomNamePercent + englishNamePercent + dictNamePercent+xianShengNvShiPercent;
        Map<Integer, String> allPercentMap = new HashMap<>();
        allPercentMap.put(starNamePercent, "star");
        allPercentMap.put(starNamePercent + randomNamePercent, "random");
        allPercentMap.put(starNamePercent + randomNamePercent + englishNamePercent, "english");
        allPercentMap.put(starNamePercent + randomNamePercent + englishNamePercent + dictNamePercent, "dict");
        allPercentMap.put(starNamePercent + randomNamePercent + englishNamePercent + dictNamePercent+xianShengNvShiPercent, "XianShengNvShi");
        //百分之七十根据字典库去查询，百分之三十随机生成
        int chooseTypeRandom = randomAB(0, allPercent);
        List<Integer> collect = new ArrayList<>(allPercentMap.keySet()).stream().sorted().filter(x -> x > chooseTypeRandom).collect(Collectors.toList());
        if ("star".equals(allPercentMap.get(collect.get(0)))) {
            return starName.get(randomAB(0, starName.size()));
        } else if ("english".equals(allPercentMap.get(collect.get(0)))) {
            return englishName.get(randomAB(0, englishName.size()));
        } else if ("dict".equals(allPercentMap.get(collect.get(0)))) {
            return firstName() + dictName.get(randomAB(0, dictName.size()));
        } else if ("random".equals(allPercentMap.get(collect.get(0)))) {
            //百分之20是单字，80是双字
            int isDouble = randomAB(0, 100);
            if (isDouble <= 20) {
                return firstName() + getRandomChar() + getRandomChar();
            } else {
                return firstName() + getRandomChar();
            }
        } else if ("XianShengNvShi".equals(allPercentMap.get(collect.get(0)))) {
            return firstName()+XianShengNvShiList[randomAB(0, XianShengNvShiList.length)];
        } else {
            System.out.println("系统错误");
            return null;
        }
    }

    private static char getRandomChar() {
        return  (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }

    /*
     * 随机返回a和b其中一个数
     */
    private static int randomAB(int a, int b) {
        return (int) ((Math.random() * Math.abs(a - b)) + Math.min(a, b));
    }

    /**
     * 生成姓氏
     *
     * @throws IOException
     */
    private static String firstName() throws IOException {
        Integer integer = TFSorted.get(TFSorted.size() - 1);
        int i = randomAB(0, integer);
        List<Integer> result = TFSorted.stream().filter(x -> x > i).collect(Collectors.toList());
        return firstNameMap.get(result.get(0));
    }

    /**
     * 读取姓氏文件，获取姓氏
     *
     * @return
     * @throws IOException
     */
    private static Map<Integer, String> loadAllFirstName(String path) throws IOException {
        //使用类加载器来加载文件
        InputStream in = NameUtils.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
        //文件读取
        String line = null;
        Map<Integer, String> firstName = new HashMap<Integer, String>();
        //结果集合
        int tfSum = 0;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            //使用空白字符分割
            String[] names = line.split("\\s+");
            firstName.put(tfSum + Integer.parseInt(names[1]), names[0]);
            tfSum += Integer.parseInt(names[1]);
        }
        return firstName;
    }

    /**
     * @return
     * @throws IOException
     * @生成名字
     */
    private static String dictSecondName(boolean male) throws IOException {
        if (male) {
            List<String> names = loadNames("/asserts/男性.txt");
            return names.get(randomAB(0, names.size()));
        } else {
            List<String> names = loadNames("/asserts/女性.txt");
            return names.get(randomAB(0, names.size()));
        }
    }

    /**
     * 读取百家姓文件，获取名字
     *
     * @return
     * @throws IOException
     */
    private static List<String> loadNames(String path) throws IOException {
        InputStream in = NameUtils.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
        //文件读取
        String line = null;
        //结果集合
        List<String> result = new ArrayList<String>(200);
        while ((line = br.readLine()) != null) {
            line = line.trim();
            //使用空白字符分割
            String[] names = line.split("\\s+");
            result.addAll(Arrays.asList(names));
        }
        return result;
    }


}
