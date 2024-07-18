package com.fiafeng.dynamicClass.utils;

import org.jboss.windup.decompiler.api.DecompilationFailure;
import org.jboss.windup.decompiler.api.DecompilationListener;
import org.jboss.windup.decompiler.api.DecompilationResult;
import org.jboss.windup.decompiler.api.Decompiler;
import org.jboss.windup.decompiler.procyon.ProcyonDecompiler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Procyon 反编译测试
 *
 * @author https://github.com/niumoo
 * @date 2021/05/15
 */
public class ProcyonUtils {
    public static void procyon(String source, String targetPath) {
        long start = System.currentTimeMillis();
        Path outDir = Paths.get(targetPath);
        Path archive = Paths.get(source);
        Decompiler dec = new ProcyonDecompiler();
        DecompilationResult res = dec.decompileArchive(archive, outDir, new DecompilationListener() {
            public void decompilationProcessComplete() {
                System.out.println("decompilationProcessComplete");
            }

            public void decompilationFailed(List<String> inputPath, String message) {
                System.out.println("decompilation Failed");
            }

            public void fileDecompiled(List<String> inputPath, String outputPath) {
            }

            public boolean isCancelled() {
                return false;
            }
        });

        if (!res.getFailures().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed decompilation of ").append(res.getFailures().size()).append(" classes: ");
            for (DecompilationFailure dex : res.getFailures()) {
                sb.append(System.lineSeparator()).append("    ").append(dex.getMessage());
            }
            System.out.println(sb.toString());
        }
        System.out.println("Compilation results: " + res.getDecompiledFiles().size() + " succeeded, " + res.getFailures().size() + " failed.");
        dec.close();
        long end = System.currentTimeMillis();

        System.out.printf("decompiler time: %dms%n", end - start);
    }
}
