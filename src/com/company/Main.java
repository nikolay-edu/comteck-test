package com.company;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3){
            System.out.println("Wrong args number");
            return;
        }

        if (!Files.exists(FileSystems.getDefault().getPath(args[0]))){
            System.out.println("First path doesn't exist");
            return;
        }
        if (!Files.exists(FileSystems.getDefault().getPath(args[1]))){
            System.out.println("Second path doesn't exist");
            return;
        }
        if (Files.exists(FileSystems.getDefault().getPath(args[2]))){
            try {
                Files.list(FileSystems.getDefault().getPath(args[2])).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            System.out.println("Output folder exist, removing all from it");
        }
        else {
            try {
                Files.createDirectory(FileSystems.getDefault().getPath(args[2]));
            } catch (IOException e) {
                System.out.println("Error, while create new dir:\n" + e.toString());
                return;
            }
        }

        final String resultDirPath = FileSystems.getDefault().getPath(args[2]).toAbsolutePath().toString();

        try {
            List<Path> result = Files.list(FileSystems.getDefault().getPath(args[0])).filter(path -> {
                try {
                    return Files.list(FileSystems.getDefault().getPath(args[1]))
                            .noneMatch(path1 -> path.getFileName().equals(path1.getFileName()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }).collect(Collectors.toList());

            result.addAll(
                    Files.list(FileSystems.getDefault().getPath(args[1])).filter(path -> {
                        try{
                            return Files.list(FileSystems.getDefault().getPath(args[0]))
                                    .noneMatch(path1 -> path.getFileName().equals(path1.getFileName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }).collect(Collectors.toList())
            );

            result.forEach(path -> {
                try {
                    Files.copy(path, FileSystems.getDefault().getPath(resultDirPath + "/" + path.getFileName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        System.out.println("All done");
    }
}
