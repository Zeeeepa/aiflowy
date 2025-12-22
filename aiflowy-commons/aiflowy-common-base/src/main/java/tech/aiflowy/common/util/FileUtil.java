package tech.aiflowy.common.util;

public class FileUtil {

    public static String calcByte(Long sizeInBytes) {
        if (sizeInBytes == null) {
            return "";
        }

        String sizeFormatted;
        if (sizeInBytes >= 1024 * 1024 * 1024) {
            // Convert to GB
            double sizeInGB = sizeInBytes / (1024.0 * 1024.0 * 1024.0);
            sizeFormatted = String.format("%.2f GB", sizeInGB);
        } else if (sizeInBytes >= 1024 * 1024) {
            // Convert to MB
            double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
            sizeFormatted = String.format("%.2f MB", sizeInMB);
        } else if (sizeInBytes >= 1024) {
            // Convert to KB
            double sizeInKB = sizeInBytes / 1024.0;
            sizeFormatted = String.format("%.2f KB", sizeInKB);
        } else {
            // Keep in bytes
            sizeFormatted = sizeInBytes + " bytes";
        }
        return sizeFormatted;
    }


    public static String getFileTypeByExtension(String fileName) {
        if (fileName.endsWith(".txt")) {
            return "txt";
        } else if (fileName.endsWith(".pdf")) {
            return "pdf";
        } else if (fileName.endsWith(".md")) {
            return "md";
        } else if (fileName.endsWith(".docx")) {
            return "docx";
        } else if (fileName.endsWith(".xlsx")) {
            return "xlsx";
        } else if (fileName.endsWith(".ppt")) {
            return "ppt";
        } else if (fileName.endsWith(".pptx")) {
            return "pptx";
        }
        else {
            return null;
        }
    }
}
