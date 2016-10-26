package com.smona.base.verify;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Logger.init();
        String currFilePath = System.getProperty("user.dir");
        if (args == null) {
            Logger.printReport("参数不能为空");
            return;
        }

        // if (args.length != 2) {
        // Logger.printReport("参数不对");
        // return;
        // }
        //
        // if ("zip".equals(args[0])) {
        // // 打包分类
        // String category = currFilePath + "/" + args[1];
        // zipCategory(category);
        // } else {
        // // 分类文件夹根目录
        // String category = currFilePath + "/" + args[1];
        // verifyCategory(category);
        // }

         String category = currFilePath + "/" + "category";
         zipCategory(category);

//        String category = currFilePath + "/" + "category";
//        verifyCategory(category);
    }

    private static void zipCategory(String category) {
        File file = new File(category);
        File[] categoryFile = file.listFiles();
        ZipFileAction action = new ZipFileAction();
        for (File categoryName : categoryFile) {
            if (categoryName.isDirectory()) {
                try {
                    action.zip(categoryName.getAbsolutePath(), category + "/"
                            + categoryName.getName() + ".zip");
                } catch (Exception e) {
                    Logger.printReport(categoryName.getName() + "压缩失败");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void verifyCategory(String category) {
        boolean verifyName = versifyCategoryName(category);
        if (!verifyName) {
            return;
        }
        Logger.printReport("分类名称及个数OK");

        verifyName = versifyCategoryEveryName(category);
        if (!verifyName) {
            return;
        }
        Logger.printReport("每个分类里面的图片分辨率、大小都OK");
    }

    private static boolean versifyCategoryName(String category) {
        File file = new File(category);
        String[] childFileName = file.list();
        if (childFileName == null) {
            Logger.printReport("没有分类名称");
            return false;
        }
        if (childFileName.length != Resolution.category.length) {
            Logger.printReport("标准分类个数对不上");
            return false;
        }
        boolean matchName;
        for (String categoryName : Resolution.category) {
            matchName = false;
            for (String childName : childFileName) {
                if (categoryName.equals(childName)) {
                    matchName = true;
                    break;
                }
            }
            if (!matchName) {
                Logger.printReport("缺少分类: " + categoryName);
                return false;
            }
        }
        return true;
    }

    private static boolean versifyCategoryEveryName(String category) {
        File file = new File(category);
        File[] childFileName = file.listFiles();
        for (File childName : childFileName) {
            boolean versify = versifyCategoryEveryNameResolution(
                    childName.getAbsolutePath(),
                    Resolution.category407Resolution, false);
            if (!versify) {
                return false;
            }
            versify = versifyCategoryEveryNameResolution(
                    childName.getAbsolutePath(),
                    Resolution.category408Resolution, true);
            if (!versify) {
                return false;
            }
        }
        return true;
    }

    private static boolean versifyCategoryEveryNameResolution(
            String categoryName, String[] standsFileName, boolean isNew) {
        File file = new File(categoryName);
        String[] childFileName = file.list();
        String[] picName;
        boolean matchName;
        for (String resolution : standsFileName) {
            matchName = false;
            for (String childName : childFileName) {
                if (resolution.equals(childName)) {
                    File resolutionFile = new File(categoryName + "/"
                            + resolution);
                    System.out.println("");
                    picName = resolutionFile.list();
                    if (picName == null) {
                    } else if (isNew && picName.length == 1) {

                        // 验证category.jpg的分辨率
                        matchName = "category.jpg".equals(picName[0]);
                        String filePath = categoryName + File.separator
                                + resolution + File.separator + "category.jpg";
                        int[] cor = ValidateImageFormat.getImageWAndH(filePath);
                        System.out.println("File: " + filePath + "; ]" + cor[0]
                                + "," + cor[1] + "],[" + resolution + "]");
                        matchName &= resolution.equals(cor[0] + "x" + cor[1]);

                    } else if (!isNew && picName.length == 2) {
                        // 验证category.jpg的分辨率
                        matchName = "category.jpg".equals(picName[0]);
                        String filePath = categoryName + File.separator
                                + resolution + File.separator + "category.jpg";
                        int[] cor = ValidateImageFormat.getImageWAndH(filePath);
                        System.out.println("File: " + filePath + "; ]" + cor[0]
                                + "," + cor[1] + "],[" + resolution + "]");
                        matchName &= resolution.equals(cor[0] + "x" + cor[1]);

                        // 验证description.jpg的分辨率
                        matchName &= "description.jpg".equals(picName[1]);
                        filePath = categoryName + File.separator + resolution
                                + File.separator + "description.jpg";
                        cor = ValidateImageFormat.getImageWAndH(filePath);
                        System.out.println("File: " + filePath + "; ]" + cor[0]
                                + "," + cor[1] + "],[" + resolution + "]");
                        matchName &= resolution.equals(cor[0] + "x" + cor[1]);
                    }
                    break;
                }
            }
            if (!matchName) {
                Logger.printReport("分类[" + categoryName + "]下的分辨率["
                        + resolution + "]里缺少图片");
            }
        }
        return true;
    }
}
