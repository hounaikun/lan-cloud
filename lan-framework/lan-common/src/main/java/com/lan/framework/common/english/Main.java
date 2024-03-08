package com.lan.framework.common.english;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static File forgetFile = new File("D:\\kaoy\\kaoy\\english\\test\\word\\today.txt");

    public static void main(String[] args) {
        final String path = "D:\\kaoy\\kaoy\\english\\test\\word";
        File directory = new File(path);
        if (!directory.exists()) {
            System.out.println(path + "not exists");
        }

        File[] files = directory.listFiles(((dir, name) -> name.endsWith(".txt")));

        List<String> wordsList = new ArrayList<>();


        try (Scanner inputScanner = new Scanner(System.in)) {
            List<File> finalFileList = new ArrayList<>();

            System.out.print("文件列表：");
            List<String> nameList = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nameList.size(); i++) {
                sb.append(nameList.get(i));
                if (i < nameList.size() - 1) {
                    sb.append(", ");
                }
                if (i != 0 && i % 10 == 0 && i != nameList.size() - 1) {
                    System.out.println(sb);
                    sb = new StringBuilder();
                }
            }
            System.out.print(sb);


            while (true) {
                System.out.print("\n请输入文件名：");
                List<String> lookFileNameList = Arrays.stream(inputScanner.nextLine()
                                .split(", "))
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(lookFileNameList)) {
                    for (File file : files) {
                        for (String lookFileName : lookFileNameList) {
                            int index = lookFileName.indexOf("*.txt");
                            if (index != -1) {
                                if(file.getName().contains(lookFileName.substring(0, index))) {
                                    finalFileList.add(file);
                                }
                            } else {
                                if(file.getName().contains(lookFileName)) {
                                    finalFileList.add(file);
                                }
                            }
                        }
                    }

                    if (CollectionUtils.isEmpty(finalFileList)) {
                        System.out.print("file not exists");
                        continue;
                    }
                } else {
                    finalFileList.addAll(Arrays.asList(files));
                }
                break;
            }

            System.out.print("文件：");
            for (File file : finalFileList) {
                System.out.print(file.getName() + ", ");
                readWordsFromFile(file, wordsList);
            }
            System.out.println();

            System.out.println("\n是否随机：输入单词 'yes' or 任意。。。");
            boolean shuffle = "yes".equals(inputScanner.nextLine());
            // 随机化列表
            if (shuffle) {
                Collections.shuffle(wordsList);
                System.out.println("随机。。。");
            } else {
                System.out.println("非随机。。。");
            }

            System.out.println("\n是否未forget：输入单词 'yes' or 任意。。。");
            boolean nonForget = "yes".equals(inputScanner.nextLine());
            if (nonForget) {
                List<String> forgetLines = readForgetFile().stream()
                        .map(line -> line.substring(0, line.lastIndexOf(" ")))
                        .collect(Collectors.toList());
                wordsList = wordsList.stream()
                        .filter(w -> !forgetLines.contains(w))
                        .collect(Collectors.toList());
            }

            LinkedList<String> queue = new LinkedList<>(wordsList);
            System.out.println("文件中总共有 " + queue.size() + " 个单词!\n");

            int index = 1;
            while (!queue.isEmpty()) {
                String wordPair = queue.poll();
                String[] parts = wordPair.split(" ");
                if (parts.length >= 2) {
                    String word = parts[0];
                    String translation = parts[1];

                    // 输出单词和翻译
                    System.out.print("     word " + index + ": " + word);
                    // 等待用户输入
                    inputScanner.nextLine();
                    System.out.println("translate " + index++ + ": " + translation);
                    String userInput = inputScanner.nextLine();
                    if (userInput.equals("-")) {
                        // 重新将当前单词重新放回队列
                        queue.addFirst(wordPair);
                        if (shuffle) {
                            Collections.shuffle(queue);
                        }
                        index--;
                    } else if (userInput.equals("+") || userInput.equals("_")) {
                        addWordToForgetFile(word, translation, userInput);
                    }
                }
            }
        }
    }

    private static List<String> readForgetFile() {
        List<String> forgetLines = new ArrayList<>();
        try {
            // 读取forget.txt文件内容到内存
            if (forgetFile.exists()) {
                try (BufferedReader forgetReader = new BufferedReader(new FileReader(forgetFile))) {
                    String forgetLine;
                    while ((forgetLine = forgetReader.readLine()) != null) {
                        forgetLines.add(forgetLine);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return forgetLines;
    }

    private static void addWordToForgetFile(String word, String translation, String sign) {
        try {
            List<String> forgetLines = readForgetFile();

            boolean found = false;
            int count = 1;

            // 检查是否有重复的单词
            for (int i = 0; i < forgetLines.size(); i++) {
                String line = forgetLines.get(i);
                if (line.startsWith(word + " ")) {
                    found = true;
                    String[] partsCount = line.split(" ");
                    if (sign.equals("+")) {
                        count = Integer.parseInt(partsCount[2]) + 1;
                    } else if (sign.equals("_")) {
                        count = Integer.parseInt(partsCount[2]) - 1;
                        if (count == 0) {
                            forgetLines.remove(i);
                            break;
                        }
                    }
                    forgetLines.set(i, word + " " + translation + " " + count);
                    break;
                }
            }

            // 如果有重复的单词，在行末加上次数
            if (!found) {
                forgetLines.add(word + " " + translation + " " + count);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(forgetFile))) {
                // 写入forget.txt文件
                for (String newLine : forgetLines) {
                    writer.write(newLine);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void readWordsFromFile(File file, List<String> wordsList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // 读取文件中的单词对到列表中
            while ((line = reader.readLine()) != null) {
                wordsList.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


