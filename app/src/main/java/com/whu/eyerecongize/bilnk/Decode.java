package com.whu.eyerecongize.bilnk;

import java.util.HashMap;
import java.util.Map;

public class Decode {
    private static String tmpText="";
    private static String current_code = "";
    private static String former_code = "";
    private static String output = "";

    private static final double EYE_AR_THRESH = 0.2;
    private static final int EYE_AR_CONSEC_FRAMES = 2;
    private static final int EYE_OPEN_CONSEC_FRAMES = 2;
    private static final int DIV_THRESH = 20;

    private static final int SHORT_THRESH = 4;
    private static final int LONG_THRESH = 15;
    private static final int BLINK_FRE = 30;
    private static final int WIDTH = 1080;
    private static final int OUTPUT_LAST_TIME = 100;


    private static int CLOSED_COUNTER = 0;
    private static int TOTAL = 0;
    private static  int OPEN_COUNTER = 0;
    private static int PARSED = 0;
    private static final int SPACED = 0;
    private static int STATUS = 0;
    private static final int INPUT_MODE = 0;
    private static int FRAME_COUNT = 0;
    private static int OUTPUT_DISP_TIME = 0;
    private static int OUTPUT_MODE = 0;
    private static final int CURRENT_TEMP = 24;



    private static int get_with(String str){
        int result = 0;
        char[] string1 = str.toCharArray();

        for(Character ch:string1){
            if (ch.equals('.')){
                result += 1;
            }
            else if (ch.equals(' ')){
                result += 2;
            }
            else{
                result += 3;
            }
        }
        return result;
    }


    private static String shorten_code(String str){
        int result = 0;
        int i = 0;
        for (char c : str.toCharArray()) {
            if (c == '.') {
                result += 1;
            } else if (c == ' ') {
                result += 2;
            } else {
                result += 3;
            }
            i += 1;
            if (result > 10) {
                break;
            }
        }
        return str.substring(i);

    }


    private static String parse_string(String str){
        String result = "";
        if (!str.isEmpty()) {
            for (char ch : str.toCharArray()) {
                if (ch == '0') {
                    result += ".";
                } else {
                    result += "-";
                }
            }
        }
        return result;

    }

    public static String blinksDetectors(boolean blink) {
        if (OUTPUT_MODE != 0) {
            OUTPUT_DISP_TIME++;
            if (OUTPUT_DISP_TIME > OUTPUT_LAST_TIME) {
                OUTPUT_DISP_TIME = 0;
                OUTPUT_MODE = 0;
                output = "";
            }
        }

        FRAME_COUNT++;
        FRAME_COUNT = FRAME_COUNT % BLINK_FRE;

        if (OPEN_COUNTER >= DIV_THRESH && STATUS == 1 && !output.isEmpty() && !output.endsWith(" ")) {
            output = output + " ";
            System.out.println("输出空格");
        }

        if (OPEN_COUNTER >= DIV_THRESH && PARSED == 0 && STATUS == 1) {
            PARSED = 1;
            if (!current_code.isEmpty()) {
                current_code += " ";
                if (getWidth(current_code) > 15) {
                    current_code = shortenCode(current_code);
                }
            }
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("current_code", current_code);
            userInfo.put("former_code", parseString(former_code));
            userInfo.put("decode", true);
            notifyObserver(userInfo);
            former_code = "";
        }

        if (blink) {
            CLOSED_COUNTER++;
            if (CLOSED_COUNTER >= EYE_AR_CONSEC_FRAMES) {//闭眼门槛
                if (STATUS == 1) {
                    STATUS = 0;
                }
                OPEN_COUNTER = 0;
            }
        } else {
            OPEN_COUNTER++;
            if (OPEN_COUNTER >= EYE_OPEN_CONSEC_FRAMES) {
                if (STATUS == 0) {
                    PARSED = 0;
                    TOTAL++;
                    if (CLOSED_COUNTER < SHORT_THRESH) {
                        current_code = current_code + ".";
                        former_code = former_code + "0";
                    } else {
                        if (CLOSED_COUNTER < LONG_THRESH) {
                            current_code = current_code + "-";
                            former_code = former_code + "1";
                        } else {
                            current_code += "+";
                            former_code = "";
                            if (INPUT_MODE == 1) {
                                if (output.length() > 1) {
                                    output = output.substring(0, output.length() - 1);
                                } else {
                                    output = "";
                                }
                            }
                        }
                    }
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("current_code", current_code);
                    userInfo.put("former_code", parse_string(former_code));
                    userInfo.put("decode", false);
                }
                STATUS = 1;
                CLOSED_COUNTER = 0;
            }
        }

        StringBuilder tmp = new StringBuilder();
        tmp.append("current_code").append(current_code).append("\n");
        tmp.append("INPUT_MODE: ").append(INPUT_MODE).append("\n");
        tmp.append("OUTPUT_MODE: ").append(OUTPUT_MODE).append("\n");

        if (OUTPUT_MODE == 1 || OUTPUT_MODE == 2) {
            tmp.append("输出: ").append(output).append("\n");
        } else if (OUTPUT_MODE == 3) {
            tmp.append("向儿子发送呼唤信息\n");
            tmp.append("输出: ").append(output).append("\n");
        }

        if (INPUT_MODE > 0) {
            tmp.append(parse_string(former_code)).append("\n");
        }

        if (INPUT_MODE == 1) {
            tmp.append("输入模式\n");
            tmp.append("连眨六次（......）退出\n");
        } else if (INPUT_MODE == 0) {
            tmp.append("锁定模式\n");
            tmp.append("连眨四次（....）进入输入模式\n");
            tmp.append("短闭眼三次（---）进入紧急模式\n");
            tmp.append("眨二闭一（..-）进入呼唤模式\n");
            tmp.append("眨闭眨闭（.-.-）进入娱乐模式\n");
            tmp.append("眨闭（.-.-）进入空调模式\n");
        } else if (INPUT_MODE == 4) {
            tmp.append("功能选择\n");
            tmp.append("连眨四次（....）回主界面\n");
        }

        if (INPUT_MODE == 1 && OUTPUT_MODE < 2) {
            tmp.append("需要水：-.-\n");
            tmp.append("需要食物：..-.\n");
            tmp.append("需要小便：-..-\n");
            tmp.append("需要大便：.--.\n");
            tmp.append("是的：...\n");
            tmp.append("不是：.-.\n");
        } else if (INPUT_MODE == 2) {
            tmp.append("是否开启紧急呼救？\n");
            tmp.append("是：眨两次\n");
            tmp.append("不是：其他操作\n");
        } else if (INPUT_MODE == 3 && OUTPUT_MODE < 2) {
            tmp.append("请选择需要发送的备注\n");
            tmp.append("退出：其他\n");
            tmp.append("无备注：..-\n");
            tmp.append("需要水：-.-\n");
            tmp.append("需要食物：..-.\n");
            tmp.append("需要小便：-..-\n");
            tmp.append("需要大便：.--.\n");
            tmp.append("我好冷：...\n");
            tmp.append("我好热：.-.\n");
        } else if (INPUT_MODE == 4) {
            tmp.append("看视频：.-.\n");
            tmp.append("听音乐：--\n");
            tmp.append("看小说：-.\n");
            tmp.append("查看信息：--.\n");
            tmp.append("写文字：-.-\n");
        } else if (INPUT_MODE == 5) {
            tmp.append("当前空调温度：\n");
            tmp.append("调高空调温度：.-.\n");
            tmp.append("调低空调温度：--\n");
            tmp.append("退出：...\n");
        }

        return tmp.toString();
    }
}
