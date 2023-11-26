package vknue.mahjong.utilities;

import javafx.scene.control.Alert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeneralUtils {

    private GeneralUtils() {
    }

    public static void showMessage(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static List<String> getDirectoryFileNames(String directoryPath){
        File directory = new File(directoryPath);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                List<String> fileNamesWithoutExtension = new ArrayList<>();
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        int lastDotIndex = fileName.lastIndexOf('.');
                        if (lastDotIndex > 0) { // Check if the file has an extension
                            String fileNameWithoutExtension = fileName.substring(0, lastDotIndex);
                            fileNamesWithoutExtension.add(fileNameWithoutExtension);
                        } else {
                            fileNamesWithoutExtension.add(fileName); // File has no extension
                        }
                    }
                }
                String[] fileNamesArray = fileNamesWithoutExtension.toArray(new String[0]);

                return List.of(fileNamesArray);
            }
        }
        return null;
    }

}
